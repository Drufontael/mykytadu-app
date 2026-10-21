package br.com.mykytadu.di

import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import org.koin.dsl.module

expect fun provideLibraryLocalDataSource(): LibraryLocalDataSource

val StorageModule = module {
    single<LibraryLocalDataSource> {
        provideLibraryLocalDataSource()
    }
}
