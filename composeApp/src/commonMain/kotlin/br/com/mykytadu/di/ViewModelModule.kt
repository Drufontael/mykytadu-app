package br.com.mykytadu.di

import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.features.anime.AnimeDetailsViewModel
import br.com.mykytadu.features.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel {
        SearchViewModel(animeRepository = get())
    }
    viewModel { parameters ->
        AnimeDetailsViewModel(
            id = parameters.get<CatalogAnimeId>(),
            animeRepository = get(),
        )
    }
}
