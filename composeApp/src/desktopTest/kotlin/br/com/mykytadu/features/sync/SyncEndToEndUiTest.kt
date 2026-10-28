package br.com.mykytadu.features.sync

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemorySecureStorageDriver
import br.com.mykytadu.data.local.datasource.InMemorySyncQueueLocalDataSource
import br.com.mykytadu.data.remote.api.AuthApi
import br.com.mykytadu.data.remote.api.SyncApi
import br.com.mykytadu.data.remote.dto.auth.LoginRequestDto
import br.com.mykytadu.data.remote.dto.auth.LoginResponseDto
import br.com.mykytadu.data.remote.dto.auth.LogoutRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshResponseDto
import br.com.mykytadu.data.remote.dto.auth.RegisterRequestDto
import br.com.mykytadu.data.remote.dto.auth.RegisterResponseDto
import br.com.mykytadu.data.remote.dto.auth.UserDto
import br.com.mykytadu.data.remote.dto.sync.LibraryEntrySyncDto
import br.com.mykytadu.data.remote.dto.sync.SyncBatchRequestDto
import br.com.mykytadu.data.remote.dto.sync.SyncBatchResponseDto
import br.com.mykytadu.data.repository.DefaultAuthRepository
import br.com.mykytadu.data.repository.DefaultLibraryRepository
import br.com.mykytadu.data.repository.DefaultProfileRepository
import br.com.mykytadu.data.repository.DefaultSyncRepository
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.result.SyncResult
import br.com.mykytadu.features.profile.ProfileScreenContent
import br.com.mykytadu.features.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class SyncEndToEndUiTest {

    @get:Rule
    val compose = createComposeRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `resolucao de conflito last-write-wins favorece versao mais recente entre local e remoto`() = runTest(testDispatcher) {
        val libraryDataSource = InMemoryLibraryLocalDataSource()
        val syncQueueDataSource = InMemorySyncQueueLocalDataSource()
        val libraryRepository = DefaultLibraryRepository(libraryDataSource, syncQueueDataSource)
        val secureStorage = InMemorySecureStorageDriver()
        val authApi = FakeSyncAuthApi()
        val authRepository = DefaultAuthRepository(authApi, secureStorage)

        // Autentica o usuário para permitir sync
        authRepository.login("user@mykytadu.com", "senha123")
        advanceUntilIdle()

        val fakeSyncApi = FakeSyncApi()
        val syncRepository = DefaultSyncRepository(
            syncApi = fakeSyncApi,
            authRepository = authRepository,
            libraryLocalDataSource = libraryDataSource,
            syncQueueLocalDataSource = syncQueueDataSource,
        )

        // 1. Cria registro local com timestamp T=1000 e progresso 2
        val localEntryV1 = LibraryEntry(
            id = LibraryEntryId("entry-1"),
            animeId = CatalogAnimeId("anime-100"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 2, total = 12),
            rating = LibraryRating(70),
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 1000L,
        )
        libraryRepository.saveEntry(localEntryV1)
        advanceUntilIdle()

        // 2. Servidor possui versão mais recente T=2000 com progresso 12
        fakeSyncApi.remoteEntriesToReturn = listOf(
            LibraryEntrySyncDto(
                animeId = "anime-100",
                status = "COMPLETED",
                progressCurrent = 12,
                progressTotal = 12,
                rating = 90,
                notes = "Finalizado no servidor",
                createdAtEpochMillis = 1000L,
                updatedAtEpochMillis = 2000L,
            )
        )

        val result1 = syncRepository.synchronize()
        advanceUntilIdle()
        assertIs<SyncResult.Success>(result1)

        // Remoto ganhou (2000 > 1000)
        val afterSync1 = libraryRepository.getEntryByAnimeId(CatalogAnimeId("anime-100"))
        val entry1 = (afterSync1 as? br.com.mykytadu.domain.result.RepositoryResult.Success<LibraryEntry?>)?.value
        assertNotNull(entry1)
        assertEquals(LibraryStatus.COMPLETED, entry1.status)
        assertEquals(12, entry1.progress.current)

        // 3. Usuário faz edição local mais recente T=3000 com progresso 15 (ex: rewatch)
        val localEntryV2 = entry1.copy(
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 15, total = 24),
            updatedAtEpochMillis = 3000L,
        )
        libraryRepository.saveEntry(localEntryV2)
        advanceUntilIdle()

        // 4. Servidor retorna versão mais antiga T=2500
        fakeSyncApi.remoteEntriesToReturn = listOf(
            LibraryEntrySyncDto(
                animeId = "anime-100",
                status = "PAUSED",
                progressCurrent = 8,
                progressTotal = 24,
                rating = 80,
                createdAtEpochMillis = 1000L,
                updatedAtEpochMillis = 2500L,
            )
        )

        val result2 = syncRepository.synchronize()
        advanceUntilIdle()
        assertIs<SyncResult.Success>(result2)

        // Local ganhou (3000 > 2500)
        val afterSync2 = libraryRepository.getEntryByAnimeId(CatalogAnimeId("anime-100"))
        val entry2 = (afterSync2 as? br.com.mykytadu.domain.result.RepositoryResult.Success<LibraryEntry?>)?.value
        assertNotNull(entry2)
        assertEquals(LibraryStatus.WATCHING, entry2.status)
        assertEquals(15, entry2.progress.current)
    }

    @Test
    fun `erro de rede durante sincronizacao mantem fila intacta e exibe erro`() = runTest(testDispatcher) {
        val libraryDataSource = InMemoryLibraryLocalDataSource()
        val syncQueueDataSource = InMemorySyncQueueLocalDataSource()
        val libraryRepository = DefaultLibraryRepository(libraryDataSource, syncQueueDataSource)
        val secureStorage = InMemorySecureStorageDriver()
        val authApi = FakeSyncAuthApi()
        val authRepository = DefaultAuthRepository(authApi, secureStorage)

        authRepository.login("user@mykytadu.com", "senha123")
        advanceUntilIdle()

        val fakeSyncApi = FakeSyncApi()
        fakeSyncApi.networkError = NetworkFailure.Connection(Exception("Falha de conexão com a API"))

        val syncRepository = DefaultSyncRepository(
            syncApi = fakeSyncApi,
            authRepository = authRepository,
            libraryLocalDataSource = libraryDataSource,
            syncQueueLocalDataSource = syncQueueDataSource,
        )

        // Enfileira alteração local
        libraryRepository.saveEntry(
            LibraryEntry(
                id = LibraryEntryId("entry-err"),
                animeId = CatalogAnimeId("anime-err"),
                status = LibraryStatus.PLANNING,
                createdAtEpochMillis = 1000L,
                updatedAtEpochMillis = 1000L,
            )
        )
        advanceUntilIdle()

        assertEquals(1, syncRepository.pendingMutationsCount.first())

        val result = syncRepository.synchronize()
        advanceUntilIdle()

        assertIs<SyncResult.Failure>(result)
        assertEquals("Sem conexão com a internet", result.message)

        // Fila deve permanecer intacta com 1 mutação pendente
        assertEquals(1, syncRepository.pendingMutationsCount.first())
    }

    @Test
    fun `fluxo completo de sincronizacao na interface do perfil`() {
        val libraryDataSource = InMemoryLibraryLocalDataSource()
        val syncQueueDataSource = InMemorySyncQueueLocalDataSource()
        val libraryRepository = DefaultLibraryRepository(libraryDataSource, syncQueueDataSource)
        val secureStorage = InMemorySecureStorageDriver()
        val authApi = FakeSyncAuthApi()
        val authRepository = DefaultAuthRepository(authApi, secureStorage)

        runBlocking {
            authRepository.login("eduardo@mykytadu.com", "senha123")
        }

        val fakeSyncApi = FakeSyncApi()
        val syncRepository = DefaultSyncRepository(
            syncApi = fakeSyncApi,
            authRepository = authRepository,
            libraryLocalDataSource = libraryDataSource,
            syncQueueLocalDataSource = syncQueueDataSource,
        )

        val profileRepository = DefaultProfileRepository(authRepository, libraryRepository)
        val viewModel = ProfileViewModel(profileRepository, syncRepository, authRepository)

        // Cria entrada local
        runBlocking {
            libraryRepository.saveEntry(
                LibraryEntry(
                    id = LibraryEntryId("entry-ui"),
                    animeId = CatalogAnimeId("anime-ui"),
                    status = LibraryStatus.WATCHING,
                    progress = LibraryProgress(current = 5, total = 12),
                    isFavorite = true,
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 1000L,
                )
            )
        }

        compose.setContent {
            val state = viewModel.uiState.collectAsState().value
            AppTheme {
                ProfileScreenContent(
                    uiState = state,
                    onNavigateToSettings = {},
                    onTriggerSync = { viewModel.triggerSync() },
                    onLogout = {},
                )
            }
        }

        // Verifica que o card de sync é exibido e indica pendências
        compose.onNodeWithTag("profile_sync_card").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("profile_sync_pending_text").assertIsDisplayed()
        compose.onNodeWithTag("profile_sync_button").performScrollTo().assertIsDisplayed().performClick()
        testDispatcher.scheduler.advanceUntilIdle()
        compose.waitForIdle()

        // Após clicar, a sincronização foi chamada e consumiu as mutações da fila
        assertEquals(1, fakeSyncApi.pushedBatches.size)
    }

    private class FakeSyncApi : SyncApi {
        var networkError: NetworkFailure? = null
        var remoteEntriesToReturn: List<LibraryEntrySyncDto> = emptyList()
        val pushedBatches = mutableListOf<SyncBatchRequestDto>()

        override suspend fun syncLibrary(
            accessToken: String,
            request: SyncBatchRequestDto,
        ): NetworkResult<SyncBatchResponseDto> {
            pushedBatches.add(request)
            networkError?.let { return NetworkResult.Failure(it) }

            return NetworkResult.Success(
                SyncBatchResponseDto(
                    syncedAtEpochMillis = 1700000000000L,
                    appliedCount = request.mutations.size,
                    serverChanges = remoteEntriesToReturn,
                )
            )
        }
    }

    private class FakeSyncAuthApi : AuthApi {
        override suspend fun login(request: LoginRequestDto): NetworkResult<LoginResponseDto> =
            NetworkResult.Success(
                LoginResponseDto(
                    accessToken = "mock-access-token",
                    refreshToken = "mock-refresh-token",
                    expiresIn = 600,
                    user = UserDto("usr-1", request.email, "Eduardo Estigarribia"),
                )
            )

        override suspend fun register(request: RegisterRequestDto): NetworkResult<RegisterResponseDto> =
            NetworkResult.Success(
                RegisterResponseDto(
                    id = "usr-1",
                    email = request.email,
                    name = request.name,
                )
            )

        override suspend fun refresh(request: RefreshRequestDto): NetworkResult<RefreshResponseDto> =
            NetworkResult.Success(
                RefreshResponseDto(
                    accessToken = "mock-new-access",
                    refreshToken = "mock-new-refresh",
                    expiresIn = 600,
                )
            )

        override suspend fun logout(request: LogoutRequestDto): NetworkResult<Unit> =
            NetworkResult.Success(Unit)

        override suspend fun getProfile(accessToken: String): NetworkResult<UserDto> =
            NetworkResult.Success(
                UserDto("usr-1", "eduardo@mykytadu.com", "Eduardo Estigarribia")
            )
    }
}
