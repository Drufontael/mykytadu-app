package br.com.mykytadu.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val SEARCH_DEBOUNCE_MILLIS = 800L
internal const val SEARCH_AUTOMATIC_MIN_LENGTH = 3
internal const val SEARCH_RATE_LIMIT_COOLDOWN_SECONDS = 15
internal const val SEARCH_PAGE_SIZE = 20

class SearchViewModel internal constructor(
    private val animeRepository: AnimeRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var generation = 0L
    private var searchJob: Job? = null

    fun onQueryChange(queryText: String) {
        val normalizedQuery = queryText.trim()
        val current = _uiState.value

        if (normalizedQuery == current.normalizedQuery.orEmpty()) {
            _uiState.update { it.copy(queryText = queryText) }
            return
        }

        if (current.content is SearchContent.RateLimitCooldown && normalizedQuery.isNotEmpty()) {
            _uiState.value = current.copy(
                queryText = queryText,
                normalizedQuery = normalizedQuery,
            )
            return
        }

        val currentGeneration = invalidateCurrentExecution()
        if (normalizedQuery.isEmpty()) {
            _uiState.value = SearchUiState(queryText = queryText)
            return
        }

        if (normalizedQuery.length < SEARCH_AUTOMATIC_MIN_LENGTH) {
            _uiState.value = SearchUiState(
                queryText = queryText,
                normalizedQuery = normalizedQuery,
            )
            return
        }

        _uiState.value = SearchUiState(
            queryText = queryText,
            normalizedQuery = normalizedQuery,
            content = SearchContent.Debouncing,
        )
        searchJob = viewModelScope.launch(dispatcher) {
            delay(SEARCH_DEBOUNCE_MILLIS)
            executeSearch(normalizedQuery, currentGeneration)
        }
    }

    fun submit() {
        val state = _uiState.value
        val normalizedQuery = state.queryText.trim()

        if (normalizedQuery.isEmpty()) {
            onQueryChange(state.queryText)
            return
        }
        val canSubmit = state.content is SearchContent.Debouncing ||
                state.content is SearchContent.Initial &&
                normalizedQuery.length < SEARCH_AUTOMATIC_MIN_LENGTH
        if (normalizedQuery != state.normalizedQuery || !canSubmit) {
            return
        }

        val currentGeneration = invalidateCurrentExecution()
        _uiState.value = state.copy(content = SearchContent.Loading)
        searchJob = viewModelScope.launch(dispatcher) {
            executeSearch(normalizedQuery, currentGeneration)
        }
    }

    fun clearQuery() {
        onQueryChange("")
    }

    fun retry() {
        val state = _uiState.value
        val failedQuery = state.normalizedQuery ?: return

        if (state.content !is SearchContent.Failure || state.queryText.trim() != failedQuery) {
            return
        }

        val currentGeneration = invalidateCurrentExecution()
        _uiState.value = state.copy(content = SearchContent.Loading)
        searchJob = viewModelScope.launch(dispatcher) {
            executeSearch(failedQuery, currentGeneration)
        }
    }

    private fun invalidateCurrentExecution(): Long {
        generation += 1
        searchJob?.cancel()
        searchJob = null
        return generation
    }

    private suspend fun executeSearch(
        normalizedQuery: String,
        executionGeneration: Long,
        allowAutomaticRateLimitRetry: Boolean = true,
    ) {
        if (!isCurrent(normalizedQuery, executionGeneration)) return

        _uiState.update { it.copy(content = SearchContent.Loading) }
        val result = animeRepository.searchAnime(
            query = normalizedQuery,
            page = 1,
            perPage = SEARCH_PAGE_SIZE,
        )

        currentCoroutineContext().ensureActive()
        if (!isCurrent(normalizedQuery, executionGeneration)) return

        if (
            result is RepositoryResult.Failure &&
            result.reason == RepositoryFailure.RateLimited &&
            allowAutomaticRateLimitRetry
        ) {
            awaitRateLimitCooldown(executionGeneration)
            return
        }

        _uiState.update { state -> state.copy(content = result.toSearchContent()) }
    }

    private suspend fun awaitRateLimitCooldown(executionGeneration: Long) {
        for (remainingSeconds in SEARCH_RATE_LIMIT_COOLDOWN_SECONDS downTo 1) {
            if (generation != executionGeneration) return
            _uiState.update { state ->
                state.copy(content = SearchContent.RateLimitCooldown(remainingSeconds))
            }
            delay(1_000L)
        }

        currentCoroutineContext().ensureActive()
        if (generation != executionGeneration) return

        val latestQuery = _uiState.value.queryText.trim()
        if (latestQuery.isEmpty()) {
            _uiState.value = SearchUiState()
            return
        }

        _uiState.update { state ->
            state.copy(
                normalizedQuery = latestQuery,
                content = SearchContent.Loading,
            )
        }
        executeSearch(
            normalizedQuery = latestQuery,
            executionGeneration = executionGeneration,
            allowAutomaticRateLimitRetry = false,
        )
    }

    private fun RepositoryResult<PagedResult<AnimeSummary>>.toSearchContent(): SearchContent =
        when (this) {
            is RepositoryResult.Success -> {
                if (value.items.isEmpty()) SearchContent.Empty else SearchContent.Results(value.items)
            }

            is RepositoryResult.Failure -> SearchContent.Failure(reason)
        }

    private fun isCurrent(
        normalizedQuery: String,
        executionGeneration: Long,
    ): Boolean =
        generation == executionGeneration &&
                _uiState.value.normalizedQuery == normalizedQuery
}
