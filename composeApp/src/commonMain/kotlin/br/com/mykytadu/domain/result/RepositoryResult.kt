package br.com.mykytadu.domain.result

sealed interface RepositoryResult<out T> {
    data class Success<T>(
        val value: T,
    ) : RepositoryResult<T>

    data class Failure(
        val reason: RepositoryFailure,
        val cause: Throwable? = null,
    ) : RepositoryResult<Nothing>
}

sealed interface RepositoryFailure {
    data object InvalidInput : RepositoryFailure
    data object Unavailable : RepositoryFailure
    data object Timeout : RepositoryFailure
    data object RateLimited : RepositoryFailure
    data object InvalidData : RepositoryFailure
    data object RemoteFailure : RepositoryFailure
    data object Unknown : RepositoryFailure
}
