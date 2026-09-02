package br.com.mykytadu.core.network

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

sealed interface NetworkResult<out T> {
    data class Success<T>(val value: T) : NetworkResult<T>

    data class Failure(val error: NetworkFailure) : NetworkResult<Nothing>
}

suspend inline fun <T> safeNetworkCall(block: suspend () -> T): NetworkResult<T> =
    try {
        NetworkResult.Success(block())
    } catch (cause: CancellationException) {
        throw cause
    } catch (cause: HttpRequestTimeoutException) {
        NetworkResult.Failure(NetworkFailure.Timeout(cause))
    } catch (cause: ConnectTimeoutException) {
        NetworkResult.Failure(NetworkFailure.Timeout(cause))
    } catch (cause: SocketTimeoutException) {
        NetworkResult.Failure(NetworkFailure.Timeout(cause))
    } catch (cause: ResponseException) {
        NetworkResult.Failure(
            NetworkFailure.Http(
                statusCode = cause.response.status.value,
                cause = cause,
            )
        )
    } catch (cause: ContentConvertException) {
        NetworkResult.Failure(NetworkFailure.Serialization(cause))
    } catch (cause: SerializationException) {
        NetworkResult.Failure(NetworkFailure.Serialization(cause))
    } catch (cause: IOException) {
        NetworkResult.Failure(NetworkFailure.Connection(cause))
    } catch (cause: Throwable) {
        NetworkResult.Failure(NetworkFailure.Unknown(cause))
    }
