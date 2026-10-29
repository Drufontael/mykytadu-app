package br.com.mykytadu.di

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemoryPreferencesLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemorySecureStorageDriver
import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.PreferencesLocalDataSource
import br.com.mykytadu.data.local.datasource.SecureStorageDriver
import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
import br.com.mykytadu.data.remote.api.AnimeApi
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
import br.com.mykytadu.data.repository.AniListAnimeRepository
import br.com.mykytadu.data.repository.DefaultAuthRepository
import br.com.mykytadu.data.repository.DefaultLibraryRepository
import br.com.mykytadu.data.repository.DefaultPreferencesRepository
import br.com.mykytadu.data.repository.DefaultProfileRepository
import br.com.mykytadu.data.repository.DefaultSyncRepository
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.repository.LibraryRepository
import br.com.mykytadu.domain.repository.PreferencesRepository
import br.com.mykytadu.data.local.datasource.CatalogCacheLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemoryCatalogCacheLocalDataSource
import br.com.mykytadu.data.repository.DefaultCatalogCacheManager
import br.com.mykytadu.domain.repository.CatalogCacheManager
import br.com.mykytadu.domain.repository.ProfileRepository
import br.com.mykytadu.domain.repository.SyncRepository
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

    @Test
    fun `deve resolver AuthRepository com implementacao DefaultAuthRepository`() {
        val storageDriver = InMemorySecureStorageDriver()
        val authApi = FakeAuthApi()
        val testDepsModule = module {
            single<SecureStorageDriver> { storageDriver }
            single<AuthApi> { authApi }
        }
        val koinApplication = koinApplication {
            modules(testDepsModule, RepositoryModule)
        }

        try {
            val repository = koinApplication.koin.get<AuthRepository>()

            assertIs<DefaultAuthRepository>(repository)
            assertSame(storageDriver, koinApplication.koin.get<SecureStorageDriver>())
            assertSame(authApi, koinApplication.koin.get<AuthApi>())
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de AuthRepository`() {
        val testDepsModule = module {
            single<SecureStorageDriver> { InMemorySecureStorageDriver() }
            single<AuthApi> { FakeAuthApi() }
        }
        val koinApplication = koinApplication {
            modules(testDepsModule, RepositoryModule)
        }

        try {
            val repository1 = koinApplication.koin.get<AuthRepository>()
            val repository2 = koinApplication.koin.get<AuthRepository>()

            assertSame(repository1, repository2)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver SyncRepository com implementacao DefaultSyncRepository`() {
        val testDepsModule = module {
            single<SyncApi> { FakeSyncApi() }
            single<AuthApi> { FakeAuthApi() }
            single<SecureStorageDriver> { InMemorySecureStorageDriver() }
            single<LibraryLocalDataSource> { InMemoryLibraryLocalDataSource() }
            single<br.com.mykytadu.data.local.datasource.SyncQueueLocalDataSource> { br.com.mykytadu.data.local.datasource.InMemorySyncQueueLocalDataSource() }
        }
        val koinApplication = koinApplication {
            modules(testDepsModule, RepositoryModule)
        }

        try {
            val repository = koinApplication.koin.get<SyncRepository>()

            assertIs<DefaultSyncRepository>(repository)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de SyncRepository`() {
        val testDepsModule = module {
            single<SyncApi> { FakeSyncApi() }
            single<AuthApi> { FakeAuthApi() }
            single<SecureStorageDriver> { InMemorySecureStorageDriver() }
            single<LibraryLocalDataSource> { InMemoryLibraryLocalDataSource() }
            single<br.com.mykytadu.data.local.datasource.SyncQueueLocalDataSource> { br.com.mykytadu.data.local.datasource.InMemorySyncQueueLocalDataSource() }
        }
        val koinApplication = koinApplication {
            modules(testDepsModule, RepositoryModule)
        }

        try {
            val repository1 = koinApplication.koin.get<SyncRepository>()
            val repository2 = koinApplication.koin.get<SyncRepository>()

            assertSame(repository1, repository2)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver ProfileRepository com implementacao DefaultProfileRepository`() {
        val testDepsModule = module {
            single<AuthApi> { FakeAuthApi() }
            single<SecureStorageDriver> { InMemorySecureStorageDriver() }
            single<LibraryLocalDataSource> { InMemoryLibraryLocalDataSource() }
        }
        val koinApplication = koinApplication {
            modules(testDepsModule, RepositoryModule)
        }

        try {
            val repository = koinApplication.koin.get<ProfileRepository>()

            assertIs<DefaultProfileRepository>(repository)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de ProfileRepository`() {
        val testDepsModule = module {
            single<AuthApi> { FakeAuthApi() }
            single<SecureStorageDriver> { InMemorySecureStorageDriver() }
            single<LibraryLocalDataSource> { InMemoryLibraryLocalDataSource() }
        }
        val koinApplication = koinApplication {
            modules(testDepsModule, RepositoryModule)
        }

        try {
            val repository1 = koinApplication.koin.get<ProfileRepository>()
            val repository2 = koinApplication.koin.get<ProfileRepository>()

            assertSame(repository1, repository2)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver CatalogCacheManager com implementacao DefaultCatalogCacheManager`() {
        val testDepsModule = module {
            single<CatalogCacheLocalDataSource> { InMemoryCatalogCacheLocalDataSource() }
        }
        val koinApplication = koinApplication {
            modules(testDepsModule, RepositoryModule)
        }

        try {
            val manager = koinApplication.koin.get<CatalogCacheManager>()

            assertIs<DefaultCatalogCacheManager>(manager)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de CatalogCacheManager`() {
        val testDepsModule = module {
            single<CatalogCacheLocalDataSource> { InMemoryCatalogCacheLocalDataSource() }
        }
        val koinApplication = koinApplication {
            modules(testDepsModule, RepositoryModule)
        }

        try {
            val manager1 = koinApplication.koin.get<CatalogCacheManager>()
            val manager2 = koinApplication.koin.get<CatalogCacheManager>()

            assertSame(manager1, manager2)
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

    private class FakeAuthApi : AuthApi {
        override suspend fun login(request: LoginRequestDto): NetworkResult<LoginResponseDto> =
            error("Não deve ser chamado neste teste.")

        override suspend fun register(request: RegisterRequestDto): NetworkResult<RegisterResponseDto> =
            error("Não deve ser chamado neste teste.")

        override suspend fun refresh(request: RefreshRequestDto): NetworkResult<RefreshResponseDto> =
            error("Não deve ser chamado neste teste.")

        override suspend fun logout(request: LogoutRequestDto): NetworkResult<Unit> =
            error("Não deve ser chamado neste teste.")

        override suspend fun getProfile(accessToken: String): NetworkResult<UserDto> =
            error("Não deve ser chamado neste teste.")
    }

    private class FakeSyncApi : SyncApi {
        override suspend fun syncLibrary(
            accessToken: String,
            request: br.com.mykytadu.data.remote.dto.sync.SyncBatchRequestDto,
        ): NetworkResult<br.com.mykytadu.data.remote.dto.sync.SyncBatchResponseDto> =
            error("Não deve ser chamado neste teste.")
    }
}