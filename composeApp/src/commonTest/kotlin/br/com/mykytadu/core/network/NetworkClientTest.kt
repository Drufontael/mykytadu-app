package br.com.mykytadu.core.network

import br.com.mykytadu.di.configureSharedHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NetworkClientTest {

    @Test
    fun `deve retornar sucesso para resposta HTTP valida`() = runNetworkTest(
        responseBody = """{"value":"ok"}""",
    ) { client ->
        val result = safeNetworkCall { client.get(TEST_URL).body<TestPayload>() }

        val success = assertIs<NetworkResult.Success<TestPayload>>(result)
        assertEquals("ok", success.value.value)
    }

    @Test
    fun `deve converter status HTTP de erro`() = runNetworkTest(
        responseBody = """{"error":"failure"}""",
        status = HttpStatusCode.TooManyRequests,
    ) { client ->
        val result = safeNetworkCall { client.get(TEST_URL).body<String>() }

        val failure = assertIs<NetworkResult.Failure>(result)
        val httpFailure = assertIs<NetworkFailure.Http>(failure.error)
        assertEquals(HttpStatusCode.TooManyRequests.value, httpFailure.statusCode)
    }

    @Test
    fun `deve converter timeout`() = runNetworkTest { _ ->
        val result = safeNetworkCall<String> {
            throw HttpRequestTimeoutException(TEST_URL, HttpClientSettings.REQUEST_TIMEOUT_MILLIS)
        }

        assertIs<NetworkFailure.Timeout>(assertIs<NetworkResult.Failure>(result).error)
    }

    @Test
    fun `deve converter falha de conexao`() = runNetworkTest { _ ->
        val result = safeNetworkCall<String> { throw IOException("connection failure") }

        assertIs<NetworkFailure.Connection>(assertIs<NetworkResult.Failure>(result).error)
    }

    @Test
    fun `deve converter falha de serializacao`() = runNetworkTest(
        responseBody = "not-json",
    ) { client ->
        val result = safeNetworkCall { client.get(TEST_URL).body<TestPayload>() }

        assertIs<NetworkFailure.Serialization>(assertIs<NetworkResult.Failure>(result).error)
    }

    @Test
    fun `deve converter falha desconhecida preservando a causa`() = runNetworkTest { _ ->
        val cause = IllegalStateException("unexpected")
        val result = safeNetworkCall<String> { throw cause }

        val unknown = assertIs<NetworkFailure.Unknown>(assertIs<NetworkResult.Failure>(result).error)
        assertEquals(cause, unknown.cause)
    }

    @Test
    fun `requisicao publica nao deve enviar Authorization`() = runTest {
        var authorizationHeader: String? = "not-inspected"
        val engine = MockEngine { request ->
            authorizationHeader = request.headers[HttpHeaders.Authorization]
            respond(
                content = "{}",
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)

        try {
            client.get(TEST_URL)
            assertNull(authorizationHeader)
        } finally {
            client.close()
        }
    }

    @Test
    fun `logging deve ocultar Authorization`() = runTest {
        val messages = mutableListOf<String>()
        val logger = object : Logger {
            override fun log(message: String) {
                messages += message
            }
        }
        val client = createTestClient(
            engine = MockEngine { respond("{}", headers = JSON_HEADERS) },
            logger = logger,
        )

        try {
            client.get(TEST_URL) {
                header(HttpHeaders.Authorization, SECRET_AUTHORIZATION)
            }
            val logOutput = messages.joinToString("\n")
            assertFalse(SECRET_AUTHORIZATION in logOutput)
            assertTrue(HttpHeaders.Authorization in logOutput)
        } finally {
            client.close()
        }
    }

    private fun runNetworkTest(
        responseBody: String = "{}",
        status: HttpStatusCode = HttpStatusCode.OK,
        block: suspend (HttpClient) -> Unit,
    ) = runTest {
        val client = createTestClient(
            MockEngine {
                respond(
                    content = responseBody,
                    status = status,
                    headers = JSON_HEADERS,
                )
            }
        )

        try {
            block(client)
        } finally {
            client.close()
        }
    }

    private fun createTestClient(
        engine: MockEngine,
        logger: Logger = Logger.EMPTY,
    ): HttpClient = HttpClient(engine) {
        configureSharedHttpClient(
            networkLogger = logger,
            loggingEnabled = true,
        )
    }

    @Serializable
    private data class TestPayload(val value: String)

    private companion object {
        const val TEST_URL = "https://example.test/graphql"
        const val SECRET_AUTHORIZATION = "Bearer secret-value"
        val JSON_HEADERS = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    }
}
