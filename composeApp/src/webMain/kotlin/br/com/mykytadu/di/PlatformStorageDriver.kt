package br.com.mykytadu.di

import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource

actual fun provideLibraryLocalDataSource(): LibraryLocalDataSource =
    InMemoryLibraryLocalDataSource()
