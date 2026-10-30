package br.com.mykytadu.domain.repository

import br.com.mykytadu.domain.model.translation.LanguageCode
import br.com.mykytadu.domain.model.translation.TranslatedSynopsis
import br.com.mykytadu.domain.result.RepositoryResult

interface TranslationRepository {
    suspend fun translateSynopsis(
        text: String,
        targetLanguage: LanguageCode,
        sourceLanguage: LanguageCode = LanguageCode.ENGLISH,
        forceRefresh: Boolean = false,
    ): RepositoryResult<TranslatedSynopsis>

    suspend fun clearCache(): RepositoryResult<Unit>
}
