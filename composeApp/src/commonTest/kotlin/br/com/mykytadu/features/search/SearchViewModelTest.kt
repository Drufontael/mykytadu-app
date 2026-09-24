package br.com.mykytadu.features.search

import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PageInfo
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @Test
    fun `estado inicial e consulta vazia nao chamam repository`() = runTest {
        val repository = ControllableAnimeRepository()
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("   ")
        advanceTimeBy(SEARCH_DEBOUNCE_MILLIS)
        runCurrent()

        assertEquals("   ", viewModel.uiState.value.queryText)
        assertEquals(null, viewModel.uiState.value.normalizedQuery)
        assertEquals(SearchContent.Initial, viewModel.uiState.value.content)
        assertTrue(repository.calls.isEmpty())
    }

    @Test
    fun `debounce preserva texto normaliza consulta e usa primeira pagina`() = runTest {
        val repository = ControllableAnimeRepository()
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("  Naruto  ")

        assertEquals("  Naruto  ", viewModel.uiState.value.queryText)
        assertEquals("Naruto", viewModel.uiState.value.normalizedQuery)
        assertEquals(SearchContent.Debouncing, viewModel.uiState.value.content)

        advanceTimeBy(SEARCH_DEBOUNCE_MILLIS - 1)
        runCurrent()
        assertTrue(repository.calls.isEmpty())

        advanceTimeBy(1)
        runCurrent()

        assertEquals(listOf(SearchCall("Naruto", 1, SEARCH_PAGE_SIZE)), repository.calls)
        assertEquals(SearchContent.Empty, viewModel.uiState.value.content)
    }

    @Test
    fun `consulta curta aguarda submissao e nao sobrecarrega pesquisa automatica`() = runTest {
        val repository = ControllableAnimeRepository()
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("N")
        advanceTimeBy(SEARCH_DEBOUNCE_MILLIS)
        viewModel.onQueryChange("Na")
        advanceTimeBy(SEARCH_DEBOUNCE_MILLIS)
        runCurrent()

        assertEquals("Na", viewModel.uiState.value.normalizedQuery)
        assertEquals(SearchContent.Initial, viewModel.uiState.value.content)
        assertTrue(repository.calls.isEmpty())

        viewModel.submit()
        runCurrent()

        assertEquals(listOf(SearchCall("Na", 1, SEARCH_PAGE_SIZE)), repository.calls)
        assertEquals(SearchContent.Empty, viewModel.uiState.value.content)
    }

    @Test
    fun `submissao executa imediatamente e cancela chamada posterior do debounce`() = runTest {
        val repository = ControllableAnimeRepository()
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()
        advanceTimeBy(SEARCH_DEBOUNCE_MILLIS)
        runCurrent()
        viewModel.submit()
        runCurrent()

        assertEquals(1, repository.calls.size)
    }

    @Test
    fun `alteracao equivalente nao reinicia debounce`() = runTest {
        val repository = ControllableAnimeRepository()
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange(" Naruto")
        val elapsedBeforeEquivalentChange = SEARCH_DEBOUNCE_MILLIS / 2
        advanceTimeBy(elapsedBeforeEquivalentChange)
        viewModel.onQueryChange("Naruto ")
        advanceTimeBy(
            SEARCH_DEBOUNCE_MILLIS - elapsedBeforeEquivalentChange - 1,
        )
        runCurrent()
        assertTrue(repository.calls.isEmpty())

        advanceTimeBy(1)
        runCurrent()

        assertEquals("Naruto ", viewModel.uiState.value.queryText)
        assertEquals(1, repository.calls.size)
    }

    @Test
    fun `nova consulta invalida resultado anterior durante novo debounce`() = runTest {
        val oldResponse = CompletableDeferred<RepositoryResult<PagedResult<AnimeSummary>>>()
        val repository = ControllableAnimeRepository(ignoreCancellation = true) { call ->
            if (call.query == "Naruto") oldResponse.await() else emptyResult()
        }
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()
        viewModel.onQueryChange("Bleach")

        assertEquals(SearchContent.Debouncing, viewModel.uiState.value.content)
        assertEquals("Bleach", viewModel.uiState.value.normalizedQuery)

        oldResponse.complete(successResult(anime(20, "Naruto")))
        runCurrent()

        assertEquals(SearchContent.Debouncing, viewModel.uiState.value.content)
        viewModel.clearQuery()
    }

    @Test
    fun `limpar durante debounce cancela chamada e retorna ao inicial`() = runTest {
        val repository = ControllableAnimeRepository()
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.clearQuery()
        advanceTimeBy(SEARCH_DEBOUNCE_MILLIS)
        runCurrent()

        assertEquals(SearchUiState(), viewModel.uiState.value)
        assertTrue(repository.calls.isEmpty())
    }

    @Test
    fun `limpar durante requisicao preserva cancelamento e ignora resposta`() = runTest {
        val response = CompletableDeferred<RepositoryResult<PagedResult<AnimeSummary>>>()
        val repository = ControllableAnimeRepository { response.await() }
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()
        viewModel.clearQuery()
        response.complete(successResult(anime(20, "Naruto")))
        runCurrent()

        assertEquals(SearchUiState(), viewModel.uiState.value)
        assertEquals(1, repository.cancellationCount)
    }

    @Test
    fun `sucesso com itens publica resultados`() = runTest {
        val expected = anime(20, "Naruto")
        val repository = ControllableAnimeRepository { successResult(expected) }
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()

        val results = assertIs<SearchContent.Results>(viewModel.uiState.value.content)
        assertEquals(listOf(expected), results.items)
    }

    @Test
    fun `carregar proxima pagina usa pageInfo e acumula resultados`() = runTest {
        val firstPage = anime(20, "Naruto")
        val secondPage = anime(21, "Naruto Shippuden")
        val repository = ControllableAnimeRepository { call ->
            if (call.page == 1) {
                successPage(1, true, firstPage)
            } else {
                successPage(2, false, firstPage, secondPage, secondPage)
            }
        }
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()
        viewModel.loadNextPage()
        runCurrent()

        assertEquals(
            listOf(SearchCall("Naruto", 1, SEARCH_PAGE_SIZE), SearchCall("Naruto", 2, SEARCH_PAGE_SIZE)),
            repository.calls,
        )
        val results = assertIs<SearchContent.Results>(viewModel.uiState.value.content)
        assertEquals(listOf(firstPage, secondPage), results.items)
        assertEquals(2, results.pageInfo.currentPage)
        assertEquals(false, results.pageInfo.hasNextPage)
        assertEquals(SearchPaginationState.Idle, results.pagination)
    }

    @Test
    fun `carregar proxima pagina respeita hasNextPage`() = runTest {
        val repository = ControllableAnimeRepository { successResult(anime(20, "Naruto")) }
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()
        viewModel.loadNextPage()
        runCurrent()

        assertEquals(1, repository.calls.size)
        assertEquals(SearchPaginationState.Idle, assertIs<SearchContent.Results>(viewModel.uiState.value.content).pagination)
    }

    @Test
    fun `falha incremental preserva resultados e retry repete a mesma pagina`() = runTest {
        val firstPage = anime(20, "Naruto")
        val secondPage = anime(21, "Naruto Shippuden")
        var nextPageAttempts = 0
        val repository = ControllableAnimeRepository { call ->
            if (call.page == 1) {
                successPage(1, true, firstPage)
            } else if (nextPageAttempts++ == 0) {
                RepositoryResult.Failure(RepositoryFailure.Timeout, TestException())
            } else {
                successPage(2, false, secondPage)
            }
        }
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()
        viewModel.loadNextPage()
        runCurrent()

        val failedResults = assertIs<SearchContent.Results>(viewModel.uiState.value.content)
        assertEquals(listOf(firstPage), failedResults.items)
        assertEquals(
            SearchPaginationState.Failure(RepositoryFailure.Timeout),
            failedResults.pagination,
        )

        viewModel.retryNextPage()
        runCurrent()

        assertEquals(
            listOf(
                SearchCall("Naruto", 1, SEARCH_PAGE_SIZE),
                SearchCall("Naruto", 2, SEARCH_PAGE_SIZE),
                SearchCall("Naruto", 2, SEARCH_PAGE_SIZE),
            ),
            repository.calls,
        )
        val recoveredResults = assertIs<SearchContent.Results>(viewModel.uiState.value.content)
        assertEquals(listOf(firstPage, secondPage), recoveredResults.items)
        assertEquals(SearchPaginationState.Idle, recoveredResults.pagination)
    }

    @Test
    fun `falha esperada permite retry da consulta atual`() = runTest {
        var attempt = 0
        val repository = ControllableAnimeRepository {
            attempt += 1
            if (attempt == 1) {
                RepositoryResult.Failure(RepositoryFailure.Timeout, TestException())
            } else {
                successResult(anime(20, "Naruto"))
            }
        }
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()

        val failure = assertIs<SearchContent.Failure>(viewModel.uiState.value.content)
        assertEquals(RepositoryFailure.Timeout, failure.reason)

        viewModel.retry()
        runCurrent()

        assertIs<SearchContent.Results>(viewModel.uiState.value.content)
        assertEquals(2, repository.calls.size)
        assertEquals(repository.calls[0], repository.calls[1])
    }

    @Test
    fun `limite de requisicoes aguarda e pesquisa uma vez com consulta mais recente`() = runTest {
        var attempt = 0
        val expected = anime(269, "Bleach")
        val repository = ControllableAnimeRepository {
            attempt += 1
            if (attempt == 1) rateLimitedResult() else successResult(expected)
        }
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()

        assertEquals(
            SearchContent.RateLimitCooldown(SEARCH_RATE_LIMIT_COOLDOWN_SECONDS),
            viewModel.uiState.value.content,
        )

        advanceTimeBy(10_000L)
        runCurrent()
        assertEquals(SearchContent.RateLimitCooldown(5), viewModel.uiState.value.content)

        viewModel.onQueryChange("  Bleach  ")
        viewModel.submit()
        advanceTimeBy(4_999L)
        runCurrent()
        assertEquals(1, repository.calls.size)

        advanceTimeBy(1L)
        runCurrent()

        assertEquals(2, repository.calls.size)
        assertEquals(SearchCall("Bleach", 1, SEARCH_PAGE_SIZE), repository.calls.last())
        assertEquals(listOf(expected), assertIs<SearchContent.Results>(viewModel.uiState.value.content).items)
    }

    @Test
    fun `limpar durante espera por limite cancela tentativa automatica`() = runTest {
        val repository = ControllableAnimeRepository { rateLimitedResult() }
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()
        viewModel.clearQuery()
        advanceTimeBy(SEARCH_RATE_LIMIT_COOLDOWN_SECONDS * 1_000L)
        runCurrent()

        assertEquals(SearchUiState(), viewModel.uiState.value)
        assertEquals(1, repository.calls.size)
    }

    @Test
    fun `segundo limite seguido nao cria ciclo de tentativas automaticas`() = runTest {
        val repository = ControllableAnimeRepository { rateLimitedResult() }
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()
        advanceTimeBy(SEARCH_RATE_LIMIT_COOLDOWN_SECONDS * 1_000L)
        runCurrent()

        val failure = assertIs<SearchContent.Failure>(viewModel.uiState.value.content)
        assertEquals(RepositoryFailure.RateLimited, failure.reason)
        assertEquals(2, repository.calls.size)

        advanceTimeBy(SEARCH_RATE_LIMIT_COOLDOWN_SECONDS * 1_000L)
        runCurrent()
        assertEquals(2, repository.calls.size)
    }

    @Test
    fun `retry nao usa consulta que ja foi alterada`() = runTest {
        val repository = ControllableAnimeRepository {
            RepositoryResult.Failure(RepositoryFailure.Unavailable)
        }
        val viewModel = SearchViewModel(repository, StandardTestDispatcher(testScheduler))

        viewModel.onQueryChange("Naruto")
        viewModel.submit()
        runCurrent()
        viewModel.onQueryChange("Bleach")
        viewModel.retry()
        runCurrent()

        assertEquals(1, repository.calls.size)
        assertEquals(SearchContent.Debouncing, viewModel.uiState.value.content)
        viewModel.clearQuery()
    }

    private data class SearchCall(
        val query: String,
        val page: Int,
        val perPage: Int,
    )

    private class ControllableAnimeRepository(
        private val ignoreCancellation: Boolean = false,
        private val result: suspend (SearchCall) -> RepositoryResult<PagedResult<AnimeSummary>> = {
            emptyResult()
        },
    ) : AnimeRepository {
        val calls = mutableListOf<SearchCall>()
        var cancellationCount = 0
            private set

        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int,
        ): RepositoryResult<PagedResult<AnimeSummary>> {
            val call = SearchCall(query, page, perPage)
            calls += call
            return try {
                result(call)
            } catch (cause: CancellationException) {
                cancellationCount += 1
                if (ignoreCancellation) {
                    withContext(NonCancellable) { result(call) }
                } else {
                    throw cause
                }
            }
        }

        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> =
            error("Não deve ser chamado neste teste.")
    }

    private class TestException : Exception()
}

private fun anime(id: Int, title: String): AnimeSummary =
    AnimeSummary(
        id = CatalogAnimeId(id.toString()),
        titles = AnimeTitles(english = title),
    )

private fun successResult(
    vararg items: AnimeSummary,
): RepositoryResult<PagedResult<AnimeSummary>> = successPage(1, false, *items)

private fun successPage(
    page: Int = 1,
    hasNextPage: Boolean = false,
    vararg items: AnimeSummary,
): RepositoryResult<PagedResult<AnimeSummary>> =
    RepositoryResult.Success(
        PagedResult(
            items = items.toList(),
            pageInfo = PageInfo(
                currentPage = page,
                lastPage = if (hasNextPage) page + 1 else page,
                hasNextPage = hasNextPage,
                perPage = SEARCH_PAGE_SIZE,
                total = items.size,
            ),
        ),
    )

private fun emptyResult(): RepositoryResult<PagedResult<AnimeSummary>> = successResult()

private fun rateLimitedResult(): RepositoryResult<PagedResult<AnimeSummary>> =
    RepositoryResult.Failure(RepositoryFailure.RateLimited)
