package br.com.mykytadu.data.repository

import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.auth.User
import br.com.mykytadu.domain.model.auth.UserId
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.result.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DefaultProfileRepositoryTest {

    private class FakeAuthRepository(
        initialState: AuthState = AuthState.Unauthenticated,
    ) : AuthRepository {
        private val _authState = MutableStateFlow(initialState)
        override val authState: StateFlow<AuthState> = _authState.asStateFlow()

        fun setAuthenticated(user: User) {
            _authState.value = AuthState.Authenticated(user)
        }

        override suspend fun login(email: String, password: String): AuthResult<User> = error("Not needed")
        override suspend fun register(email: String, password: String, name: String): AuthResult<User> = error("Not needed")
        override suspend fun logout(): AuthResult<Unit> = AuthResult.Success(Unit)
        override suspend fun restoreSession(): AuthResult<User?> = AuthResult.Success(null)
        override fun currentAccessToken(): String? = null
    }

    @Test
    fun `observeUserStats reflete contagens da biblioteca em tempo real`() = runTest {
        val libraryDs = InMemoryLibraryLocalDataSource()
        val libraryRepo = DefaultLibraryRepository(libraryDs)
        val authRepo = FakeAuthRepository()
        val profileRepo = DefaultProfileRepository(authRepo, libraryRepo)

        val initialStats = profileRepo.observeUserStats().first()
        assertEquals(0, initialStats.totalAnimeCount)

        libraryDs.upsertEntry(
            LibraryEntryEntity(
                id = "1",
                animeId = "101",
                status = "WATCHING",
                progressCurrent = 5,
                isFavorite = true,
                createdAtEpochMillis = 1000L,
            )
        )

        val updatedStats = profileRepo.observeUserStats().first()
        assertEquals(1, updatedStats.totalAnimeCount)
        assertEquals(1, updatedStats.watchingCount)
        assertEquals(1, updatedStats.favoriteCount)
        assertEquals(5, updatedStats.totalEpisodesWatched)
    }

    @Test
    fun `authState reflete estado do AuthRepository`() = runTest {
        val libraryDs = InMemoryLibraryLocalDataSource()
        val libraryRepo = DefaultLibraryRepository(libraryDs)
        val authRepo = FakeAuthRepository()
        val profileRepo = DefaultProfileRepository(authRepo, libraryRepo)

        assertIs<AuthState.Unauthenticated>(profileRepo.authState.value)

        authRepo.setAuthenticated(User(UserId("u-1"), "edu@test.com", "Eduardo"))

        val currentAuth = profileRepo.authState.value
        val authenticated = assertIs<AuthState.Authenticated>(currentAuth)
        assertEquals("Eduardo", authenticated.user.name)
    }
}
