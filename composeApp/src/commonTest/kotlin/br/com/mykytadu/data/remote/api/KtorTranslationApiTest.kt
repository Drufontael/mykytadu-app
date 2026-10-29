package br.com.mykytadu.data.remote.api

import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.di.configureSharedHttpClient
import br.com.mykytadu.domain.model.translation.LanguageCode
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.Logger
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class KtorTranslationApiTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

    @Test
    fun `translate retorna texto traduzido e decodifica entidades html com sucesso`() = runTest {
        val responseJson = """
            {
              "responseData": {
                "translatedText": "Hist&#39;ria de um jovem pirata &quot;Luffy&quot;",
                "match": 0.85
              },
              "responseStatus": 200
            }
        """.trimIndent()

        var requestedUrl: String? = null
        val engine = MockEngine { request ->
            requestedUrl = request.url.toString()
            respond(responseJson, HttpStatusCode.OK, jsonHeaders)
        }
        val api = KtorTranslationApi(createTestClient(engine))

        val result = api.translate(
            text = "Story of a young pirate 'Luffy'",
            sourceLanguage = LanguageCode.ENGLISH,
            targetLanguage = LanguageCode.PORTUGUESE,
        )

        val success = assertIs<NetworkResult.Success<String>>(result)
        assertEquals("Hist'ria de um jovem pirata \"Luffy\"", success.value)
        assertEquals(true, requestedUrl?.contains("langpair=en%7Cpt") == true || requestedUrl?.contains("langpair=en|pt") == true)
    }

    @Test
    fun `translate retorna texto original imediatamente quando idiomas sao iguais`() = runTest {
        var callCount = 0
        val engine = MockEngine {
            callCount++
            respond("{}", HttpStatusCode.OK, jsonHeaders)
        }
        val api = KtorTranslationApi(createTestClient(engine))

        val result = api.translate(
            text = "Already in english",
            sourceLanguage = LanguageCode.ENGLISH,
            targetLanguage = LanguageCode.ENGLISH,
        )

        val success = assertIs<NetworkResult.Success<String>>(result)
        assertEquals("Already in english", success.value)
        assertEquals(0, callCount)
    }

    @Test
    fun `translate retorna vazio sem chamada de rede para texto em branco`() = runTest {
        var callCount = 0
        val engine = MockEngine {
            callCount++
            respond("{}", HttpStatusCode.OK, jsonHeaders)
        }
        val api = KtorTranslationApi(createTestClient(engine))

        val result = api.translate(
            text = "   ",
            sourceLanguage = LanguageCode.ENGLISH,
            targetLanguage = LanguageCode.PORTUGUESE,
        )

        val success = assertIs<NetworkResult.Success<String>>(result)
        assertEquals("", success.value)
        assertEquals(0, callCount)
    }

    @Test
    fun `translate retorna NetworkResult Failure quando resposta http falha`() = runTest {
        val engine = MockEngine {
            respond("Service unavailable", HttpStatusCode.ServiceUnavailable)
        }
        val api = KtorTranslationApi(createTestClient(engine))

        val result = api.translate(
            text = "Valid text",
            sourceLanguage = LanguageCode.ENGLISH,
            targetLanguage = LanguageCode.PORTUGUESE,
        )

        val failure = assertIs<NetworkResult.Failure>(result)
        val httpFailure = assertIs<NetworkFailure.Http>(failure.error)
        assertEquals(503, httpFailure.statusCode)
    }

    private fun createTestClient(engine: MockEngine): HttpClient = HttpClient(engine) {
        configureSharedHttpClient(
            networkLogger = Logger.EMPTY,
            loggingEnabled = false,
        )
    }
}
