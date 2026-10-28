package br.com.mykytadu.domain.model.auth

/**
 * Representa o estado reativo da sessão de autenticação do usuário.
 */
sealed interface AuthState {
    data object Initializing : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val user: User) : AuthState
}
