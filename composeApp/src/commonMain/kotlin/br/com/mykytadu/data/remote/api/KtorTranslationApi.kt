package br.com.mykytadu.data.remote.api

import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.core.network.safeNetworkCall
import br.com.mykytadu.data.remote.dto.translation.MyMemoryTranslationResponseDto
import br.com.mykytadu.domain.model.translation.LanguageCode
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorTranslationApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = DEFAULT_BASE_URL,
) : TranslationApi {

    override suspend fun translate(
        text: String,
        sourceLanguage: LanguageCode,
        targetLanguage: LanguageCode,
    ): NetworkResult<String> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return NetworkResult.Success("")
        }
        if (sourceLanguage == targetLanguage) {
            return NetworkResult.Success(trimmed)
        }

        val langPair = "${sourceLanguage.code}|${targetLanguage.code}"
        val networkCall = safeNetworkCall {
            httpClient.get(baseUrl) {
                parameter("q", trimmed)
                parameter("langpair", langPair)
            }.body<MyMemoryTranslationResponseDto>()
        }

        return when (networkCall) {
            is NetworkResult.Success -> {
                val translated = networkCall.value.responseData?.translatedText
                if (!translated.isNullOrBlank()) {
                    NetworkResult.Success(unescapeHtmlEntities(translated))
                } else {
                    val details = networkCall.value.responseDetails ?: "Empty translation result"
                    NetworkResult.Failure(NetworkFailure.Unknown(IllegalStateException(details)))
                }
            }
            is NetworkResult.Failure -> networkCall
        }
    }

    private fun unescapeHtmlEntities(input: String): String =
        input.replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")

    companion object {
        const val DEFAULT_BASE_URL = "https://api.mymemory.translated.net/get"
    }
}
