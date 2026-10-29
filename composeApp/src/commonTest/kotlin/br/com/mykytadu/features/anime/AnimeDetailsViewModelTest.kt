package br.com.mykytadu.features.anime

import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.model.translation.LanguageCode
import br.com.mykytadu.domain.model.translation.TranslatedSynopsis
import br.com.mykytadu.domain.repository.TranslationRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    @Test
    fun `detalhes com fallback offline reflete isOfflineFallback no estado Content`() = runTest {
        val id = CatalogAnimeId("21579")
        val expected = AnimeDetails(id = id, isOfflineFallback = true)
        val repository = FakeAnimeRepository {
            RepositoryResult.Success(expected)
        }

        val viewModel = AnimeDetailsViewModel(
            id = id,
            animeRepository = repository,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        runCurrent()

        val content = assertIs<AnimeDetailsUiState.Content>(viewModel.uiState.value)
        assertEquals(true, content.isOfflineFallback)
    }

    @Test
    fun `toggleTranslation traduz a sinopse e alterna entre traducao e texto original`() = runTest {
        val id = CatalogAnimeId("10")
        val originalText = "Original anime synopsis in English"
        val expected = AnimeDetails(id = id, description = originalText)
        val animeRepo = FakeAnimeRepository { RepositoryResult.Success(expected) }
        val fakeTranslationRepo = FakeTranslationRepo()

        val viewModel = AnimeDetailsViewModel(
            id = id,
            animeRepository = animeRepo,
            translationRepository = fakeTranslationRepo,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        runCurrent()
        val initialContent = assertIs<AnimeDetailsUiState.Content>(viewModel.uiState.value)
        assertEquals(originalText, initialContent.displayedDescription)
        assertFalse(initialContent.isShowingTranslation)

        // 1. Dispara tradução sob demanda
        viewModel.toggleTranslation()
        runCurrent()

        val translatedContent = assertIs<AnimeDetailsUiState.Content>(viewModel.uiState.value)
        assertTrue(translatedContent.isShowingTranslation)
        assertEquals("Traduzido: Original anime synopsis in English", translatedContent.displayedDescription)
        assertEquals(1, fakeTranslationRepo.callCount)

        // 2. Alterna de volta para ver original (sem chamar API novamente)
        viewModel.toggleTranslation()
        runCurrent()

        val originalContent = assertIs<AnimeDetailsUiState.Content>(viewModel.uiState.value)
        assertFalse(originalContent.isShowingTranslation)
        assertEquals(originalText, originalContent.displayedDescription)
        assertEquals(1, fakeTranslationRepo.callCount)

        // 3. Alterna novamente para tradução (usa a tradução já carregada sem chamar API)
        viewModel.toggleTranslation()
        runCurrent()

        val toggledBack = assertIs<AnimeDetailsUiState.Content>(viewModel.uiState.value)
        assertTrue(toggledBack.isShowingTranslation)
        assertEquals("Traduzido: Original anime synopsis in English", toggledBack.displayedDescription)
        assertEquals(1, fakeTranslationRepo.callCount)
    }

    @Test
    fun `toggleTranslation com falha do servico mantem texto original e publica translationError`() = runTest {
        val id = CatalogAnimeId("10")
        val originalText = "Original synopsis"
        val expected = AnimeDetails(id = id, description = originalText)
        val animeRepo = FakeAnimeRepository { RepositoryResult.Success(expected) }
        val fakeTranslationRepo = FakeTranslationRepo().apply {
            shouldFail = true
        }

        val viewModel = AnimeDetailsViewModel(
            id = id,
            animeRepository = animeRepo,
            translationRepository = fakeTranslationRepo,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        runCurrent()
        viewModel.toggleTranslation()
        runCurrent()

        val content = assertIs<AnimeDetailsUiState.Content>(viewModel.uiState.value)
        assertFalse(content.isShowingTranslation)
        assertNotNull(content.translationError)
        assertEquals(originalText, content.displayedDescription)
    }

    private class FakeTranslationRepo : TranslationRepository {
        var callCount = 0
        var shouldFail = false

        override suspend fun translateSynopsis(
            text: String,
            targetLanguage: LanguageCode,
            sourceLanguage: LanguageCode,
            forceRefresh: Boolean,
        ): RepositoryResult<TranslatedSynopsis> {
            callCount++
            return if (shouldFail) {
                RepositoryResult.Failure(RepositoryFailure.Unavailable)
            } else {
                RepositoryResult.Success(
                    TranslatedSynopsis(
                        originalText = text,
                        translatedText = "Traduzido: $text",
                        sourceLanguage = sourceLanguage,
                        targetLanguage = targetLanguage,
                        isFromCache = false,
                    )
                )
            }
        }

        override suspend fun clearCache(): RepositoryResult<Unit> = RepositoryResult.Success(Unit)
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
