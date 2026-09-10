package br.com.mykytadu.web.diagnostics

import br.com.mykytadu.domain.model.AnimeImages
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.AniListAnimeId
import br.com.mykytadu.domain.model.PageInfo
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WebNetworkDiagnosticRunnerTest {

    @Test
    fun `modo de diagnostico depende somente do parametro exato`() {
        assertFalse(isWebNetworkSmokeEnabled(""))
        assertFalse(isWebNetworkSmokeEnabled("?network-smoke=false"))
        assertFalse(isWebNetworkSmokeEnabled("?network-smoke=true%20"))
        assertTrue(isWebNetworkSmokeEnabled("?network-smoke=true"))
        assertTrue(isWebNetworkSmokeEnabled("?source=test&network-smoke=true"))
    }

    @Test
    fun `falha desconhecida preserva causa sanitizada para o diagnostico`() {
        val failure = RepositoryResult.Failure(
            reason = RepositoryFailure.Unknown,
            cause = IllegalStateException("engine não inicializada"),
        ).toWebDiagnosticFailure()

        assertEquals("falha não classificada", failure.category)
        assertEquals(WebDiagnosticPhase.HTTP_TRANSPORT, failure.phase)
        assertEquals(1, failure.technicalDetails.causes.size)
        assertEquals("IllegalStateException", failure.technicalDetails.causes.single().className)
        assertEquals("engine não inicializada", failure.technicalDetails.causes.single().message)
    }

    @Test
    fun `falha sem causa informa indisponibilidade tecnica`() {
        val failure = RepositoryResult.Failure(
            reason = RepositoryFailure.Unknown,
        ).toWebDiagnosticFailure()

        assertTrue(failure.technicalDetails.causes.isEmpty())
        assertFalse(failure.technicalDetails.chainTruncated)
        assertFalse(failure.technicalDetails.cycleDetected)
    }

    @Test
    fun `cadeia de causas e limitada e protege contra ciclo`() {
        val fourth = IllegalStateException("quatro")
        val third = IllegalStateException("três", fourth)
        val second = IllegalStateException("dois", third)
        val first = IllegalStateException("um", second)

        val limited = first.toWebDiagnosticTechnicalDetails()

        assertEquals(3, limited.causes.size)
        assertTrue(limited.chainTruncated)

        val cyclic = CyclicDiagnosticThrowable("primeira")
        val nested = CyclicDiagnosticThrowable("segunda")
        cyclic.next = nested
        nested.next = cyclic

        val protected = cyclic.toWebDiagnosticTechnicalDetails()

        assertEquals(2, protected.causes.size)
        assertTrue(protected.cycleDetected)
    }

    @Test
    fun `sanitizacao remove dados sensiveis e trunca mensagens`() {
        val sensitive = "Authorization: Bearer segredo Cookie: sessao=privada token=abc123"
            .toSanitizedDiagnosticMessage()

        assertFalse(sensitive.contains("segredo"))
        assertFalse(sensitive.contains("privada"))
        assertFalse(sensitive.contains("abc123"))

        val graphql = "{\"query\":\"query AnimeSearch { Page { media { id } } }\"}"
            .toSanitizedDiagnosticMessage()
        assertEquals("conteúdo GraphQL omitido", graphql)

        val truncated = "x".repeat(200).toSanitizedDiagnosticMessage()
        assertTrue(truncated.endsWith("…"))
        assertTrue(truncated.length <= 181)
    }

    @Test
    fun `nao deve iniciar chamadas concorrentes para o mesmo diagnostico`() = runTest {
        val repository = ControllableRepository()
        val states = mutableListOf<WebNetworkDiagnosticState>()
        val runner = WebNetworkDiagnosticRunner(repository, this) { states += it }

        runner.start()
        runner.start()

        runCurrent()

        assertEquals(1, repository.searchCalls)
        assertIs<WebNetworkDiagnosticState.Loading>(states.last())

        repository.completeWithSuccess()
        advanceUntilIdle()
    }

    @Test
    fun `cancelamento nao vira falha nem permite resultado obsoleto`() = runTest {
        val repository = ControllableRepository()
        val states = mutableListOf<WebNetworkDiagnosticState>()
        val runner = WebNetworkDiagnosticRunner(repository, this) { states += it }

        runner.start()
        runCurrent()
        runner.cancel()
        repository.completeWithSuccess()
        runCurrent()

        assertIs<WebNetworkDiagnosticState.Cancelled>(states.last())
    }

    @Test
    fun `sucesso apresenta somente resumo de dominio`() = runTest {
        val repository = ControllableRepository()
        val states = mutableListOf<WebNetworkDiagnosticState>()
        val runner = WebNetworkDiagnosticRunner(repository, this) { states += it }

        runner.start()
        runCurrent()
        repository.completeWithSuccess()
        advanceUntilIdle()

        val success = assertIs<WebNetworkDiagnosticState.Success>(states.last())
        assertEquals(1, success.itemCount)
        assertEquals(20, success.firstId)
        assertEquals("Naruto", success.firstTitle)
        assertEquals("https://example.test/naruto.jpg", success.coverUrl)
    }

    @Test
    fun `resultado anterior nao substitui uma nova execucao`() = runTest {
        val repository = IgnoringCancellationRepository()
        val states = mutableListOf<WebNetworkDiagnosticState>()
        val runner = WebNetworkDiagnosticRunner(repository, this) { states += it }

        runner.start()
        runCurrent()
        runner.cancel()
        runner.start()
        runCurrent()

        repository.complete(1)
        advanceUntilIdle()
        assertEquals(21, assertIs<WebNetworkDiagnosticState.Success>(states.last()).firstId)

        repository.complete(0)
        advanceUntilIdle()
        assertEquals(21, assertIs<WebNetworkDiagnosticState.Success>(states.last()).firstId)
    }

    private class ControllableRepository : AnimeRepository {
        var searchCalls = 0
            private set

        private val response = CompletableDeferred<Unit>()

        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int,
        ): RepositoryResult<PagedResult<AnimeSummary>> {
            searchCalls++
            response.await()
            return result()
        }

        override suspend fun getAnimeDetails(id: AniListAnimeId) =
            error("Não usado pelo diagnóstico.")

        fun completeWithSuccess() {
            response.complete(Unit)
        }

        private fun result(): RepositoryResult<PagedResult<AnimeSummary>> =
            RepositoryResult.Success(
                PagedResult(
                    items = listOf(
                        AnimeSummary(
                            id = AniListAnimeId(20),
                            titles = AnimeTitles(english = "Naruto"),
                            images = AnimeImages(coverLarge = "https://example.test/naruto.jpg"),
                        ),
                    ),
                    pageInfo = PageInfo(
                        currentPage = 1,
                        lastPage = 1,
                        hasNextPage = false,
                        perPage = 3,
                        total = 1,
                    ),
                ),
            )
    }

    private class IgnoringCancellationRepository : AnimeRepository {
        private val responses = mutableListOf<CompletableDeferred<Unit>>()

        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int,
        ): RepositoryResult<PagedResult<AnimeSummary>> {
            val index = responses.size
            val response = CompletableDeferred<Unit>()
            responses += response

            return withContext(NonCancellable) {
                response.await()
                RepositoryResult.Success(
                    PagedResult(
                        items = listOf(
                            AnimeSummary(
                                id = AniListAnimeId(20 + index),
                                titles = AnimeTitles(english = "Naruto $index"),
                            ),
                        ),
                        pageInfo = PageInfo(
                            currentPage = 1,
                            lastPage = 1,
                            hasNextPage = false,
                            perPage = 3,
                            total = 1,
                        ),
                    ),
                )
            }
        }

        override suspend fun getAnimeDetails(id: AniListAnimeId) =
            error("Não usado pelo diagnóstico.")

        fun complete(index: Int) {
            responses[index].complete(Unit)
        }
    }

    private class CyclicDiagnosticThrowable(
        message: String,
    ) : Throwable(message) {
        var next: Throwable? = null

        override val cause: Throwable?
            get() = next
    }
}
