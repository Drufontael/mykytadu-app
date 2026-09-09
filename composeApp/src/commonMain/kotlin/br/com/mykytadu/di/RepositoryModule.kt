package br.com.mykytadu.di

import br.com.mykytadu.data.repository.AniListAnimeRepository
import br.com.mykytadu.domain.repository.AnimeRepository
import org.koin.dsl.module

val RepositoryModule = module {
    single<AnimeRepository> {
        AniListAnimeRepository(animeApi = get())
    }
}