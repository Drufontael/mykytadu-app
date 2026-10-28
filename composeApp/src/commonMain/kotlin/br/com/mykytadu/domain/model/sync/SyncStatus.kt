package br.com.mykytadu.domain.model.sync

sealed interface SyncStatus {
    data object Idle : SyncStatus
    data object Syncing : SyncStatus
    data class Success(val syncedAtEpochMillis: Long, val appliedCount: Int) : SyncStatus
    data class Error(val message: String, val canRetry: Boolean = true) : SyncStatus
}
