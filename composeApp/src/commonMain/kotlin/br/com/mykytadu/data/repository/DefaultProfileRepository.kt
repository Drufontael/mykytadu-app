package br.com.mykytadu.data.repository

import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.profile.UserStats
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.repository.LibraryRepository
import br.com.mykytadu.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class DefaultProfileRepository(
    private val authRepository: AuthRepository,
    private val libraryRepository: LibraryRepository,
) : ProfileRepository {

    override val authState: StateFlow<AuthState> = authRepository.authState

    override fun observeUserStats(): Flow<UserStats> =
        libraryRepository.observeAllEntries().map { entries ->
            UserStats.fromEntries(entries)
        }
}
