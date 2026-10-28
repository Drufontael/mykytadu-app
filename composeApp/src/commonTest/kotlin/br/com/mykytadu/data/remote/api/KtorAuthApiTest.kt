package br.com.mykytadu.data.remote.api

import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.remote.dto.auth.LoginRequestDto
import br.com.mykytadu.data.remote.dto.auth.LogoutRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshRequestDto
import br.com.mykytadu.data.remote.dto.auth.RegisterRequestDto
import br.com.mykytadu.di.configureSharedHttpClient
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
import kotlin.test.assertNotNull

class KtorAuthApiTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

    @Test
    fun `login bem-sucedido retorna LoginResponseDto com tokens e usuario`() = runTest {
        val responseJson = """
            {
              "accessToken": "access-123",
              "tokenType": "Bearer",
              "expiresIn": 600,
              "refreshToken": "refresh-123",
              "user": {
                "id": "usr-1",
                "email": "user@example.com",
                "name": "User One"
              }
            }
        """.trimIndent()

        val engine = MockEngine { request ->
            assertEquals("/api/v1/auth/login", request.url.encodedPath)
            respond(responseJson, HttpStatusCode.OK, jsonHeaders)
        }
        val api = KtorAuthApi(createTestClient(engine))

        val result = api.login(LoginRequestDto(email = "user@example.com", password = "password123"))

        val success = assertIs<NetworkResult.Success<*>>(result)
        val loginResponse = assertIs<br.com.mykytadu.data.remote.dto.auth.LoginResponseDto>(success.value)
        assertEquals("access-123", loginResponse.accessToken)
        assertEquals("refresh-123", loginResponse.refreshToken)
        assertEquals("usr-1", loginResponse.user?.id)
        assertEquals("User One", loginResponse.user?.name)
    }

    @Test
    fun `login com credenciais invalidas retorna NetworkFailure Http com corpo RFC 9457`() = runTest {
        val problemJson = """
            {
              "type": "https://api.mykytadu.com/errors/invalid-credentials",
              "title": "Invalid Credentials",
              "status": 401,
              "code": "invalid_credentials",
              "detail": "Credenciais informadas estao incorretas."
            }
        """.trimIndent()

        val engine = MockEngine {
            respond(problemJson, HttpStatusCode.Unauthorized, jsonHeaders)
        }
        val api = KtorAuthApi(createTestClient(engine))

        val result = api.login(LoginRequestDto(email = "wrong@example.com", password = "bad"))

        val failure = assertIs<NetworkResult.Failure>(result)
        val httpFailure = assertIs<NetworkFailure.Http>(failure.error)
        assertEquals(401, httpFailure.statusCode)
        assertNotNull(httpFailure.responseBody)
        assertEquals(true, httpFailure.responseBody.contains("invalid_credentials"))
    }

    @Test
    fun `register bem-sucedido envia payload correto e retorna RegisterResponseDto`() = runTest {
        val responseJson = """
            {
              "id": "usr-2",
              "email": "novo@example.com",
              "name": "Novo Usuario"
            }
        """.trimIndent()

        val engine = MockEngine { request ->
            assertEquals("/api/v1/auth/register", request.url.encodedPath)
            respond(responseJson, HttpStatusCode.Created, jsonHeaders)
        }
        val api = KtorAuthApi(createTestClient(engine))

        val result = api.register(RegisterRequestDto(email = "novo@example.com", password = "pass", name = "Novo Usuario"))

        val success = assertIs<NetworkResult.Success<*>>(result)
        val regResponse = assertIs<br.com.mykytadu.data.remote.dto.auth.RegisterResponseDto>(success.value)
        assertEquals("usr-2", regResponse.id)
        assertEquals("novo@example.com", regResponse.email)
    }

    @Test
    fun `refresh bem-sucedido retorna novo accessToken e refreshToken rotacionado`() = runTest {
        val responseJson = """
            {
              "accessToken": "new-access-456",
              "tokenType": "Bearer",
              "expiresIn": 600,
              "refreshToken": "rotated-refresh-456"
            }
        """.trimIndent()

        val engine = MockEngine { request ->
            assertEquals("/api/v1/auth/refresh", request.url.encodedPath)
            respond(responseJson, HttpStatusCode.OK, jsonHeaders)
        }
        val api = KtorAuthApi(createTestClient(engine))

        val result = api.refresh(RefreshRequestDto(refreshToken = "old-refresh-123"))

        val success = assertIs<NetworkResult.Success<*>>(result)
        val refreshResponse = assertIs<br.com.mykytadu.data.remote.dto.auth.RefreshResponseDto>(success.value)
        assertEquals("new-access-456", refreshResponse.accessToken)
        assertEquals("rotated-refresh-456", refreshResponse.refreshToken)
    }

    @Test
    fun `logout envia refreshToken e conclui com sucesso`() = runTest {
        var called = false
        val engine = MockEngine { request ->
            assertEquals("/api/v1/auth/logout", request.url.encodedPath)
            called = true
            respond("{}", HttpStatusCode.OK, jsonHeaders)
        }
        val api = KtorAuthApi(createTestClient(engine))

        val result = api.logout(LogoutRequestDto(refreshToken = "token-to-invalidate"))

        assertIs<NetworkResult.Success<Unit>>(result)
        assertEquals(true, called)
    }

    @Test
    fun `getProfile envia header Authorization e retorna UserDto`() = runTest {
        val userJson = """
            {
              "id": "usr-1",
              "email": "user@example.com",
              "name": "User One",
              "createdAtEpochMillis": 1700000000000
            }
        """.trimIndent()

        var authHeaderReceived: String? = null
        val engine = MockEngine { request ->
            assertEquals("/api/v1/me", request.url.encodedPath)
            authHeaderReceived = request.headers[HttpHeaders.Authorization]
            respond(userJson, HttpStatusCode.OK, jsonHeaders)
        }
        val api = KtorAuthApi(createTestClient(engine))

        val result = api.getProfile("valid-access-token")

        val success = assertIs<NetworkResult.Success<*>>(result)
        val userDto = assertIs<br.com.mykytadu.data.remote.dto.auth.UserDto>(success.value)
        assertEquals("Bearer valid-access-token", authHeaderReceived)
        assertEquals("usr-1", userDto.id)
        assertEquals("User One", userDto.name)
    }

    private fun createTestClient(engine: MockEngine): HttpClient = HttpClient(engine) {
        configureSharedHttpClient(
            networkLogger = Logger.EMPTY,
            loggingEnabled = false,
        )
    }
}
