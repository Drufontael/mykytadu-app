package br.com.mykytadu.data.remote.dto.problem

import br.com.mykytadu.domain.model.auth.AuthFailure
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProblemDetailsMapperTest {

    @Test
    fun `mapeia invalid_credentials para AuthFailure InvalidCredentials`() {
        val dto = ProblemDetailsDto(
            type = "https://api.mykytadu.com/errors/invalid-credentials",
            status = 401,
            code = "invalid_credentials",
            detail = "Credenciais inválidas",
        )
        val failure = dto.toAuthFailure(401)
        assertTrue(failure is AuthFailure.InvalidCredentials)
        assertEquals("Credenciais inválidas", failure.message)
    }

    @Test
    fun `mapeia email_verification_required para AuthFailure EmailVerificationRequired`() {
        val dto = ProblemDetailsDto(
            status = 401,
            code = "email_verification_required",
            detail = "E-mail não verificado",
        )
        val failure = dto.toAuthFailure(401)
        assertTrue(failure is AuthFailure.EmailVerificationRequired)
        assertEquals("E-mail não verificado", failure.message)
    }

    @Test
    fun `mapeia registration_rejected para AuthFailure RegistrationRejected`() {
        val dto = ProblemDetailsDto(
            status = 409,
            code = "registration_rejected",
            detail = "Cadastro rejeitado",
        )
        val failure = dto.toAuthFailure(409)
        assertTrue(failure is AuthFailure.RegistrationRejected)
        assertEquals("Cadastro rejeitado", failure.message)
    }

    @Test
    fun `mapeia session_invalid e authentication_required para AuthFailure SessionExpired`() {
        val dto1 = ProblemDetailsDto(status = 401, code = "session_invalid", detail = "Sessão expirada")
        val dto2 = ProblemDetailsDto(status = 401, code = "authentication_required", detail = "Autenticação requerida")

        val f1 = dto1.toAuthFailure(401)
        val f2 = dto2.toAuthFailure(401)

        assertTrue(f1 is AuthFailure.SessionExpired)
        assertTrue(f2 is AuthFailure.SessionExpired)
    }

    @Test
    fun `mapeia rate_limit_exceeded ou status 429 para AuthFailure RateLimitExceeded`() {
        val dto1 = ProblemDetailsDto(status = 429, code = "rate_limit_exceeded")
        val dto2 = ProblemDetailsDto(status = 429, code = "other_code")

        assertTrue(dto1.toAuthFailure(429) is AuthFailure.RateLimitExceeded)
        assertTrue(dto2.toAuthFailure(429) is AuthFailure.RateLimitExceeded)
    }

    @Test
    fun `mapeia erros de servidor 5xx para AuthFailure ServerError`() {
        val dto = ProblemDetailsDto(
            status = 500,
            code = "internal_error",
            detail = "Erro interno no servidor",
        )
        val failure = dto.toAuthFailure(500)
        assertTrue(failure is AuthFailure.ServerError)
        assertEquals(500, failure.status)
        assertEquals("internal_error", failure.code)
    }
}
