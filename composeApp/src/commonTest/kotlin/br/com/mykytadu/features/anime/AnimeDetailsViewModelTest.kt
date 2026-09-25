package br.com.mykytadu.features.anime

import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class AnimeDetailsViewModelTest {

    @Test
    fun `carrega detalhes automaticamente com a identidade recebida`() = runTest {
        val id = CatalogAnimeId("21579")
        val expected = AnimeDetails(id = id)
        val repository = FakeAnimeRepository {
            RepositoryResult.Success(expected)
        }

        val viewModel = AnimeDetailsViewModel(
            id = id,
            animeRepository = repository,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        assertEquals(AnimeDetailsUiState.Loading, viewModel.uiState.value)
        runCurrent()

        assertEquals(listOf(id), repository.detailsCalls)
        assertEquals(expected, assertIs<AnimeDetailsUiState.Content>(viewModel.uiState.value).details)
    }

    @Test
    fun `publica falha esperada e retry repete a identidade`() = runTest {
        val id = CatalogAnimeId("20")
        var attempt = 0
        val expected = AnimeDetails(id = id)
        val repository = FakeAnimeRepository {
            if (attempt++ == 0) {
                RepositoryResult.Failure(RepositoryFailure.Timeout)
            } else {
                RepositoryResult.Success(expected)
            }
        }
        val viewModel = AnimeDetailsViewModel(
            id = id,
            animeRepository = repository,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        runCurrent()
        assertEquals(
            RepositoryFailure.Timeout,
            assertIs<AnimeDetailsUiState.Failure>(viewModel.uiState.value).reason,
        )

        viewModel.retry()
        assertEquals(AnimeDetailsUiState.Loading, viewModel.uiState.value)
        runCurrent()

        assertEquals(listOf(id, id), repository.detailsCalls)
        assertEquals(expected, assertIs<AnimeDetailsUiState.Content>(viewModel.uiState.value).details)
    }

    @Test
    fun `retry fora do estado de falha nao duplica carregamento`() = runTest {
        val response = CompletableDeferred<RepositoryResult<AnimeDetails>>()
        val repository = FakeAnimeRepository { response.await() }
        val viewModel = AnimeDetailsViewModel(
            id = CatalogAnimeId("20"),
            animeRepository = repository,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        runCurrent()
        viewModel.retry()
        runCurrent()

        assertEquals(1, repository.detailsCalls.size)
        response.complete(RepositoryResult.Success(AnimeDetails(CatalogAnimeId("20"))))
        runCurrent()

        viewModel.retry()
        runCurrent()
        assertEquals(1, repository.detailsCalls.size)
    }

    private class FakeAnimeRepository(
        private val detailsResult: suspend () -> RepositoryResult<AnimeDetails>,
    ) : AnimeRepository {
        val detailsCalls = mutableListOf<CatalogAnimeId>()

        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> {
            detailsCalls += id
            return detailsResult()
        }

        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int,
        ): RepositoryResult<PagedResult<AnimeSummary>> = error("Não deve ser chamado neste teste.")
    }
}
