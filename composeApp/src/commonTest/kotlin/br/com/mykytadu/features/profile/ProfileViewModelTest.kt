package br.com.mykytadu.features.profile

import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.auth.User
import br.com.mykytadu.domain.model.auth.UserId
import br.com.mykytadu.domain.model.profile.UserStats
import br.com.mykytadu.domain.model.sync.SyncStatus
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.repository.ProfileRepository
import br.com.mykytadu.domain.repository.SyncRepository
import br.com.mykytadu.domain.result.AuthResult
import br.com.mykytadu.domain.result.SyncResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeProfileRepository(
        initialAuth: AuthState = AuthState.Unauthenticated,
        initialStats: UserStats = UserStats(),
    ) : ProfileRepository {
        val authFlow = MutableStateFlow(initialAuth)
        val statsFlow = MutableStateFlow(initialStats)

        override val authState: StateFlow<AuthState> = authFlow.asStateFlow()
        override fun observeUserStats(): Flow<UserStats> = statsFlow
    }

    private class FakeSyncRepository : SyncRepository {
        val statusFlow = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
        val lastSyncedFlow = MutableStateFlow<Long?>(null)
        val pendingCountFlow = MutableStateFlow(0)

        var synchronizeCalled = false

        override val syncStatus: StateFlow<SyncStatus> = statusFlow.asStateFlow()
        override val lastSyncedEpochMillis: StateFlow<Long?> = lastSyncedFlow.asStateFlow()
        override val pendingMutationsCount: Flow<Int> = pendingCountFlow

        override suspend fun synchronize(): SyncResult {
            synchronizeCalled = true
            return SyncResult.Success(1, 0)
        }

        override suspend fun clearPendingQueue() {}
    }

    private class FakeAuthRepository : AuthRepository {
        var logoutCalled = false

        override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)
        override suspend fun login(email: String, password: String): AuthResult<User> = error("Not needed")
        override suspend fun register(email: String, password: String, name: String): AuthResult<User> = error("Not needed")
        override suspend fun logout(): AuthResult<Unit> {
            logoutCalled = true
            return AuthResult.Success(Unit)
        }
        override suspend fun restoreSession(): AuthResult<User?> = AuthResult.Success(null)
        override fun currentAccessToken(): String? = null
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `inicializacao carrega dados do usuario, estatisticas e estado de sincronizacao`() = runTest {
        val user = User(UserId("usr-1"), "edu@test.com", "Eduardo Estigarribia")
        val stats = UserStats(totalAnimeCount = 10, watchingCount = 3, favoriteCount = 2)

        val profileRepo = FakeProfileRepository(
            initialAuth = AuthState.Authenticated(user),
            initialStats = stats,
        )
        val syncRepo = FakeSyncRepository()
        val authRepo = FakeAuthRepository()

        val viewModel = ProfileViewModel(profileRepo, syncRepo, authRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(user, state.user)
        assertEquals("EE", state.userInitials)
        assertEquals(10, state.stats.totalAnimeCount)
        assertEquals(3, state.stats.watchingCount)
        assertEquals(2, state.stats.favoriteCount)
        assertFalse(state.isSyncing)
        assertNull(state.syncErrorMessage)
    }

    @Test
    fun `triggerSync chama synchronize no SyncRepository`() = runTest {
        val profileRepo = FakeProfileRepository()
        val syncRepo = FakeSyncRepository()
        val authRepo = FakeAuthRepository()

        val viewModel = ProfileViewModel(profileRepo, syncRepo, authRepo)
        advanceUntilIdle()

        viewModel.triggerSync()
        advanceUntilIdle()

        assertTrue(syncRepo.synchronizeCalled)
    }

    @Test
    fun `logout chama logout no AuthRepository`() = runTest {
        val profileRepo = FakeProfileRepository()
        val syncRepo = FakeSyncRepository()
        val authRepo = FakeAuthRepository()

        val viewModel = ProfileViewModel(profileRepo, syncRepo, authRepo)
        advanceUntilIdle()

        viewModel.logout()
        advanceUntilIdle()

        assertTrue(authRepo.logoutCalled)
    }

    @Test
    fun `atualizacao de syncStatus reflete isSyncing e erro no uiState`() = runTest {
        val profileRepo = FakeProfileRepository()
        val syncRepo = FakeSyncRepository()
        val authRepo = FakeAuthRepository()

        val viewModel = ProfileViewModel(profileRepo, syncRepo, authRepo)
        advanceUntilIdle()

        syncRepo.statusFlow.value = SyncStatus.Syncing
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSyncing)

        syncRepo.statusFlow.value = SyncStatus.Error("Falha de rede")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSyncing)
        assertEquals("Falha de rede", viewModel.uiState.value.syncErrorMessage)
    }
}
