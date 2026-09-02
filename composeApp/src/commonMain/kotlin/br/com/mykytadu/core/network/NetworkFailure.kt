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

    data class Unknown(override val cause: Throwable) : NetworkFailure
}
