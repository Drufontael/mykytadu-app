package br.com.mykytadu.data.local.entity

import br.com.mykytadu.data.remote.dto.sync.SyncMutationType
import kotlinx.serialization.Serializable

/**
 * Representa uma operação pendente na fila local de sincronização.
 * Registra inserções, atualizações ou exclusões que devem ser replicadas no backend.
 */
@Serializable
data class SyncQueueEntryEntity(
    val id: String,
    val animeId: String,
    val type: SyncMutationType,
    val entryEntity: LibraryEntryEntity? = null,
    val timestampEpochMillis: Long,
)
