package br.com.mykytadu.data.repository

import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.SyncQueueLocalDataSource
import br.com.mykytadu.data.mapper.toEntity
import br.com.mykytadu.data.mapper.toSyncDto
import br.com.mykytadu.data.remote.api.SyncApi
import br.com.mykytadu.data.remote.dto.sync.SyncBatchRequestDto
import br.com.mykytadu.data.remote.dto.sync.SyncMutationDto
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.sync.SyncStatus
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.repository.SyncRepository
import br.com.mykytadu.domain.result.AuthResult
import br.com.mykytadu.domain.result.SyncResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DefaultSyncRepository(
    private val syncApi: SyncApi,
    private val authRepository: AuthRepository,
    private val libraryLocalDataSource: LibraryLocalDataSource,
    private val syncQueueLocalDataSource: SyncQueueLocalDataSource,
) : SyncRepository {

    private val mutex = Mutex()
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    override val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _lastSyncedEpochMillis = MutableStateFlow<Long?>(null)
    override val lastSyncedEpochMillis: StateFlow<Long?> = _lastSyncedEpochMillis.asStateFlow()

    override val pendingMutationsCount: Flow<Int>
        get() = syncQueueLocalDataSource.observePendingCount()

    override suspend fun synchronize(): SyncResult = mutex.withLock {
        val authState = authRepository.authState.value
        if (authState !is AuthState.Authenticated) {
            _syncStatus.value = SyncStatus.Idle
            return SyncResult.Unauthenticated
        }

        var token = authRepository.currentAccessToken()
        if (token == null) {
            val restored = authRepository.restoreSession()
            if (restored is AuthResult.Success && restored.value != null) {
                token = authRepository.currentAccessToken()
            }
        }

        if (token == null) {
            _syncStatus.value = SyncStatus.Idle
            return SyncResult.Unauthenticated
        }

        _syncStatus.value = SyncStatus.Syncing

        val pending = syncQueueLocalDataSource.getPendingEntries()
        val mutationDtos = pending.map { op ->
            SyncMutationDto(
                animeId = op.animeId,
                type = op.type,
                payload = op.entryEntity?.toSyncDto(),
                timestampEpochMillis = op.timestampEpochMillis,
            )
        }

        val request = SyncBatchRequestDto(
            lastSyncedAtEpochMillis = _lastSyncedEpochMillis.value,
            mutations = mutationDtos,
        )

        val result = syncApi.syncLibrary(accessToken = token, request = request)

        return when (result) {
            is NetworkResult.Success -> {
                val response = result.value

                // 1. Aplicar atualizacoes/insercoes do servidor usando Last-Write-Wins (LWW)
                for (serverChange in response.serverChanges) {
                    val local = libraryLocalDataSource.getEntryByAnimeId(serverChange.animeId)
                    if (local == null) {
                        libraryLocalDataSource.upsertEntry(serverChange.toEntity())
                    } else if (serverChange.updatedAtEpochMillis >= local.updatedAtEpochMillis) {
                        libraryLocalDataSource.upsertEntry(serverChange.toEntity(existingLocalId = local.id))
                    }
                }

                // 2. Aplicar exclusoes do servidor
                for (deletedAnimeId in response.deletedAnimeIds) {
                    val local = libraryLocalDataSource.getEntryByAnimeId(deletedAnimeId)
                    if (local != null && local.updatedAtEpochMillis <= response.syncedAtEpochMillis) {
                        libraryLocalDataSource.deleteEntryByAnimeId(deletedAnimeId)
                    }
                }

                // 3. Remover mutacoes processadas da fila
                if (pending.isNotEmpty()) {
                    syncQueueLocalDataSource.removeEntries(pending.map { it.id })
                }

                _lastSyncedEpochMillis.value = response.syncedAtEpochMillis
                _syncStatus.value = SyncStatus.Success(
                    syncedAtEpochMillis = response.syncedAtEpochMillis,
                    appliedCount = response.appliedCount,
                )

                SyncResult.Success(
                    appliedCount = response.appliedCount,
                    serverChangesCount = response.serverChanges.size,
                )
            }

            is NetworkResult.Failure -> {
                val errorMessage = when (val error = result.error) {
                    is NetworkFailure.Http -> "Erro do servidor (${error.statusCode})"
                    is NetworkFailure.Connection -> "Sem conexão com a internet"
                    is NetworkFailure.Timeout -> "Tempo limite esgotado"
                    else -> "Falha ao sincronizar biblioteca"
                }
                _syncStatus.value = SyncStatus.Error(message = errorMessage, canRetry = true)
                SyncResult.Failure(message = errorMessage, canRetry = true)
            }
        }
    }

    override suspend fun clearPendingQueue() {
        syncQueueLocalDataSource.clear()
    }
}
