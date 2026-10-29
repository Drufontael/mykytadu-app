package br.com.mykytadu.data.repository

import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemorySyncQueueLocalDataSource
import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import br.com.mykytadu.data.local.entity.SyncQueueEntryEntity
import br.com.mykytadu.data.remote.api.SyncApi
import br.com.mykytadu.data.remote.dto.sync.LibraryEntrySyncDto
import br.com.mykytadu.data.remote.dto.sync.SyncBatchRequestDto
import br.com.mykytadu.data.remote.dto.sync.SyncBatchResponseDto
import br.com.mykytadu.data.remote.dto.sync.SyncMutationType
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.auth.User
import br.com.mykytadu.domain.model.auth.UserId
import br.com.mykytadu.domain.model.sync.SyncStatus
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.result.AuthResult
import br.com.mykytadu.domain.result.SyncResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultSyncRepositoryTest {

    private class FakeAuthRepository(
        initialState: AuthState = AuthState.Unauthenticated,
        private var token: String? = null,
    ) : AuthRepository {
        private val _authState = MutableStateFlow(initialState)
        override val authState: StateFlow<AuthState> = _authState.asStateFlow()

        fun setAuthenticated(user: User, accessToken: String) {
            token = accessToken
            _authState.value = AuthState.Authenticated(user)
        }

        fun setUnauthenticated() {
            token = null
            _authState.value = AuthState.Unauthenticated
        }

        override suspend fun login(email: String, password: String): AuthResult<User> =
            error("Not needed in test")

        override suspend fun register(email: String, password: String, name: String): AuthResult<User> =
            error("Not needed in test")

        override suspend fun logout(): AuthResult<Unit> {
            setUnauthenticated()
            return AuthResult.Success(Unit)
        }

        override suspend fun restoreSession(): AuthResult<User?> =
            AuthResult.Success((_authState.value as? AuthState.Authenticated)?.user)

        override fun currentAccessToken(): String? = token
    }

    private class FakeSyncApi : SyncApi {
        var lastRequest: SyncBatchRequestDto? = null
        var responseToReturn: NetworkResult<SyncBatchResponseDto> = NetworkResult.Success(
            SyncBatchResponseDto(syncedAtEpochMillis = 5000L, appliedCount = 0)
        )

        override suspend fun syncLibrary(
            accessToken: String,
            request: SyncBatchRequestDto,
        ): NetworkResult<SyncBatchResponseDto> {
            lastRequest = request
            return responseToReturn
        }
    }

    @Test
    fun `synchronize retorna Unauthenticated quando usuario nao esta logado`() = runTest {
        val fakeApi = FakeSyncApi()
        val fakeAuth = FakeAuthRepository(AuthState.Unauthenticated)
        val libraryDs = InMemoryLibraryLocalDataSource()
        val queueDs = InMemorySyncQueueLocalDataSource()

        val repository = DefaultSyncRepository(
            syncApi = fakeApi,
            authRepository = fakeAuth,
            libraryLocalDataSource = libraryDs,
            syncQueueLocalDataSource = queueDs,
        )

        val result = repository.synchronize()

        assertIs<SyncResult.Unauthenticated>(result)
        assertNull(fakeApi.lastRequest)
        assertEquals(SyncStatus.Idle, repository.syncStatus.value)
    }

    @Test
    fun `synchronize envia mutacoes pendentes da fila e limpa fila apos sucesso`() = runTest {
        val fakeApi = FakeSyncApi()
        val fakeAuth = FakeAuthRepository()
        fakeAuth.setAuthenticated(User(UserId("u1"), "user@test.com", "Test"), "token-1")

        val libraryDs = InMemoryLibraryLocalDataSource()
        val queueDs = InMemorySyncQueueLocalDataSource()

        queueDs.enqueue(
            SyncQueueEntryEntity(
                id = "op-1",
                animeId = "10",
                type = SyncMutationType.UPSERT,
                entryEntity = LibraryEntryEntity(
                    id = "loc-1",
                    animeId = "10",
                    status = "WATCHING",
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 2000L,
                ),
                timestampEpochMillis = 2000L,
            )
        )

        fakeApi.responseToReturn = NetworkResult.Success(
            SyncBatchResponseDto(
                syncedAtEpochMillis = 3000L,
                appliedCount = 1,
                serverChanges = emptyList(),
                deletedAnimeIds = emptyList(),
            )
        )

        val repository = DefaultSyncRepository(
            syncApi = fakeApi,
            authRepository = fakeAuth,
            libraryLocalDataSource = libraryDs,
            syncQueueLocalDataSource = queueDs,
        )

        val result = repository.synchronize()

        val success = assertIs<SyncResult.Success>(result)
        assertEquals(1, success.appliedCount)
        assertEquals(0, success.serverChangesCount)

        // Fila de pendencias deve ter sido esvaziada
        assertTrue(queueDs.getPendingEntries().isEmpty())
        assertEquals(3000L, repository.lastSyncedEpochMillis.value)
        val status = assertIs<SyncStatus.Success>(repository.syncStatus.value)
        assertEquals(3000L, status.syncedAtEpochMillis)
    }

    @Test
    fun `synchronize aplica Last-Write-Wins quando servidor for mais recente`() = runTest {
        val fakeApi = FakeSyncApi()
        val fakeAuth = FakeAuthRepository()
        fakeAuth.setAuthenticated(User(UserId("u1"), "user@test.com", "Test"), "token-1")

        val libraryDs = InMemoryLibraryLocalDataSource(
            initialEntries = listOf(
                LibraryEntryEntity(
                    id = "loc-10",
                    animeId = "10",
                    status = "WATCHING",
                    progressCurrent = 2,
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 1500L,
                )
            )
        )
        val queueDs = InMemorySyncQueueLocalDataSource()

        fakeApi.responseToReturn = NetworkResult.Success(
            SyncBatchResponseDto(
                syncedAtEpochMillis = 4000L,
                appliedCount = 0,
                serverChanges = listOf(
                    LibraryEntrySyncDto(
                        animeId = "10",
                        status = "COMPLETED",
                        progressCurrent = 12,
                        createdAtEpochMillis = 1000L,
                        updatedAtEpochMillis = 3000L, // Servidor mais recente que 1500L
                    )
                ),
                deletedAnimeIds = emptyList(),
            )
        )

        val repository = DefaultSyncRepository(
            syncApi = fakeApi,
            authRepository = fakeAuth,
            libraryLocalDataSource = libraryDs,
            syncQueueLocalDataSource = queueDs,
        )

        repository.synchronize()

        val updatedLocal = libraryDs.getEntryByAnimeId("10")
        assertNotNull(updatedLocal)
        assertEquals("COMPLETED", updatedLocal.status)
        assertEquals(12, updatedLocal.progressCurrent)
        assertEquals("loc-10", updatedLocal.id) // Preservou o id local
    }

    @Test
    fun `synchronize aplica Last-Write-Wins quando local for mais recente preservando versao local`() = runTest {
        val fakeApi = FakeSyncApi()
        val fakeAuth = FakeAuthRepository()
        fakeAuth.setAuthenticated(User(UserId("u1"), "user@test.com", "Test"), "token-1")

        val libraryDs = InMemoryLibraryLocalDataSource(
            initialEntries = listOf(
                LibraryEntryEntity(
                    id = "loc-10",
                    animeId = "10",
                    status = "WATCHING",
                    progressCurrent = 8,
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 5000L, // Local mais recente que 3000L
                )
            )
        )
        val queueDs = InMemorySyncQueueLocalDataSource()

        fakeApi.responseToReturn = NetworkResult.Success(
            SyncBatchResponseDto(
                syncedAtEpochMillis = 5000L,
                appliedCount = 0,
                serverChanges = listOf(
                    LibraryEntrySyncDto(
                        animeId = "10",
                        status = "PLANNING",
                        progressCurrent = 0,
                        createdAtEpochMillis = 1000L,
                        updatedAtEpochMillis = 3000L, // Servidor mais antigo
                    )
                ),
                deletedAnimeIds = emptyList(),
            )
        )

        val repository = DefaultSyncRepository(
            syncApi = fakeApi,
            authRepository = fakeAuth,
            libraryLocalDataSource = libraryDs,
            syncQueueLocalDataSource = queueDs,
        )

        repository.synchronize()

        val localEntry = libraryDs.getEntryByAnimeId("10")
        assertNotNull(localEntry)
        assertEquals("WATCHING", localEntry.status)
        assertEquals(8, localEntry.progressCurrent)
    }

    @Test
    fun `synchronize aplica exclusao de anime informada pelo servidor se local nao foi alterado posteriormente`() = runTest {
        val fakeApi = FakeSyncApi()
        val fakeAuth = FakeAuthRepository()
        fakeAuth.setAuthenticated(User(UserId("u1"), "user@test.com", "Test"), "token-1")

        val libraryDs = InMemoryLibraryLocalDataSource(
            initialEntries = listOf(
                LibraryEntryEntity(
                    id = "loc-20",
                    animeId = "20",
                    status = "DROPPED",
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 2000L,
                )
            )
        )
        val queueDs = InMemorySyncQueueLocalDataSource()

        fakeApi.responseToReturn = NetworkResult.Success(
            SyncBatchResponseDto(
                syncedAtEpochMillis = 3000L,
                appliedCount = 0,
                serverChanges = emptyList(),
                deletedAnimeIds = listOf("20"),
            )
        )

        val repository = DefaultSyncRepository(
            syncApi = fakeApi,
            authRepository = fakeAuth,
            libraryLocalDataSource = libraryDs,
            syncQueueLocalDataSource = queueDs,
        )

        repository.synchronize()

        assertNull(libraryDs.getEntryByAnimeId("20"))
    }

    @Test
    fun `synchronize com falha de rede nao perde fila pendente e registra estado de erro com retry`() = runTest {
        val fakeApi = FakeSyncApi()
        fakeApi.responseToReturn = NetworkResult.Failure(NetworkFailure.Connection(Exception("Sem conexao")))
        val fakeAuth = FakeAuthRepository()
        fakeAuth.setAuthenticated(User(UserId("u1"), "user@test.com", "Test"), "token-1")

        val libraryDs = InMemoryLibraryLocalDataSource()
        val queueDs = InMemorySyncQueueLocalDataSource()

        queueDs.enqueue(
            SyncQueueEntryEntity(
                id = "op-99",
                animeId = "99",
                type = SyncMutationType.UPSERT,
                entryEntity = null,
                timestampEpochMillis = 1000L,
            )
        )

        val repository = DefaultSyncRepository(
            syncApi = fakeApi,
            authRepository = fakeAuth,
            libraryLocalDataSource = libraryDs,
            syncQueueLocalDataSource = queueDs,
        )

        val result = repository.synchronize()

        val failure = assertIs<SyncResult.Failure>(result)
        assertTrue(failure.canRetry)
        assertEquals("Sem conexão com a internet", failure.message)

        // Fila deve permanecer intacta
        assertEquals(1, queueDs.getPendingEntries().size)
        val status = assertIs<SyncStatus.Error>(repository.syncStatus.value)
        assertTrue(status.canRetry)
    }

    @Test
    fun `DefaultLibraryRepository com syncQueue grava operacao ao salvar e ao deletar`() = runTest {
        val libraryDs = InMemoryLibraryLocalDataSource()
        val queueDs = InMemorySyncQueueLocalDataSource()

        val libraryRepo = DefaultLibraryRepository(
            localDataSource = libraryDs,
            syncQueueLocalDataSource = queueDs,
        )

        val entry = LibraryEntry(
            id = LibraryEntryId("local-1"),
            animeId = CatalogAnimeId("555"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 3, total = 12),
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 2000L,
        )

        libraryRepo.saveEntry(entry)

        val pendingAfterSave = queueDs.getPendingEntries()
        assertEquals(1, pendingAfterSave.size)
        assertEquals("555", pendingAfterSave.first().animeId)
        assertEquals(SyncMutationType.UPSERT, pendingAfterSave.first().type)

        libraryRepo.deleteEntryByAnimeId(CatalogAnimeId("555"))

        val pendingAfterDelete = queueDs.getPendingEntries()
        assertEquals(1, pendingAfterDelete.size)
        assertEquals("555", pendingAfterDelete.first().animeId)
        assertEquals(SyncMutationType.DELETE, pendingAfterDelete.first().type)
    }
}
