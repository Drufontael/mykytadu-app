package br.com.mykytadu.data.repository

import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.local.datasource.InMemorySecureStorageDriver
import br.com.mykytadu.data.remote.api.AuthApi
import br.com.mykytadu.data.remote.dto.auth.LoginRequestDto
import br.com.mykytadu.data.remote.dto.auth.LoginResponseDto
import br.com.mykytadu.data.remote.dto.auth.LogoutRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshResponseDto
import br.com.mykytadu.data.remote.dto.auth.RegisterRequestDto
import br.com.mykytadu.data.remote.dto.auth.RegisterResponseDto
import br.com.mykytadu.data.remote.dto.auth.UserDto
import br.com.mykytadu.domain.model.auth.AuthFailure
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.auth.UserId
import br.com.mykytadu.domain.result.AuthResult
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class DefaultAuthRepositoryTest {

    @Test
    fun `login com sucesso persiste refreshToken e atualiza authState para Authenticated`() = runTest {
        val storage = InMemorySecureStorageDriver()
        val fakeApi = FakeAuthApi().apply {
            loginResult = NetworkResult.Success(
                LoginResponseDto(
                    accessToken = "access-token-1",
                    refreshToken = "refresh-token-1",
                    expiresIn = 600,
                    user = UserDto(id = "u1", email = "test@example.com", name = "Test User")
                )
            )
        }
        val repository = DefaultAuthRepository(fakeApi, storage)

        val result = repository.login("test@example.com", "password")

        val success = assertIs<AuthResult.Success<*>>(result)
        val user = assertIs<br.com.mykytadu.domain.model.auth.User>(success.value)
        assertEquals(UserId("u1"), user.id)
        assertEquals("test@example.com", user.email)
        assertEquals("refresh-token-1", storage.getRefreshToken())
        assertEquals("access-token-1", repository.currentAccessToken())
        val authState = assertIs<AuthState.Authenticated>(repository.authState.value)
        assertEquals(user, authState.user)
    }

    @Test
    fun `login com credenciais invalidas retorna Failure e define Unauthenticated`() = runTest {
        val storage = InMemorySecureStorageDriver()
        val fakeApi = FakeAuthApi().apply {
            loginResult = NetworkResult.Failure(
                NetworkFailure.Http(
                    statusCode = 401,
                    cause = IllegalStateException("Unauthorized"),
                    responseBody = """{"code":"invalid_credentials","detail":"Senha incorreta."}"""
                )
            )
        }
        val repository = DefaultAuthRepository(fakeApi, storage)

        val result = repository.login("test@example.com", "wrong")

        val failure = assertIs<AuthResult.Failure>(result)
        assertIs<AuthFailure.InvalidCredentials>(failure.failure)
        assertNull(repository.currentAccessToken())
        assertIs<AuthState.Unauthenticated>(repository.authState.value)
    }

    @Test
    fun `login sem user no DTO busca perfil via getProfile`() = runTest {
        val storage = InMemorySecureStorageDriver()
        val fakeApi = FakeAuthApi().apply {
            loginResult = NetworkResult.Success(
                LoginResponseDto(
                    accessToken = "token-no-user",
                    refreshToken = "refresh-token-2",
                    expiresIn = 600,
                    user = null
                )
            )
            profileResult = NetworkResult.Success(
                UserDto(id = "u2", email = "profile@example.com", name = "Profile User")
            )
        }
        val repository = DefaultAuthRepository(fakeApi, storage)

        val result = repository.login("profile@example.com", "password")

        val success = assertIs<AuthResult.Success<*>>(result)
        val user = assertIs<br.com.mykytadu.domain.model.auth.User>(success.value)
        assertEquals(UserId("u2"), user.id)
        assertIs<AuthState.Authenticated>(repository.authState.value)
    }

    @Test
    fun `register com sucesso invoca login e conclui autenticado`() = runTest {
        val storage = InMemorySecureStorageDriver()
        val fakeApi = FakeAuthApi().apply {
            registerResult = NetworkResult.Success(
                RegisterResponseDto(id = "u3", email = "new@example.com", name = "New User")
            )
            loginResult = NetworkResult.Success(
                LoginResponseDto(
                    accessToken = "token-reg",
                    refreshToken = "refresh-reg",
                    expiresIn = 600,
                    user = UserDto(id = "u3", email = "new@example.com", name = "New User")
                )
            )
        }
        val repository = DefaultAuthRepository(fakeApi, storage)

        val result = repository.register("new@example.com", "password", "New User")

        val success = assertIs<AuthResult.Success<*>>(result)
        val user = assertIs<br.com.mykytadu.domain.model.auth.User>(success.value)
        assertEquals(UserId("u3"), user.id)
        assertIs<AuthState.Authenticated>(repository.authState.value)
        assertEquals("refresh-reg", storage.getRefreshToken())
    }

    @Test
    fun `register com erro RFC 9457 retorna AuthFailure RegistrationRejected`() = runTest {
        val storage = InMemorySecureStorageDriver()
        val fakeApi = FakeAuthApi().apply {
            registerResult = NetworkResult.Failure(
                NetworkFailure.Http(
                    statusCode = 409,
                    cause = IllegalStateException("Conflict"),
                    responseBody = """{"code":"registration_rejected","detail":"Email ja cadastrado."}"""
                )
            )
        }
        val repository = DefaultAuthRepository(fakeApi, storage)

        val result = repository.register("exists@example.com", "password", "Name")

        val failure = assertIs<AuthResult.Failure>(result)
        assertIs<AuthFailure.RegistrationRejected>(failure.failure)
    }

    @Test
    fun `logout limpa storage e redefine estado para Unauthenticated`() = runTest {
        val storage = InMemorySecureStorageDriver().apply {
            saveRefreshToken("token-to-be-cleared")
        }
        val fakeApi = FakeAuthApi()
        val repository = DefaultAuthRepository(fakeApi, storage)

        val result = repository.logout()

        assertIs<AuthResult.Success<Unit>>(result)
        assertNull(storage.getRefreshToken())
        assertNull(repository.currentAccessToken())
        assertIs<AuthState.Unauthenticated>(repository.authState.value)
    }

    @Test
    fun `restoreSession sem refreshToken no storage retorna null e Unauthenticated`() = runTest {
        val storage = InMemorySecureStorageDriver()
        val fakeApi = FakeAuthApi()
        val repository = DefaultAuthRepository(fakeApi, storage)

        val result = repository.restoreSession()

        val success = assertIs<AuthResult.Success<*>>(result)
        assertNull(success.value)
        assertIs<AuthState.Unauthenticated>(repository.authState.value)
    }

    @Test
    fun `restoreSession com refresh token valido rotaciona token e autentica`() = runTest {
        val storage = InMemorySecureStorageDriver().apply {
            saveRefreshToken("initial-refresh-token")
        }
        val fakeApi = FakeAuthApi().apply {
            refreshResult = NetworkResult.Success(
                RefreshResponseDto(
                    accessToken = "rotated-access-token",
                    refreshToken = "new-rotated-refresh-token",
                    expiresIn = 600
                )
            )
            profileResult = NetworkResult.Success(
                UserDto(id = "u4", email = "restored@example.com", name = "Restored User")
            )
        }
        val repository = DefaultAuthRepository(fakeApi, storage)

        val result = repository.restoreSession()

        val success = assertIs<AuthResult.Success<*>>(result)
        val user = assertIs<br.com.mykytadu.domain.model.auth.User>(success.value)
        assertEquals(UserId("u4"), user.id)
        assertEquals("new-rotated-refresh-token", storage.getRefreshToken())
        assertEquals("rotated-access-token", repository.currentAccessToken())
        assertIs<AuthState.Authenticated>(repository.authState.value)
    }

    @Test
    fun `restoreSession com token expirado limpa storage e retorna null`() = runTest {
        val storage = InMemorySecureStorageDriver().apply {
            saveRefreshToken("expired-token")
        }
        val fakeApi = FakeAuthApi().apply {
            refreshResult = NetworkResult.Failure(
                NetworkFailure.Http(
                    statusCode = 401,
                    cause = IllegalStateException("Token expired"),
                    responseBody = """{"code":"session_invalid","detail":"Sessao expirada."}"""
                )
            )
        }
        val repository = DefaultAuthRepository(fakeApi, storage)

        val result = repository.restoreSession()

        val success = assertIs<AuthResult.Success<*>>(result)
        assertNull(success.value)
        assertNull(storage.getRefreshToken())
        assertNull(repository.currentAccessToken())
        assertIs<AuthState.Unauthenticated>(repository.authState.value)
    }

    @Test
    fun `restoreSession com falha de rede nao limpa refresh token do storage`() = runTest {
        val storage = InMemorySecureStorageDriver().apply {
            saveRefreshToken("valid-offline-token")
        }
        val fakeApi = FakeAuthApi().apply {
            refreshResult = NetworkResult.Failure(
                NetworkFailure.Connection(IOException("Sem conexao"))
            )
        }
        val repository = DefaultAuthRepository(fakeApi, storage)

        val result = repository.restoreSession()

        val failure = assertIs<AuthResult.Failure>(result)
        assertIs<AuthFailure.NetworkError>(failure.failure)
        assertEquals("valid-offline-token", storage.getRefreshToken())
        assertIs<AuthState.Unauthenticated>(repository.authState.value)
    }

    private class FakeAuthApi : AuthApi {
        var loginResult: NetworkResult<LoginResponseDto> = NetworkResult.Failure(NetworkFailure.Unknown(IllegalStateException()))
        var registerResult: NetworkResult<RegisterResponseDto> = NetworkResult.Failure(NetworkFailure.Unknown(IllegalStateException()))
        var refreshResult: NetworkResult<RefreshResponseDto> = NetworkResult.Failure(NetworkFailure.Unknown(IllegalStateException()))
        var logoutResult: NetworkResult<Unit> = NetworkResult.Success(Unit)
        var profileResult: NetworkResult<UserDto> = NetworkResult.Failure(NetworkFailure.Unknown(IllegalStateException()))

        override suspend fun login(request: LoginRequestDto): NetworkResult<LoginResponseDto> = loginResult
        override suspend fun register(request: RegisterRequestDto): NetworkResult<RegisterResponseDto> = registerResult
        override suspend fun refresh(request: RefreshRequestDto): NetworkResult<RefreshResponseDto> = refreshResult
        override suspend fun logout(request: LogoutRequestDto): NetworkResult<Unit> = logoutResult
        override suspend fun getProfile(accessToken: String): NetworkResult<UserDto> = profileResult
    }
}
