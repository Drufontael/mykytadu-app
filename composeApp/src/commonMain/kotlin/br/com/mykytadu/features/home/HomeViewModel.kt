package br.com.mykytadu.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.repository.LibraryRepository
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela tela Home híbrida.
 * Combina os registros locais da biblioteca do usuário com animes em alta obtidos da AniList,
 * isolando falhas por seção para garantir alta disponibilidade mesmo offline.
 */
class HomeViewModel(
    private val libraryRepository: LibraryRepository,
    private val animeRepository: AnimeRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    private val timeProvider: () -> Long = { kotlin.time.Clock.System.now().toEpochMilliseconds() },
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val currentEntries = MutableStateFlow<List<LibraryEntry>?>(null)
    private val libraryError = MutableStateFlow<String?>(null)
    private val metadataCache = MutableStateFlow<Map<String, Triple<String, String?, Int?>>>(emptyMap())
    private val trendingSectionState = MutableStateFlow<HomeSectionState<List<HomeTrendingItemUiState>>>(HomeSectionState.Loading)
    private val isRefreshingState = MutableStateFlow(false)

    init {
        observeLibraryFlow()
        combineSections()
        fetchTrending()
    }

    private fun observeLibraryFlow() {
        viewModelScope.launch(dispatcher) {
            libraryRepository.observeAllEntries()
                .catch { cause ->
                    libraryError.value = cause.message ?: "Erro ao carregar biblioteca"
                }
                .collectLatest { entries ->
                    libraryError.value = null
                    currentEntries.value = entries
                    fetchMissingMetadata(entries)
                }
        }
    }

    private fun combineSections() {
        viewModelScope.launch(dispatcher) {
            combine(
                currentEntries,
                libraryError,
                metadataCache,
                trendingSectionState,
                isRefreshingState,
            ) { entries, error, cache, trending, refreshing ->
                val continueWatching: HomeSectionState<List<HomeContinueWatchingItemUiState>> = when {
                    error != null -> HomeSectionState.Failure(message = error)
                    entries == null -> HomeSectionState.Loading
                    else -> {
                        val watching = entries.filter { it.status == LibraryStatus.WATCHING }
                            .sortedByDescending { it.updatedAtEpochMillis }
                        if (watching.isEmpty()) {
                            HomeSectionState.Empty()
                        } else {
                            val items = watching.map { entry ->
                                val meta = cache[entry.animeId.value]
                                val total = entry.progress.total ?: meta?.third
                                val ratio = total?.takeIf { it > 0 }?.let { (entry.progress.current.toFloat() / it).coerceIn(0f, 1f) }
                                HomeContinueWatchingItemUiState(
                                    animeId = entry.animeId,
                                    title = meta?.first ?: "Anime #${entry.animeId.value}",
                                    coverUrl = meta?.second,
                                    progressCurrent = entry.progress.current,
                                    progressTotal = total,
                                    progressRatio = ratio ?: entry.progress.completionRatio,
                                    updatedAtEpochMillis = entry.updatedAtEpochMillis,
                                )
                            }
                            HomeSectionState.Success(items)
                        }
                    }
                }

                val recent: HomeSectionState<List<HomeRecentItemUiState>> = when {
                    error != null -> HomeSectionState.Failure(message = error)
                    entries == null -> HomeSectionState.Loading
                    entries.isEmpty() -> HomeSectionState.Empty()
                    else -> {
                        val sorted = entries.sortedByDescending { it.updatedAtEpochMillis }.take(RECENT_ITEMS_LIMIT)
                        val items = sorted.map { entry ->
                            val meta = cache[entry.animeId.value]
                            val total = entry.progress.total ?: meta?.third
                            HomeRecentItemUiState(
                                animeId = entry.animeId,
                                title = meta?.first ?: "Anime #${entry.animeId.value}",
                                coverUrl = meta?.second,
                                status = entry.status,
                                progressCurrent = entry.progress.current,
                                progressTotal = total,
                                updatedAtEpochMillis = entry.updatedAtEpochMillis,
                            )
                        }
                        HomeSectionState.Success(items)
                    }
                }

                HomeUiState(
                    continueWatchingSection = continueWatching,
                    recentSection = recent,
                    trendingSection = trending,
                    isRefreshing = refreshing,
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
        }
    }

    private fun fetchTrending() {
        viewModelScope.launch(dispatcher) {
            trendingSectionState.value = HomeSectionState.Loading
            when (val result = animeRepository.getTrendingAnime(page = 1, perPage = TRENDING_PAGE_SIZE)) {
                is RepositoryResult.Success -> {
                    val paged = result.value
                    if (paged.items.isEmpty()) {
                        trendingSectionState.value = HomeSectionState.Empty()
                    } else {
                        val mapped = paged.items.map { summary ->
                            val title = sequenceOf(
                                summary.titles.english,
                                summary.titles.romaji,
                                summary.titles.native,
                            ).plus(summary.titles.synonyms.asSequence())
                                .mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }
                                .firstOrNull() ?: "Anime #${summary.id.value}"

                            val cover = summary.images.coverLarge ?: summary.images.coverExtraLarge

                            HomeTrendingItemUiState(
                                animeId = summary.id,
                                title = title,
                                coverUrl = cover,
                                format = summary.format,
                                averageScore = summary.averageScore,
                                episodes = summary.episodes,
                            )
                        }
                        trendingSectionState.value = HomeSectionState.Success(mapped)
                    }
                }
                is RepositoryResult.Failure -> {
                    trendingSectionState.value = HomeSectionState.Failure(
                        reason = result.reason,
                        message = result.cause?.message,
                    )
                }
            }
        }
    }

    private fun fetchMissingMetadata(entries: List<LibraryEntry>) {
        val currentCache = metadataCache.value
        for (entry in entries) {
            val key = entry.animeId.value
            if (!currentCache.containsKey(key)) {
                viewModelScope.launch(dispatcher) {
                    when (val result = animeRepository.getAnimeDetails(entry.animeId)) {
                        is RepositoryResult.Success -> {
                            val details = result.value
                            val title = sequenceOf(
                                details.titles.english,
                                details.titles.romaji,
                                details.titles.native,
                            ).plus(details.titles.synonyms.asSequence())
                                .mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }
                                .firstOrNull() ?: "Anime #$key"
                            val cover = details.images.coverExtraLarge ?: details.images.coverLarge
                            val episodes = details.episodes

                            metadataCache.value = metadataCache.value + (key to Triple(title, cover, episodes))
                        }
                        is RepositoryResult.Failure -> {
                            if (!metadataCache.value.containsKey(key)) {
                                metadataCache.value = metadataCache.value + (key to Triple("Anime #$key", null, null))
                            }
                        }
                    }
                }
            }
        }
    }

    fun incrementProgress(animeId: CatalogAnimeId) {
        val entries = currentEntries.value ?: return
        val entry = entries.firstOrNull { it.animeId == animeId } ?: return
        val meta = metadataCache.value[animeId.value]
        val total = entry.progress.total ?: meta?.third
        if (total != null && entry.progress.current >= total) {
            return
        }
        val now = timeProvider()
        val newProgress = entry.progress.increment()
        val updated = entry.withProgress(newProgress, now)
        viewModelScope.launch(dispatcher) {
            libraryRepository.saveEntry(updated)
        }
    }

    fun retryTrending() {
        fetchTrending()
    }

    fun refresh() {
        viewModelScope.launch(dispatcher) {
            isRefreshingState.value = true
            fetchTrending()
            isRefreshingState.value = false
        }
    }

    companion object {
        const val RECENT_ITEMS_LIMIT = 10
        const val TRENDING_PAGE_SIZE = 10
    }
}
