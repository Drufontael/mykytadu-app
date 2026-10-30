package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.CachedTranslationEntity
import br.com.mykytadu.domain.model.translation.LanguageCode

interface TranslationCacheLocalDataSource {
    suspend fun getTranslation(
        text: String,
        sourceLanguage: LanguageCode,
        targetLanguage: LanguageCode,
    ): CachedTranslationEntity?

    suspend fun putTranslation(
        originalText: String,
        translatedText: String,
        sourceLanguage: LanguageCode,
        targetLanguage: LanguageCode,
        cachedAt: Long,
        expiresAt: Long? = null,
    )

    suspend fun clearTranslations()

    suspend fun getTranslationsCount(): Int
}
