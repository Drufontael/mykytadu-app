package br.com.mykytadu.domain.result

sealed interface SyncResult {
    data class Success(val appliedCount: Int, val serverChangesCount: Int) : SyncResult
    data object Unauthenticated : SyncResult
    data class Failure(val message: String, val canRetry: Boolean = true) : SyncResult
}
