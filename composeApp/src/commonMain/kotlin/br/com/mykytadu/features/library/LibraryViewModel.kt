package br.com.mykytadu.features.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.model.resolveDisplayTitle
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val libraryRepository: LibraryRepository,
    private val animeRepository: AnimeRepository? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    private val timeProvider: () -> Long = { kotlin.time.Clock.System.now().toEpochMilliseconds() },
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private val selectedFilter = MutableStateFlow(LibraryFilter.ALL)
    private val selectedSort = MutableStateFlow(LibrarySort.RECENT_UPDATED)
    private val currentEntries = MutableStateFlow<List<LibraryEntry>>(emptyList())
    private val metadataCache = MutableStateFlow<Map<String, Pair<String, String?>>>(emptyMap())

    init {
        observeLibraryFlow()
        combineState()
    }

    private fun observeLibraryFlow() {
        viewModelScope.launch(dispatcher) {
            libraryRepository.observeAllEntries()
                .catch { cause ->
                    _uiState.value = LibraryUiState.Failure(cause.message)
                }
                .collectLatest { entries ->
                    currentEntries.value = entries
                    fetchMissingMetadata(entries)
                }
        }
    }

    private fun combineState() {
        viewModelScope.launch(dispatcher) {
            combine(
                currentEntries,
                selectedFilter,
                selectedSort,
                metadataCache,
            ) { entries, filter, sort, cache ->
                val counts = buildStatusCounts(entries)
                val mappedItems = entries.map { entry ->
                    val metadata = cache[entry.animeId.value]
                    LibraryItemUiState(
                        id = entry.id,
                        animeId = entry.animeId,
                        title = metadata?.first ?: "Anime #${entry.animeId.value}",
                        coverUrl = metadata?.second,
                        status = entry.status,
                        progressCurrent = entry.progress.current,
                        progressTotal = entry.progress.total,
                        progressRatio = entry.progress.completionRatio,
                        isFavorite = entry.isFavorite,
                        rating = entry.rating?.value,
                        notes = entry.notes,
                        updatedAtEpochMillis = entry.updatedAtEpochMillis,
                    )
                }

                val filtered = mappedItems.filter { item -> filter.matches(item.status) }
                val sorted = when (sort) {
                    LibrarySort.RECENT_UPDATED -> filtered.sortedByDescending { it.updatedAtEpochMillis }
                    LibrarySort.TITLE -> filtered.sortedBy { it.title.lowercase() }
                    LibrarySort.RATING -> filtered.sortedByDescending { it.rating ?: -1 }
                    LibrarySort.PROGRESS -> filtered.sortedByDescending { it.progressRatio ?: -1f }
                }

                LibraryUiState.Content(
                    items = sorted,
                    filter = filter,
                    sort = sort,
                    statusCounts = counts,
                    totalEntriesCount = entries.size,
                )
            }.collectLatest { contentState ->
                _uiState.value = contentState
            }
        }
    }

    private fun buildStatusCounts(entries: List<LibraryEntry>): Map<LibraryFilter, Int> {
        val counts = mutableMapOf<LibraryFilter, Int>()
        counts[LibraryFilter.ALL] = entries.size
        for (filter in LibraryFilter.entries) {
            if (filter != LibraryFilter.ALL) {
                counts[filter] = entries.count { filter.matches(it.status) }
            }
        }
        return counts
    }

    private fun fetchMissingMetadata(entries: List<LibraryEntry>) {
        val repo = animeRepository ?: return
        val currentCache = metadataCache.value

        for (entry in entries) {
            val key = entry.animeId.value
            if (!currentCache.containsKey(key)) {
                viewModelScope.launch(dispatcher) {
                    when (val result = repo.getAnimeDetails(entry.animeId)) {
                        is RepositoryResult.Success -> {
                            val details = result.value
                            val title = details.titles.resolveDisplayTitle(unavailableTitle = "Anime #${key}")
                            val cover = details.images.coverExtraLarge ?: details.images.coverLarge

                            metadataCache.value = metadataCache.value + (key to Pair(title, cover))
                        }
                        is RepositoryResult.Failure -> {
                            // Se falhar (ex: offline), registrar com fallback amigável
                            if (!metadataCache.value.containsKey(key)) {
                                metadataCache.value = metadataCache.value + (key to Pair("Anime #${key}", null))
                            }
                        }
                    }
                }
            }
        }
    }

    fun retry() {
        observeLibraryFlow()
    }

    fun setFilter(filter: LibraryFilter) {
        selectedFilter.value = filter
    }

    fun onFilterSelected(filter: LibraryFilter) = setFilter(filter)

    fun setSort(sort: LibrarySort) {
        selectedSort.value = sort
    }

    fun onSortSelected(sort: LibrarySort) = setSort(sort)

    fun incrementProgress(entryId: LibraryEntryId) {
        val entry = findEntry(entryId) ?: return
        val newProgress = entry.progress.increment()
        val now = timeProvider()
        val updated = entry.withProgress(newProgress, now)
        saveEntry(updated)
    }

    fun incrementProgress(animeId: CatalogAnimeId) {
        val entry = currentEntries.value.firstOrNull { it.animeId == animeId } ?: return
        incrementProgress(entry.id)
    }

    fun decrementProgress(entryId: LibraryEntryId) {
        val entry = findEntry(entryId) ?: return
        val newProgress = entry.progress.decrement()
        val now = timeProvider()
        val updated = entry.withProgress(newProgress, now)
        saveEntry(updated)
    }

    fun toggleFavorite(entryId: LibraryEntryId) {
        val entry = findEntry(entryId) ?: return
        val now = timeProvider()
        val updated = entry.withFavorite(!entry.isFavorite, now)
        saveEntry(updated)
    }

    fun updateStatus(entryId: LibraryEntryId, newStatus: LibraryStatus) {
        val entry = findEntry(entryId) ?: return
        val now = timeProvider()
        val updated = entry.withStatus(newStatus, now)
        saveEntry(updated)
    }

    fun updateRating(entryId: LibraryEntryId, newRating: Int?) {
        val entry = findEntry(entryId) ?: return
        val now = timeProvider()
        val ratingObj = newRating?.let { LibraryRating(it) }
        val updated = entry.withRating(ratingObj, now)
        saveEntry(updated)
    }

    fun updateNotes(entryId: LibraryEntryId, newNotes: String?) {
        val entry = findEntry(entryId) ?: return
        val now = timeProvider()
        val updated = entry.withNotes(newNotes, now)
        saveEntry(updated)
    }

    fun updateEntryDetails(
        entryId: LibraryEntryId,
        newStatus: LibraryStatus,
        newProgress: Int,
        newRating: Int?,
        newNotes: String?,
        isFavorite: Boolean,
    ) {
        val entry = findEntry(entryId) ?: return
        val now = timeProvider()
        val ratingObj = newRating?.let { LibraryRating(it) }
        val updated = entry
            .withStatus(newStatus, now)
            .withProgress(entry.progress.withCurrent(newProgress), now)
            .withRating(ratingObj, now)
            .withNotes(newNotes, now)
            .withFavorite(isFavorite, now)
        saveEntry(updated)
    }

    fun deleteEntry(entryId: LibraryEntryId) {
        currentEntries.update { list -> list.filterNot { it.id == entryId } }
        viewModelScope.launch(dispatcher) {
            libraryRepository.deleteEntry(entryId)
        }
    }

    private fun findEntry(id: LibraryEntryId): LibraryEntry? =
        currentEntries.value.firstOrNull { it.id == id }

    private fun saveEntry(entry: LibraryEntry) {
        currentEntries.update { list ->
            val index = list.indexOfFirst { it.id == entry.id }
            if (index >= 0) {
                list.toMutableList().apply { set(index, entry) }
            } else {
                list + entry
            }
        }
        viewModelScope.launch(dispatcher) {
            libraryRepository.saveEntry(entry)
        }
    }
}
