package br.com.mykytadu.domain.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class RepositoryResultTest {

    @Test
    fun `sucesso deve preservar valor`() {
        val result = RepositoryResult.Success("anime")

        assertEquals("anime", result.value)
    }

    @Test
    fun `falha deve preservar categoria e causa`() {
        val cause = IllegalStateException("diagnostic")
        val result = RepositoryResult.Failure(
            reason = RepositoryFailure.InvalidData,
            cause = cause,
        )

        assertEquals(RepositoryFailure.InvalidData, result.reason)
        assertSame(cause, result.cause)
    }

    @Test
    fun `falha deve aceitar causa ausente`() {
        val result = RepositoryResult.Failure(
            reason = RepositoryFailure.Unavailable,
        )

        assertNull(result.cause)
    }

    @Test
    fun `resultado deve ser covariante`() {
        val textResult: RepositoryResult<String> = RepositoryResult.Success("anime")
        val anyResult: RepositoryResult<Any> = textResult

        assertEquals("anime", (anyResult as RepositoryResult.Success).value)
    }
}
