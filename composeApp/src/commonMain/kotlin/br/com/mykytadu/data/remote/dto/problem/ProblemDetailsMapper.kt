package br.com.mykytadu.data.remote.dto.problem

import br.com.mykytadu.domain.model.auth.AuthFailure

/**
 * Converte um ProblemDetailsDto (RFC 9457) em AuthFailure de domínio
 * priorizando estritamente o código canônico estável (code), nunca a mensagem livre (detail).
 */
fun ProblemDetailsDto.toAuthFailure(httpStatusCode: Int): AuthFailure {
    val effectiveStatus = status ?: httpStatusCode
    return when (code) {
        "invalid_credentials" -> AuthFailure.InvalidCredentials(detail)
        "email_verification_required" -> AuthFailure.EmailVerificationRequired(detail)
        "registration_rejected" -> AuthFailure.RegistrationRejected(detail)
        "session_invalid", "authentication_required" -> AuthFailure.SessionExpired(detail)
        "rate_limit_exceeded" -> AuthFailure.RateLimitExceeded()
        else -> {
            if (effectiveStatus == 429) {
                AuthFailure.RateLimitExceeded()
            } else if (effectiveStatus in 500..599) {
                AuthFailure.ServerError(status = effectiveStatus, code = code, detail = detail)
            } else {
                AuthFailure.ServerError(status = effectiveStatus, code = code, detail = detail)
            }
        }
    }
}
