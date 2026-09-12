package br.com.mykytadu.di

import br.com.mykytadu.features.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel {
        SearchViewModel(animeRepository = get())
    }
}
