package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.CachedTranslationEntity
import br.com.mykytadu.domain.model.translation.LanguageCode
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryTranslationCacheLocalDataSource(
    private val maxEntries: Int = DEFAULT_MAX_ENTRIES,
) : TranslationCacheLocalDataSource {

    private val mutex = Mutex()
    private val cache = LinkedHashMap<String, CachedTranslationEntity>()

    override suspend fun getTranslation(
        text: String,
        sourceLanguage: LanguageCode,
        targetLanguage: LanguageCode,
    ): CachedTranslationEntity? = mutex.withLock {
        val key = buildCacheKey(text, sourceLanguage, targetLanguage)
        val entry = cache.remove(key)
        if (entry != null) {
            // Reinsere para manter a ordem LRU (mais recente no final)
            cache[key] = entry
        }
        entry
    }

    override suspend fun putTranslation(
        originalText: String,
        translatedText: String,
        sourceLanguage: LanguageCode,
        targetLanguage: LanguageCode,
        cachedAt: Long,
        expiresAt: Long?,
    ) {
        mutex.withLock {
            val key = buildCacheKey(originalText, sourceLanguage, targetLanguage)
            cache.remove(key)

            // Descarte de LRU se exceder capacidade
            while (cache.size >= maxEntries) {
                val oldestKey = cache.keys.firstOrNull() ?: break
                cache.remove(oldestKey)
            }

            cache[key] = CachedTranslationEntity(
                cacheKey = key,
                originalText = originalText,
                translatedText = translatedText,
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                cachedAtEpochMillis = cachedAt,
                expiresAtEpochMillis = expiresAt,
            )
        }
    }

    override suspend fun clearTranslations() {
        mutex.withLock {
            cache.clear()
        }
    }

    override suspend fun getTranslationsCount(): Int = mutex.withLock {
        cache.size
    }

    private fun buildCacheKey(
        text: String,
        sourceLanguage: LanguageCode,
        targetLanguage: LanguageCode,
    ): String {
        val normalized = text.trim()
        val textHash = normalized.hashCode().toUInt().toString(16)
        val prefix = normalized.take(24).replace(Regex("[^a-zA-Z0-9]"), "_")
        return "${sourceLanguage.code}_${targetLanguage.code}_${textHash}_$prefix"
    }

    companion object {
        const val DEFAULT_MAX_ENTRIES = 200
    }
}
