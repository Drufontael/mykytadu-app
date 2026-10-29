package br.com.mykytadu.data.remote.api

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.remote.dto.sync.SyncBatchRequestDto
import br.com.mykytadu.data.remote.dto.sync.SyncBatchResponseDto

interface SyncApi {
    suspend fun syncLibrary(
        accessToken: String,
        request: SyncBatchRequestDto,
    ): NetworkResult<SyncBatchResponseDto>
}
