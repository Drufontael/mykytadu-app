package br.com.mykytadu.features.profile

import br.com.mykytadu.domain.model.auth.User
import br.com.mykytadu.domain.model.profile.UserStats
import br.com.mykytadu.domain.model.sync.SyncStatus

data class ProfileUiState(
    val user: User? = null,
    val stats: UserStats = UserStats(),
    val syncStatus: SyncStatus = SyncStatus.Idle,
    val lastSyncedEpochMillis: Long? = null,
    val pendingMutationsCount: Int = 0,
    val isSyncing: Boolean = false,
    val syncErrorMessage: String? = null,
) {
    val userInitials: String
        get() {
            val name = user?.name?.trim() ?: ""
            if (name.isEmpty()) return "?"
            val parts = name.split(" ").filter { it.isNotBlank() }
            return when {
                parts.size >= 2 -> "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
                else -> parts[0].take(2).uppercase()
            }
        }
}
