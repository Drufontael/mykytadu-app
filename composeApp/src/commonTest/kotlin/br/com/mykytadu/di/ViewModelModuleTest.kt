package br.com.mykytadu.di

import androidx.lifecycle.viewModelScope
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryResult
import br.com.mykytadu.features.search.SearchViewModel
import br.com.mykytadu.features.anime.AnimeDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.core.parameter.parametersOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotSame

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelModuleTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `deve resolver SearchViewModel como definicao nao singleton`() = runTest(testDispatcher) {
        val koinApplication = koinApplication {
            modules(
                module {
                    single<AnimeRepository> { FakeAnimeRepository() }
                },
                ViewModelModule,
            )
        }

        try {
            val first = koinApplication.koin.get<SearchViewModel>()
            val second = koinApplication.koin.get<SearchViewModel>()

            assertNotSame(first, second)
            first.viewModelScope.cancel()
            second.viewModelScope.cancel()
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver AnimeDetailsViewModel por identidade como definicao nao singleton`() = runTest(testDispatcher) {
        val koinApplication = koinApplication {
            modules(
                module {
                    single<AnimeRepository> { FakeAnimeRepository() }
                },
                ViewModelModule,
            )
        }

        try {
            val parameters = { parametersOf(CatalogAnimeId("20")) }
            val first = koinApplication.koin.get<AnimeDetailsViewModel>(parameters = parameters)
            val second = koinApplication.koin.get<AnimeDetailsViewModel>(parameters = parameters)

            assertNotSame(first, second)
            first.viewModelScope.cancel()
            second.viewModelScope.cancel()
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver AuthViewModel como definicao nao singleton`() = runTest(testDispatcher) {
        val koinApplication = koinApplication {
            modules(
                module {
                    single<br.com.mykytadu.domain.repository.AuthRepository> { FakeAuthRepository() }
                },
                ViewModelModule,
            )
        }

        try {
            val first = koinApplication.koin.get<br.com.mykytadu.features.auth.AuthViewModel>()
            val second = koinApplication.koin.get<br.com.mykytadu.features.auth.AuthViewModel>()

            assertNotSame(first, second)
            first.viewModelScope.cancel()
            second.viewModelScope.cancel()
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver ProfileViewModel como definicao nao singleton`() = runTest(testDispatcher) {
        val koinApplication = koinApplication {
            modules(
                module {
                    single<br.com.mykytadu.domain.repository.ProfileRepository> { FakeProfileRepository() }
                    single<br.com.mykytadu.domain.repository.SyncRepository> { FakeSyncRepository() }
                    single<br.com.mykytadu.domain.repository.AuthRepository> { FakeAuthRepository() }
                },
                ViewModelModule,
            )
        }

        try {
            val first = koinApplication.koin.get<br.com.mykytadu.features.profile.ProfileViewModel>()
            val second = koinApplication.koin.get<br.com.mykytadu.features.profile.ProfileViewModel>()

            assertNotSame(first, second)
            first.viewModelScope.cancel()
            second.viewModelScope.cancel()
        } finally {
            koinApplication.close()
        }
    }

    private class FakeAnimeRepository : AnimeRepository {
        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int,
        ): RepositoryResult<PagedResult<AnimeSummary>> =
            error("Não deve ser chamado neste teste.")

        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> =
            RepositoryResult.Success(AnimeDetails(id = id))
    }

    private class FakeAuthRepository : br.com.mykytadu.domain.repository.AuthRepository {
        private val _authState = kotlinx.coroutines.flow.MutableStateFlow<br.com.mykytadu.domain.model.auth.AuthState>(br.com.mykytadu.domain.model.auth.AuthState.Unauthenticated)
        override val authState: kotlinx.coroutines.flow.StateFlow<br.com.mykytadu.domain.model.auth.AuthState> = _authState

        override suspend fun login(email: String, password: String): br.com.mykytadu.domain.result.AuthResult<br.com.mykytadu.domain.model.auth.User> =
            error("Não deve ser chamado neste teste.")

        override suspend fun register(email: String, password: String, name: String): br.com.mykytadu.domain.result.AuthResult<br.com.mykytadu.domain.model.auth.User> =
            error("Não deve ser chamado neste teste.")

        override suspend fun logout(): br.com.mykytadu.domain.result.AuthResult<Unit> =
            br.com.mykytadu.domain.result.AuthResult.Success(Unit)

        override suspend fun restoreSession(): br.com.mykytadu.domain.result.AuthResult<br.com.mykytadu.domain.model.auth.User?> =
            br.com.mykytadu.domain.result.AuthResult.Success(null)

        override fun currentAccessToken(): String? = null
    }

    private class FakeProfileRepository : br.com.mykytadu.domain.repository.ProfileRepository {
        override val authState: kotlinx.coroutines.flow.StateFlow<br.com.mykytadu.domain.model.auth.AuthState> =
            kotlinx.coroutines.flow.MutableStateFlow(br.com.mykytadu.domain.model.auth.AuthState.Unauthenticated)

        override fun observeUserStats(): kotlinx.coroutines.flow.Flow<br.com.mykytadu.domain.model.profile.UserStats> =
            kotlinx.coroutines.flow.MutableStateFlow(br.com.mykytadu.domain.model.profile.UserStats())
    }

    private class FakeSyncRepository : br.com.mykytadu.domain.repository.SyncRepository {
        override val syncStatus: kotlinx.coroutines.flow.StateFlow<br.com.mykytadu.domain.model.sync.SyncStatus> =
            kotlinx.coroutines.flow.MutableStateFlow(br.com.mykytadu.domain.model.sync.SyncStatus.Idle)

        override val lastSyncedEpochMillis: kotlinx.coroutines.flow.StateFlow<Long?> =
            kotlinx.coroutines.flow.MutableStateFlow(null)

        override val pendingMutationsCount: kotlinx.coroutines.flow.Flow<Int> =
            kotlinx.coroutines.flow.MutableStateFlow(0)

        override suspend fun synchronize(): br.com.mykytadu.domain.result.SyncResult =
            br.com.mykytadu.domain.result.SyncResult.Success(0, 0)

        override suspend fun clearPendingQueue() {}
    }
}
