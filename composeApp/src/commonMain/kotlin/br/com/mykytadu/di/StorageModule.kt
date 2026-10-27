package br.com.mykytadu.di

import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.PreferencesLocalDataSource
import org.koin.dsl.module

expect fun provideLibraryLocalDataSource(): LibraryLocalDataSource
expect fun providePreferencesLocalDataSource(): PreferencesLocalDataSource

val StorageModule = module {
    single<LibraryLocalDataSource> {
        provideLibraryLocalDataSource()
    }

    single<PreferencesLocalDataSource> {
        providePreferencesLocalDataSource()
    }
}
