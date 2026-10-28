package br.com.mykytadu.features.auth

import br.com.mykytadu.domain.model.auth.AuthFailure
import br.com.mykytadu.domain.model.auth.AuthState

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val isLoading: Boolean = false,
    val error: AuthFailure? = null,
    val authState: AuthState = AuthState.Initializing,
)

enum class AuthMode {
    LOGIN,
    REGISTER
}
