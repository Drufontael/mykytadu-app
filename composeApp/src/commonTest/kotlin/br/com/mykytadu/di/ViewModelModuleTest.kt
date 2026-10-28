package br.com.mykytadu.di

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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.core.parameter.parametersOf
import kotlin.test.Test
import kotlin.test.assertNotSame

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelModuleTest {

    @Test
    fun `deve resolver SearchViewModel como definicao nao singleton`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
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
        } finally {
            koinApplication.close()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `deve resolver AnimeDetailsViewModel por identidade como definicao nao singleton`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
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
        } finally {
            koinApplication.close()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `deve resolver AuthViewModel como definicao nao singleton`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
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
        } finally {
            koinApplication.close()
            Dispatchers.resetMain()
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
}
