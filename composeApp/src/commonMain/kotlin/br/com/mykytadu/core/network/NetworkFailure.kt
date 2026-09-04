package br.com.mykytadu.core.network

sealed interface NetworkFailure {
    val cause: Throwable

    data class Timeout(override val cause: Throwable) : NetworkFailure

    data class Connection(override val cause: Throwable) : NetworkFailure

    data class Http(
        val statusCode: Int,
        override val cause: Throwable,
    ) : NetworkFailure

    data class Serialization(override val cause: Throwable) : NetworkFailure

    data class GraphQl(val messages: List<String>, override val cause: Throwable) : NetworkFailure

    data class InvalidRequest(val message: String, override val cause: Throwable) : NetworkFailure

    data class InvalidResponse(val message: String, override val cause: Throwable) : NetworkFailure
    data class Unknown(override val cause: Throwable) : NetworkFailure
}
