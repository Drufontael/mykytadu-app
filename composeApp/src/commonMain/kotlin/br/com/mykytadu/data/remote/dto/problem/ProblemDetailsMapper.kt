package br.com.mykytadu.data.remote.dto.problem

import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.domain.model.auth.AuthFailure
import kotlinx.serialization.json.Json

private val problemJson = Json { ignoreUnknownKeys = true }

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

/**
 * Converte um NetworkFailure em AuthFailure de domínio.
 * Se houver corpo de resposta contendo ProblemDetailsDto (RFC 9457), realiza o parse
 * e delega para toAuthFailure(statusCode).
 */
fun NetworkFailure.toAuthFailure(): AuthFailure = when (this) {
    is NetworkFailure.Http -> {
        val problem = responseBody?.let { body ->
            try {
                problemJson.decodeFromString<ProblemDetailsDto>(body)
            } catch (_: Throwable) {
                null
            }
        }
        problem?.toAuthFailure(statusCode) ?: when {
            statusCode == 401 || statusCode == 403 -> AuthFailure.InvalidCredentials(message = "Credenciais inválidas.")
            statusCode == 429 -> AuthFailure.RateLimitExceeded()
            statusCode in 500..599 -> AuthFailure.ServerError(status = statusCode, detail = "Erro de servidor ($statusCode).")
            else -> AuthFailure.ServerError(status = statusCode, detail = "Erro HTTP ($statusCode).")
        }
    }
    is NetworkFailure.Timeout -> AuthFailure.NetworkError(cause)
    is NetworkFailure.Connection -> AuthFailure.NetworkError(cause)
    is NetworkFailure.Serialization -> AuthFailure.ServerError(status = 500, detail = "Falha ao processar resposta do servidor.")
    is NetworkFailure.InvalidRequest -> AuthFailure.Unknown(cause)
    is NetworkFailure.InvalidResponse -> AuthFailure.ServerError(status = 500, detail = message)
    is NetworkFailure.GraphQl -> AuthFailure.ServerError(status = 500, detail = "Erro no serviço remoto.")
    is NetworkFailure.Unknown -> AuthFailure.Unknown(cause)
}
