package br.com.mykytadu.data.repository

import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.local.datasource.InMemoryTranslationCacheLocalDataSource
import br.com.mykytadu.data.remote.api.TranslationApi
import br.com.mykytadu.domain.model.translation.LanguageCode
import br.com.mykytadu.domain.model.translation.TranslatedSynopsis
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DefaultTranslationRepositoryTest {

    private class FakeTranslationApi : TranslationApi {
        var callCount = 0
        var simulateResult: NetworkResult<String>? = null

        override suspend fun translate(
            text: String,
            sourceLanguage: LanguageCode,
            targetLanguage: LanguageCode,
        ): NetworkResult<String> {
            callCount++
            return simulateResult ?: NetworkResult.Success("Tradução: $text")
        }
    }

    @Test
    fun `translateSynopsis com cache miss chama TranslationApi e salva no cache`() = runTest {
        val fakeApi = FakeTranslationApi()
        val cache = InMemoryTranslationCacheLocalDataSource()
        val repository = DefaultTranslationRepository(
            translationApi = fakeApi,
            translationCacheLocalDataSource = cache,
            timeProvider = { 1000L },
        )

        val result = repository.translateSynopsis(
            text = "A pirate adventure.",
            targetLanguage = LanguageCode.PORTUGUESE,
        )

        val success = assertIs<RepositoryResult.Success<TranslatedSynopsis>>(result)
        assertEquals("A pirate adventure.", success.value.originalText)
        assertEquals("Tradução: A pirate adventure.", success.value.translatedText)
        assertFalse(success.value.isFromCache)
        assertEquals(1, fakeApi.callCount)
        assertEquals(1, cache.getTranslationsCount())
    }

    @Test
    fun `translateSynopsis com cache hit retorna dados do cache sem chamar TranslationApi`() = runTest {
        val fakeApi = FakeTranslationApi()
        val cache = InMemoryTranslationCacheLocalDataSource()
        val repository = DefaultTranslationRepository(
            translationApi = fakeApi,
            translationCacheLocalDataSource = cache,
            timeProvider = { 1000L },
        )

        cache.putTranslation(
            originalText = "A pirate adventure.",
            translatedText = "Uma aventura pirata pré-cacheada.",
            sourceLanguage = LanguageCode.ENGLISH,
            targetLanguage = LanguageCode.PORTUGUESE,
            cachedAt = 1000L,
        )

        val result = repository.translateSynopsis(
            text = "A pirate adventure.",
            targetLanguage = LanguageCode.PORTUGUESE,
        )

        val success = assertIs<RepositoryResult.Success<TranslatedSynopsis>>(result)
        assertEquals("Uma aventura pirata pré-cacheada.", success.value.translatedText)
        assertTrue(success.value.isFromCache)
        assertEquals(0, fakeApi.callCount)
    }

    @Test
    fun `translateSynopsis com forceRefresh chama API mesmo com entrada em cache`() = runTest {
        val fakeApi = FakeTranslationApi().apply {
            simulateResult = NetworkResult.Success("Nova tradução atualizada")
        }
        val cache = InMemoryTranslationCacheLocalDataSource()
        val repository = DefaultTranslationRepository(
            translationApi = fakeApi,
            translationCacheLocalDataSource = cache,
            timeProvider = { 1000L },
        )

        cache.putTranslation(
            originalText = "A pirate adventure.",
            translatedText = "Tradução antiga",
            sourceLanguage = LanguageCode.ENGLISH,
            targetLanguage = LanguageCode.PORTUGUESE,
            cachedAt = 500L,
        )

        val result = repository.translateSynopsis(
            text = "A pirate adventure.",
            targetLanguage = LanguageCode.PORTUGUESE,
            forceRefresh = true,
        )

        val success = assertIs<RepositoryResult.Success<TranslatedSynopsis>>(result)
        assertEquals("Nova tradução atualizada", success.value.translatedText)
        assertFalse(success.value.isFromCache)
        assertEquals(1, fakeApi.callCount)

        // Verifica que o cache foi atualizado
        val updated = cache.getTranslation("A pirate adventure.", LanguageCode.ENGLISH, LanguageCode.PORTUGUESE)
        assertEquals("Nova tradução atualizada", updated?.translatedText)
    }

    @Test
    fun `translateSynopsis com mesmo idioma retorna texto original imediatamente`() = runTest {
        val fakeApi = FakeTranslationApi()
        val cache = InMemoryTranslationCacheLocalDataSource()
        val repository = DefaultTranslationRepository(
            translationApi = fakeApi,
            translationCacheLocalDataSource = cache,
        )

        val result = repository.translateSynopsis(
            text = "Original English text",
            sourceLanguage = LanguageCode.ENGLISH,
            targetLanguage = LanguageCode.ENGLISH,
        )

        val success = assertIs<RepositoryResult.Success<TranslatedSynopsis>>(result)
        assertEquals("Original English text", success.value.translatedText)
        assertEquals(0, fakeApi.callCount)
        assertEquals(0, cache.getTranslationsCount())
    }

    @Test
    fun `translateSynopsis propaga falha quando a chamada de API falha`() = runTest {
        val fakeApi = FakeTranslationApi().apply {
            simulateResult = NetworkResult.Failure(NetworkFailure.Connection(Exception("Sem conexão")))
        }
        val cache = InMemoryTranslationCacheLocalDataSource()
        val repository = DefaultTranslationRepository(
            translationApi = fakeApi,
            translationCacheLocalDataSource = cache,
        )

        val result = repository.translateSynopsis(
            text = "Some text",
            targetLanguage = LanguageCode.PORTUGUESE,
        )

        val failure = assertIs<RepositoryResult.Failure>(result)
        assertEquals(RepositoryFailure.Unavailable, failure.reason)
        assertEquals(1, fakeApi.callCount)
    }

    @Test
    fun `clearCache esvazia o cache de traducoes com sucesso`() = runTest {
        val cache = InMemoryTranslationCacheLocalDataSource()
        cache.putTranslation("Hello", "Olá", LanguageCode.ENGLISH, LanguageCode.PORTUGUESE, 1000L)
        assertEquals(1, cache.getTranslationsCount())

        val repository = DefaultTranslationRepository(
            translationApi = FakeTranslationApi(),
            translationCacheLocalDataSource = cache,
        )

        val result = repository.clearCache()
        assertIs<RepositoryResult.Success<Unit>>(result)
        assertEquals(0, cache.getTranslationsCount())
    }
}
