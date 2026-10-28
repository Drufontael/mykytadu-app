package br.com.mykytadu.di

import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.features.anime.AnimeDetailsViewModel
import br.com.mykytadu.features.home.HomeViewModel
import br.com.mykytadu.features.library.LibraryViewModel
import br.com.mykytadu.features.search.SearchViewModel
import br.com.mykytadu.features.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel {
        HomeViewModel(
            libraryRepository = get(),
            animeRepository = get(),
        )
    }
    viewModel {
        SearchViewModel(animeRepository = get())
    }
    viewModel { parameters ->
        AnimeDetailsViewModel(
            id = parameters.get<CatalogAnimeId>(),
            animeRepository = get(),
            libraryRepository = getOrNull(),
        )
    }
    viewModel {
        LibraryViewModel(
            libraryRepository = get(),
            animeRepository = get(),
        )
    }
    viewModel {
        SettingsViewModel(preferencesRepository = get())
    }
    viewModel {
        br.com.mykytadu.features.auth.AuthViewModel(authRepository = get())
    }
    viewModel {
        br.com.mykytadu.features.profile.ProfileViewModel(
            profileRepository = get(),
            syncRepository = get(),
            authRepository = get(),
        )
    }
}
