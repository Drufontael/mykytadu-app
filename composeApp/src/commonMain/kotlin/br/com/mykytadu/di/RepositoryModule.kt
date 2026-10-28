package br.com.mykytadu.di

import br.com.mykytadu.data.repository.AniListAnimeRepository
import br.com.mykytadu.data.repository.DefaultAuthRepository
import br.com.mykytadu.data.repository.DefaultLibraryRepository
import br.com.mykytadu.data.repository.DefaultPreferencesRepository
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.repository.LibraryRepository
import br.com.mykytadu.domain.repository.PreferencesRepository
import org.koin.dsl.module

val RepositoryModule = module {
    single<AnimeRepository> {
        AniListAnimeRepository(animeApi = get())
    }

    single<LibraryRepository> {
        DefaultLibraryRepository(localDataSource = get())
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
}