package br.com.mykytadu.features.search

import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.result.RepositoryFailure

data class SearchUiState(
    val queryText: String = "",
    val normalizedQuery: String? = null,
    val content: SearchContent = SearchContent.Initial,
)

sealed interface SearchContent {
    data object Initial : SearchContent
    data object Debouncing : SearchContent
    data object Loading : SearchContent

    data class RateLimitCooldown(
        val remainingSeconds: Int,
    ) : SearchContent {
        init {
            require(remainingSeconds > 0) { "Remaining seconds must be positive." }
        }
    }

    data class Results(
        val items: List<AnimeSummary>,
    ) : SearchContent

    data object Empty : SearchContent

    data class Failure(
        val reason: RepositoryFailure,
    ) : SearchContent
}
