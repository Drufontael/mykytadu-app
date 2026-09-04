package br.com.mykytadu.data.mapper

import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.domain.result.RepositoryFailure
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class NetworkFailureMapperTest {

    @Test
    fun `deve converter requisicao invalida`() = assertMapping(
        failure = NetworkFailure.InvalidRequest("invalid", TestException()),
        expected = RepositoryFailure.InvalidInput,
    )

    @Test
    fun `deve converter timeout`() = assertMapping(
        failure = NetworkFailure.Timeout(TestException()),
        expected = RepositoryFailure.Timeout,
    )

    @Test
    fun `deve converter falha de conexao`() = assertMapping(
        failure = NetworkFailure.Connection(TestException()),
        expected = RepositoryFailure.Unavailable,
    )

    @Test
    fun `deve converter HTTP 429 em limite de requisicoes`() = assertMapping(
        failure = NetworkFailure.Http(429, TestException()),
        expected = RepositoryFailure.RateLimited,
    )

    @Test
    fun `deve converter HTTP 500 em indisponibilidade`() = assertMapping(
        failure = NetworkFailure.Http(500, TestException()),
        expected = RepositoryFailure.Unavailable,
    )

    @Test
    fun `deve converter outro HTTP em falha remota`() = assertMapping(
        failure = NetworkFailure.Http(404, TestException()),
        expected = RepositoryFailure.RemoteFailure,
    )

    @Test
    fun `deve converter serializacao em dados invalidos`() = assertMapping(
        failure = NetworkFailure.Serialization(TestException()),
        expected = RepositoryFailure.InvalidData,
    )

    @Test
    fun `deve converter resposta invalida em dados invalidos`() = assertMapping(
        failure = NetworkFailure.InvalidResponse("invalid", TestException()),
        expected = RepositoryFailure.InvalidData,
    )

    @Test
    fun `deve converter GraphQL em falha remota`() = assertMapping(
        failure = NetworkFailure.GraphQl(listOf("technical"), TestException()),
        expected = RepositoryFailure.RemoteFailure,
    )

    @Test
    fun `deve converter falha desconhecida`() = assertMapping(
        failure = NetworkFailure.Unknown(TestException()),
        expected = RepositoryFailure.Unknown,
    )

    private fun assertMapping(
        failure: NetworkFailure,
        expected: RepositoryFailure,
    ) {
        val result = failure.toRepositoryFailure()

        assertEquals(expected, result.reason)
        assertSame(failure.cause, result.cause)
    }

    private class TestException : Exception()
}
