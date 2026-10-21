package br.com.mykytadu.di

import br.com.mykytadu.data.repository.AniListAnimeRepository
import br.com.mykytadu.data.repository.DefaultLibraryRepository
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.repository.LibraryRepository
import org.koin.dsl.module

val RepositoryModule = module {
    single<AnimeRepository> {
        AniListAnimeRepository(animeApi = get())
    }

    single<LibraryRepository> {
        DefaultLibraryRepository(localDataSource = get())
    }
}