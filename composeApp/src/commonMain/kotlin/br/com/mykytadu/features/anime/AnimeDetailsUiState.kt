package br.com.mykytadu.features.anime

import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.result.RepositoryFailure

sealed interface AnimeDetailsUiState {
    data object Loading : AnimeDetailsUiState

    data class Content(
        val details: AnimeDetails,
    ) : AnimeDetailsUiState

    data class Failure(
        val reason: RepositoryFailure,
    ) : AnimeDetailsUiState
}
