package br.com.mykytadu.features.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.repository.LibraryRepository
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnimeDetailsViewModel internal constructor(
    private val id: CatalogAnimeId,
    private val animeRepository: AnimeRepository,
    private val libraryRepository: LibraryRepository? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    private val timeProvider: () -> Long = { kotlin.time.Clock.System.now().toEpochMilliseconds() },
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeDetailsUiState>(AnimeDetailsUiState.Loading)
    val uiState: StateFlow<AnimeDetailsUiState> = _uiState.asStateFlow()

    val libraryEntry: StateFlow<LibraryEntry?> = libraryRepository?.observeEntryByAnimeId(id)
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        ?: MutableStateFlow(null)

    init {
        load()
    }

    fun retry() {
        if (_uiState.value !is AnimeDetailsUiState.Failure) return
        load()
    }

    private fun load() {
        _uiState.value = AnimeDetailsUiState.Loading
        viewModelScope.launch(dispatcher) {
            _uiState.value = when (val result = animeRepository.getAnimeDetails(id)) {
                is RepositoryResult.Success -> AnimeDetailsUiState.Content(result.value)
                is RepositoryResult.Failure -> AnimeDetailsUiState.Failure(result.reason)
            }
        }
    }

    fun addToLibrary(
        status: LibraryStatus = LibraryStatus.PLANNING,
        totalEpisodes: Int? = null,
    ) {
        val repo = libraryRepository ?: return
        val now = timeProvider()
        val entry = LibraryEntry(
            id = LibraryEntryId("lib_${id.value}"),
            animeId = id,
            status = status,
            progress = LibraryProgress(current = 0, total = totalEpisodes),
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now,
        )
        viewModelScope.launch(dispatcher) {
            repo.saveEntry(entry)
        }
    }

    fun incrementProgress() {
        val repo = libraryRepository ?: return
        val current = libraryEntry.value ?: return
        val now = timeProvider()
        val updated = current.withProgress(current.progress.increment(), now)
        viewModelScope.launch(dispatcher) {
            repo.saveEntry(updated)
        }
    }

    fun updateLibraryEntry(
        status: LibraryStatus,
        progress: Int,
        rating: Int?,
        notes: String?,
        isFavorite: Boolean,
    ) {
        val repo = libraryRepository ?: return
        val current = libraryEntry.value ?: return
        val now = timeProvider()
        val updated = current
            .withStatus(status, now)
            .withProgress(current.progress.withCurrent(progress), now)
            .withRating(rating?.let { LibraryRating(it) }, now)
            .withNotes(notes, now)
            .withFavorite(isFavorite, now)
        viewModelScope.launch(dispatcher) {
            repo.saveEntry(updated)
        }
    }

    fun removeFromLibrary() {
        val repo = libraryRepository ?: return
        val current = libraryEntry.value ?: return
        viewModelScope.launch(dispatcher) {
            repo.deleteEntry(current.id)
        }
    }
}
