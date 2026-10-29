package br.com.mykytadu.data.repository

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.local.datasource.TranslationCacheLocalDataSource
import br.com.mykytadu.data.mapper.toRepositoryFailure
import br.com.mykytadu.data.remote.api.TranslationApi
import br.com.mykytadu.domain.model.translation.LanguageCode
import br.com.mykytadu.domain.model.translation.TranslatedSynopsis
import br.com.mykytadu.domain.repository.TranslationRepository
import br.com.mykytadu.domain.result.RepositoryResult

class DefaultTranslationRepository(
    private val translationApi: TranslationApi,
    private val translationCacheLocalDataSource: TranslationCacheLocalDataSource,
    private val timeProvider: () -> Long = { kotlin.time.Clock.System.now().toEpochMilliseconds() },
) : TranslationRepository {

    override suspend fun translateSynopsis(
        text: String,
        targetLanguage: LanguageCode,
        sourceLanguage: LanguageCode,
        forceRefresh: Boolean,
    ): RepositoryResult<TranslatedSynopsis> {
        val original = text.trim()
        if (original.isEmpty() || sourceLanguage == targetLanguage) {
            return RepositoryResult.Success(
                TranslatedSynopsis(
                    originalText = original,
                    translatedText = original,
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    isFromCache = false,
                )
            )
        }

        val currentTime = timeProvider()

        if (!forceRefresh) {
            val cached = translationCacheLocalDataSource.getTranslation(original, sourceLanguage, targetLanguage)
            if (cached != null && !cached.isExpired(currentTime)) {
                return RepositoryResult.Success(
                    TranslatedSynopsis(
                        originalText = original,
                        translatedText = cached.translatedText,
                        sourceLanguage = sourceLanguage,
                        targetLanguage = targetLanguage,
                        isFromCache = true,
                    )
                )
            }
        }

        return when (val apiResult = translationApi.translate(original, sourceLanguage, targetLanguage)) {
            is NetworkResult.Success -> {
                val translatedText = apiResult.value
                translationCacheLocalDataSource.putTranslation(
                    originalText = original,
                    translatedText = translatedText,
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    cachedAt = currentTime,
                )
                RepositoryResult.Success(
                    TranslatedSynopsis(
                        originalText = original,
                        translatedText = translatedText,
                        sourceLanguage = sourceLanguage,
                        targetLanguage = targetLanguage,
                        isFromCache = false,
                    )
                )
            }
            is NetworkResult.Failure -> {
                apiResult.error.toRepositoryFailure()
            }
        }
    }

    override suspend fun clearCache(): RepositoryResult<Unit> = try {
        translationCacheLocalDataSource.clearTranslations()
        RepositoryResult.Success(Unit)
    } catch (e: Exception) {
        RepositoryResult.Failure(br.com.mykytadu.domain.result.RepositoryFailure.Unknown, e)
    }
}
