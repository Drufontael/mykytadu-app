package br.com.mykytadu.di

import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemoryPreferencesLocalDataSource
import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.PreferencesLocalDataSource

actual fun provideLibraryLocalDataSource(): LibraryLocalDataSource =
    InMemoryLibraryLocalDataSource()

actual fun providePreferencesLocalDataSource(): PreferencesLocalDataSource =
    InMemoryPreferencesLocalDataSource()
