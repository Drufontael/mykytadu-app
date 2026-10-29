package br.com.mykytadu.di

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
import br.com.mykytadu.domain.repository.ProfileRepository
import br.com.mykytadu.domain.repository.SyncRepository
import br.com.mykytadu.data.repository.DefaultCatalogCacheManager
import br.com.mykytadu.domain.repository.CatalogCacheManager
import org.koin.dsl.module

val RepositoryModule = module {
    single<AnimeRepository> {
        AniListAnimeRepository(
            animeApi = get(),
            catalogCacheDataSource = getOrNull(),
        )
    }

    single<LibraryRepository> {
        DefaultLibraryRepository(
            localDataSource = get(),
            syncQueueLocalDataSource = getOrNull(),
        )
    }

    single<PreferencesRepository> {
        DefaultPreferencesRepository(localDataSource = get())
    }

    single<AuthRepository> {
        DefaultAuthRepository(
            authApi = get(),
            secureStorageDriver = get(),
        )
    }

    single<SyncRepository> {
        DefaultSyncRepository(
            syncApi = get(),
            authRepository = get(),
            libraryLocalDataSource = get(),
            syncQueueLocalDataSource = get(),
        )
    }

    single<ProfileRepository> {
        DefaultProfileRepository(
            authRepository = get(),
            libraryRepository = get(),
        )
    }

    single<CatalogCacheManager> {
        DefaultCatalogCacheManager(
            catalogCacheLocalDataSource = get(),
        )
    }
}