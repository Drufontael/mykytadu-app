package br.com.mykytadu.domain.repository

import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.profile.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val authState: StateFlow<AuthState>
    fun observeUserStats(): Flow<UserStats>
}
