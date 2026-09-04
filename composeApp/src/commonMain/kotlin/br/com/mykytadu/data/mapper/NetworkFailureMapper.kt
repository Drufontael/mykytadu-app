package br.com.mykytadu.data.mapper

import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult

internal fun NetworkFailure.toRepositoryFailure(): RepositoryResult.Failure =
    RepositoryResult.Failure(
        reason = when (this) {
            is NetworkFailure.InvalidRequest -> RepositoryFailure.InvalidInput
            is NetworkFailure.Timeout -> RepositoryFailure.Timeout
            is NetworkFailure.Connection -> RepositoryFailure.Unavailable
            is NetworkFailure.Http -> when {
                statusCode == HTTP_TOO_MANY_REQUESTS -> RepositoryFailure.RateLimited
                statusCode in HTTP_SERVER_ERROR_RANGE -> RepositoryFailure.Unavailable
                else -> RepositoryFailure.RemoteFailure
            }
            is NetworkFailure.GraphQl -> RepositoryFailure.RemoteFailure
            is NetworkFailure.Serialization -> RepositoryFailure.InvalidData
            is NetworkFailure.InvalidResponse -> RepositoryFailure.InvalidData
            is NetworkFailure.Unknown -> RepositoryFailure.Unknown
        },
        cause = cause,
    )

private const val HTTP_TOO_MANY_REQUESTS = 429
private val HTTP_SERVER_ERROR_RANGE = 500..599
