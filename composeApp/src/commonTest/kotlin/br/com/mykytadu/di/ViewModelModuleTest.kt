package br.com.mykytadu.di

import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.AniListAnimeId
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryResult
import br.com.mykytadu.features.search.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.dsl.koinApplication
import org.koin.dsl.module
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

    private class FakeAnimeRepository : AnimeRepository {
        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int,
        ): RepositoryResult<PagedResult<AnimeSummary>> =
            error("Não deve ser chamado neste teste.")

        override suspend fun getAnimeDetails(id: AniListAnimeId): RepositoryResult<AnimeDetails> =
            error("Não deve ser chamado neste teste.")
    }
}
