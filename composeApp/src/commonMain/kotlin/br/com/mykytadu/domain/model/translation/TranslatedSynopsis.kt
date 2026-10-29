package br.com.mykytadu.domain.model.translation

data class TranslatedSynopsis(
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: LanguageCode = LanguageCode.ENGLISH,
    val targetLanguage: LanguageCode,
    val isFromCache: Boolean = false,
)
