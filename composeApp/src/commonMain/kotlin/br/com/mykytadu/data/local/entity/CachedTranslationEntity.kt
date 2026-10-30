package br.com.mykytadu.data.local.entity

import br.com.mykytadu.domain.model.translation.LanguageCode

data class CachedTranslationEntity(
    val cacheKey: String,
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: LanguageCode,
    val targetLanguage: LanguageCode,
    val cachedAtEpochMillis: Long,
    val expiresAtEpochMillis: Long? = null,
) {
    fun isExpired(currentTimeMillis: Long): Boolean =
        expiresAtEpochMillis != null && currentTimeMillis >= expiresAtEpochMillis
}
