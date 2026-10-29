package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.domain.model.translation.LanguageCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class InMemoryTranslationCacheLocalDataSourceTest {

    @Test
    fun `putTranslation armazena e getTranslation recupera traducao`() = runTest {
        val cache = InMemoryTranslationCacheLocalDataSource()
        val original = "A boy who wants to be the pirate king."
        val translated = "Um garoto que quer ser o rei dos piratas."

        cache.putTranslation(
            originalText = original,
            translatedText = translated,
            sourceLanguage = LanguageCode.ENGLISH,
            targetLanguage = LanguageCode.PORTUGUESE,
            cachedAt = 1000L,
        )

        val cached = cache.getTranslation(original, LanguageCode.ENGLISH, LanguageCode.PORTUGUESE)
        assertNotNull(cached)
        assertEquals(original, cached.originalText)
        assertEquals(translated, cached.translatedText)
        assertEquals(LanguageCode.PORTUGUESE, cached.targetLanguage)
        assertEquals(1, cache.getTranslationsCount())
    }

    @Test
    fun `getTranslation retorna null para chaves nao existentes`() = runTest {
        val cache = InMemoryTranslationCacheLocalDataSource()
        val cached = cache.getTranslation("Non existent", LanguageCode.ENGLISH, LanguageCode.SPANISH)
        assertNull(cached)
    }

    @Test
    fun `cache descarta entradas mais antigas ao atingir maxEntries`() = runTest {
        val cache = InMemoryTranslationCacheLocalDataSource(maxEntries = 2)

        cache.putTranslation("One", "Um", LanguageCode.ENGLISH, LanguageCode.PORTUGUESE, 1000L)
        cache.putTranslation("Two", "Dois", LanguageCode.ENGLISH, LanguageCode.PORTUGUESE, 2000L)
        cache.putTranslation("Three", "Tres", LanguageCode.ENGLISH, LanguageCode.PORTUGUESE, 3000L)

        assertEquals(2, cache.getTranslationsCount())
        // "One" deve ter sido descartado por ser o mais antigo
        assertNull(cache.getTranslation("One", LanguageCode.ENGLISH, LanguageCode.PORTUGUESE))
        assertNotNull(cache.getTranslation("Two", LanguageCode.ENGLISH, LanguageCode.PORTUGUESE))
        assertNotNull(cache.getTranslation("Three", LanguageCode.ENGLISH, LanguageCode.PORTUGUESE))
    }

    @Test
    fun `clearTranslations limpa todas as entradas`() = runTest {
        val cache = InMemoryTranslationCacheLocalDataSource()
        cache.putTranslation("One", "Um", LanguageCode.ENGLISH, LanguageCode.PORTUGUESE, 1000L)
        assertEquals(1, cache.getTranslationsCount())

        cache.clearTranslations()
        assertEquals(0, cache.getTranslationsCount())
        assertNull(cache.getTranslation("One", LanguageCode.ENGLISH, LanguageCode.PORTUGUESE))
    }
}
