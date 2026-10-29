package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.SyncQueueEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de fonte de dados local para fila de operações pendentes de sincronização.
 */
interface SyncQueueLocalDataSource {

    suspend fun enqueue(entry: SyncQueueEntryEntity)

    suspend fun getPendingEntries(): List<SyncQueueEntryEntity>

    suspend fun removeEntries(ids: List<String>)

    suspend fun clear()

    fun observePendingCount(): Flow<Int>
}
