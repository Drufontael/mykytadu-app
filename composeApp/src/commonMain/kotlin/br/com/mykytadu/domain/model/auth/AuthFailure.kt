package br.com.mykytadu.domain.model.auth

/**
 * Representa falhas padronizadas do domínio de autenticação mapeadas a partir
 * de códigos estáveis da API (RFC 9457) ou falhas de transporte/sessão.
 */
sealed interface AuthFailure {
    data class InvalidCredentials(val message: String? = null) : AuthFailure
    data class EmailVerificationRequired(val message: String? = null) : AuthFailure
    data class RegistrationRejected(val message: String? = null) : AuthFailure
    data class SessionExpired(val message: String? = null) : AuthFailure
    data class RateLimitExceeded(val retryAfterSeconds: Long? = null) : AuthFailure
    data class ServerError(val status: Int, val code: String? = null, val detail: String? = null) : AuthFailure
    data class NetworkError(val cause: Throwable? = null) : AuthFailure
    data class Unknown(val cause: Throwable? = null) : AuthFailure
}
