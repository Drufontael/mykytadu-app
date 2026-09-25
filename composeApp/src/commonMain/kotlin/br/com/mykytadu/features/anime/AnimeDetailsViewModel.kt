package br.com.mykytadu.features.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnimeDetailsViewModel internal constructor(
    private val id: CatalogAnimeId,
    private val animeRepository: AnimeRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeDetailsUiState>(AnimeDetailsUiState.Loading)
    val uiState: StateFlow<AnimeDetailsUiState> = _uiState.asStateFlow()

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
}
