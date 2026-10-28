package br.com.mykytadu.data.repository

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.local.datasource.SecureStorageDriver
import br.com.mykytadu.data.remote.api.AuthApi
import br.com.mykytadu.data.remote.dto.auth.LoginRequestDto
import br.com.mykytadu.data.remote.dto.auth.LogoutRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshRequestDto
import br.com.mykytadu.data.remote.dto.auth.RegisterRequestDto
import br.com.mykytadu.data.remote.dto.auth.toDomain
import br.com.mykytadu.data.remote.dto.problem.toAuthFailure
import br.com.mykytadu.domain.model.auth.AuthFailure
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.auth.User
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.result.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultAuthRepository(
    private val authApi: AuthApi,
    private val secureStorageDriver: SecureStorageDriver,
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var activeAccessToken: String? = null

    override fun currentAccessToken(): String? = activeAccessToken

    override suspend fun login(email: String, password: String): AuthResult<User> {
        val normalizedEmail = email.trim()
        if (normalizedEmail.isEmpty() || password.isEmpty()) {
            return AuthResult.Failure(AuthFailure.InvalidCredentials("E-mail e senha são obrigatórios."))
        }

        val result = authApi.login(
            LoginRequestDto(
                email = normalizedEmail,
                password = password,
            )
        )

        return when (result) {
            is NetworkResult.Success -> {
                val response = result.value
                activeAccessToken = response.accessToken
                response.refreshToken?.let { token ->
                    secureStorageDriver.saveRefreshToken(token)
                }

                if (response.user != null) {
                    val user = response.user.toDomain()
                    _authState.value = AuthState.Authenticated(user)
                    AuthResult.Success(user)
                } else {
                    when (val profileResult = authApi.getProfile(response.accessToken)) {
                        is NetworkResult.Success -> {
                            val user = profileResult.value.toDomain()
                            _authState.value = AuthState.Authenticated(user)
                            AuthResult.Success(user)
                        }
                        is NetworkResult.Failure -> {
                            val failure = profileResult.error.toAuthFailure()
                            activeAccessToken = null
                            _authState.value = AuthState.Unauthenticated
                            AuthResult.Failure(failure)
                        }
                    }
                }
            }
            is NetworkResult.Failure -> {
                val failure = result.error.toAuthFailure()
                if (_authState.value !is AuthState.Authenticated) {
                    _authState.value = AuthState.Unauthenticated
                }
                AuthResult.Failure(failure)
            }
        }
    }

    override suspend fun register(email: String, password: String, name: String): AuthResult<User> {
        val normalizedEmail = email.trim()
        val normalizedName = name.trim()
        if (normalizedEmail.isEmpty() || password.isEmpty() || normalizedName.isEmpty()) {
            return AuthResult.Failure(AuthFailure.RegistrationRejected("Todos os campos são obrigatórios."))
        }

        val result = authApi.register(
            RegisterRequestDto(
                email = normalizedEmail,
                password = password,
                name = normalizedName,
            )
        )

        return when (result) {
            is NetworkResult.Success -> {
                login(email = normalizedEmail, password = password)
            }
            is NetworkResult.Failure -> {
                val failure = result.error.toAuthFailure()
                AuthResult.Failure(failure)
            }
        }
    }

    override suspend fun logout(): AuthResult<Unit> {
        val storedRefreshToken = secureStorageDriver.getRefreshToken()
        try {
            authApi.logout(LogoutRequestDto(refreshToken = storedRefreshToken))
        } catch (_: Throwable) {
            // Logout remoto é best-effort; prosseguir com limpeza local da sessão
        }

        secureStorageDriver.clearRefreshToken()
        activeAccessToken = null
        _authState.value = AuthState.Unauthenticated
        return AuthResult.Success(Unit)
    }

    override suspend fun restoreSession(): AuthResult<User?> {
        val refreshToken = secureStorageDriver.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            activeAccessToken = null
            _authState.value = AuthState.Unauthenticated
            return AuthResult.Success(null)
        }

        val refreshResult = authApi.refresh(RefreshRequestDto(refreshToken = refreshToken))
        return when (refreshResult) {
            is NetworkResult.Success -> {
                val response = refreshResult.value
                activeAccessToken = response.accessToken
                response.refreshToken?.let { rotatedToken ->
                    secureStorageDriver.saveRefreshToken(rotatedToken)
                }

                when (val profileResult = authApi.getProfile(response.accessToken)) {
                    is NetworkResult.Success -> {
                        val user = profileResult.value.toDomain()
                        _authState.value = AuthState.Authenticated(user)
                        AuthResult.Success(user)
                    }
                    is NetworkResult.Failure -> {
                        val failure = profileResult.error.toAuthFailure()
                        if (failure is AuthFailure.SessionExpired || failure is AuthFailure.InvalidCredentials) {
                            secureStorageDriver.clearRefreshToken()
                            activeAccessToken = null
                            _authState.value = AuthState.Unauthenticated
                            AuthResult.Success(null)
                        } else {
                            _authState.value = AuthState.Unauthenticated
                            AuthResult.Failure(failure)
                        }
                    }
                }
            }
            is NetworkResult.Failure -> {
                val failure = refreshResult.error.toAuthFailure()
                if (failure is AuthFailure.SessionExpired || failure is AuthFailure.InvalidCredentials) {
                    secureStorageDriver.clearRefreshToken()
                    activeAccessToken = null
                    _authState.value = AuthState.Unauthenticated
                    AuthResult.Success(null)
                } else {
                    _authState.value = AuthState.Unauthenticated
                    AuthResult.Failure(failure)
                }
            }
        }
    }
}
