package br.com.mykytadu.domain.repository

import br.com.mykytadu.domain.model.sync.SyncStatus
import br.com.mykytadu.domain.result.SyncResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SyncRepository {
    val syncStatus: StateFlow<SyncStatus>
    val lastSyncedEpochMillis: StateFlow<Long?>
    val pendingMutationsCount: Flow<Int>

    suspend fun synchronize(): SyncResult
    suspend fun clearPendingQueue()
}
