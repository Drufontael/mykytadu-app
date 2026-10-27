package br.com.mykytadu.di

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemoryPreferencesLocalDataSource
import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.PreferencesLocalDataSource
import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
import br.com.mykytadu.data.remote.api.AnimeApi
import br.com.mykytadu.data.repository.AniListAnimeRepository
import br.com.mykytadu.data.repository.DefaultLibraryRepository
import br.com.mykytadu.data.repository.DefaultPreferencesRepository
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.repository.LibraryRepository
import br.com.mykytadu.domain.repository.PreferencesRepository
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertSame

class RepositoryModuleTest {

    @Test
    fun `deve resolver AnimeRepository com implementacao AniList`() {
        val animeApi = FakeAnimeApi()
        val testApiModule = module {
            single<AnimeApi> { animeApi }
        }
        val koinApplication = koinApplication {
            modules(testApiModule, RepositoryModule)
        }

        try {
            val repository = koinApplication.koin.get<AnimeRepository>()

            assertIs<AniListAnimeRepository>(repository)
            assertSame(animeApi, koinApplication.koin.get<AnimeApi>())
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de AnimeRepository`() {
        val testApiModule = module {
            single<AnimeApi> { FakeAnimeApi() }
        }
        val koinApplication = koinApplication {
            modules(testApiModule, RepositoryModule)
        }

        try {
            val repository1 = koinApplication.koin.get<AnimeRepository>()
            val repository2 = koinApplication.koin.get<AnimeRepository>()

            assertSame(repository1, repository2)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver LibraryRepository com implementacao DefaultLibraryRepository`() {
        val localDataSource = InMemoryLibraryLocalDataSource()
        val testStorageModule = module {
            single<LibraryLocalDataSource> { localDataSource }
        }
        val koinApplication = koinApplication {
            modules(testStorageModule, RepositoryModule)
        }

        try {
            val repository = koinApplication.koin.get<LibraryRepository>()

            assertIs<DefaultLibraryRepository>(repository)
            assertSame(localDataSource, koinApplication.koin.get<LibraryLocalDataSource>())
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de LibraryRepository`() {
        val testStorageModule = module {
            single<LibraryLocalDataSource> { InMemoryLibraryLocalDataSource() }
        }
        val koinApplication = koinApplication {
            modules(testStorageModule, RepositoryModule)
        }

        try {
            val repository1 = koinApplication.koin.get<LibraryRepository>()
            val repository2 = koinApplication.koin.get<LibraryRepository>()

            assertSame(repository1, repository2)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver PreferencesRepository com implementacao DefaultPreferencesRepository`() {
        val preferencesLocalDataSource = InMemoryPreferencesLocalDataSource()
        val testStorageModule = module {
            single<PreferencesLocalDataSource> { preferencesLocalDataSource }
        }
        val koinApplication = koinApplication {
            modules(testStorageModule, RepositoryModule)
        }

        try {
            val repository = koinApplication.koin.get<PreferencesRepository>()

            assertIs<DefaultPreferencesRepository>(repository)
            assertSame(preferencesLocalDataSource, koinApplication.koin.get<PreferencesLocalDataSource>())
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de PreferencesRepository`() {
        val testStorageModule = module {
            single<PreferencesLocalDataSource> { InMemoryPreferencesLocalDataSource() }
        }
        val koinApplication = koinApplication {
            modules(testStorageModule, RepositoryModule)
        }

        try {
            val repository1 = koinApplication.koin.get<PreferencesRepository>()
            val repository2 = koinApplication.koin.get<PreferencesRepository>()

            assertSame(repository1, repository2)
        } finally {
            koinApplication.close()
        }
    }

    private class FakeAnimeApi : AnimeApi {

        override suspend fun searchAnime(
            search: String,
            page: Int,
            perPage: Int,
        ): NetworkResult<AnimeSearchPageDto> =
            error("Não deve ser chamado neste teste.")

        override suspend fun getAnimeDetails(
            id: Int,
        ): NetworkResult<AnimeDetailsDto> =
            error("Não deve ser chamado neste teste.")
    }
}