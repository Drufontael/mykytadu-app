package br.com.mykytadu.domain.repository

import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.auth.User
import br.com.mykytadu.domain.result.AuthResult
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>
    suspend fun login(email: String, password: String): AuthResult<User>
    suspend fun register(email: String, password: String, name: String): AuthResult<User>
    suspend fun logout(): AuthResult<Unit>
    suspend fun restoreSession(): AuthResult<User?>
    fun currentAccessToken(): String?
}
