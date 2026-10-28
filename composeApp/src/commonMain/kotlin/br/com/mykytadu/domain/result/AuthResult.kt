package br.com.mykytadu.domain.result

import br.com.mykytadu.domain.model.auth.AuthFailure

/**
 * Resultado padronizado para operações da camada de autenticação.
 */
sealed interface AuthResult<out T> {
    data class Success<T>(val value: T) : AuthResult<T>
    data class Failure(val failure: AuthFailure, val cause: Throwable? = null) : AuthResult<Nothing>
}
