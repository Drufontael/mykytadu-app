package br.com.mykytadu.data.remote.api

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.domain.model.translation.LanguageCode

interface TranslationApi {
    suspend fun translate(
        text: String,
        sourceLanguage: LanguageCode = LanguageCode.ENGLISH,
        targetLanguage: LanguageCode,
    ): NetworkResult<String>
}
