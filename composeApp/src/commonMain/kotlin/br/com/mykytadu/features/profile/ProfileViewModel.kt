package br.com.mykytadu.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.sync.SyncStatus
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.repository.ProfileRepository
import br.com.mykytadu.domain.repository.SyncRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val syncRepository: SyncRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeProfile()
    }

    private fun observeProfile() {
        combine(
            profileRepository.authState,
            profileRepository.observeUserStats(),
            syncRepository.syncStatus,
            syncRepository.lastSyncedEpochMillis,
            syncRepository.pendingMutationsCount,
        ) { authState, stats, syncStatus, lastSynced, pendingCount ->
            val user = (authState as? AuthState.Authenticated)?.user
            val isSyncing = syncStatus is SyncStatus.Syncing
            val errorMessage = (syncStatus as? SyncStatus.Error)?.message

            ProfileUiState(
                user = user,
                stats = stats,
                syncStatus = syncStatus,
                lastSyncedEpochMillis = lastSynced,
                pendingMutationsCount = pendingCount,
                isSyncing = isSyncing,
                syncErrorMessage = errorMessage,
            )
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }

    fun triggerSync() {
        if (_uiState.value.isSyncing) return
        viewModelScope.launch {
            syncRepository.synchronize()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
