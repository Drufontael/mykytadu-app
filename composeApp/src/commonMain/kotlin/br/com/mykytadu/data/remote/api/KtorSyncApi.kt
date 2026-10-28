package br.com.mykytadu.data.remote.api

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.core.network.safeNetworkCall
import br.com.mykytadu.data.remote.dto.sync.SyncBatchRequestDto
import br.com.mykytadu.data.remote.dto.sync.SyncBatchResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders

class KtorSyncApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = KtorAuthApi.DEFAULT_BASE_URL,
) : SyncApi {

    override suspend fun syncLibrary(
        accessToken: String,
        request: SyncBatchRequestDto,
    ): NetworkResult<SyncBatchResponseDto> =
        safeNetworkCall {
            httpClient.post("$baseUrl/api/v1/sync/library") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                setBody(request)
            }.body<SyncBatchResponseDto>()
        }
}
