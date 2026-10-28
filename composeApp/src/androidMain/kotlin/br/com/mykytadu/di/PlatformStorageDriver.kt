package br.com.mykytadu.di

import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemoryPreferencesLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemorySecureStorageDriver
import br.com.mykytadu.data.local.datasource.InMemorySyncQueueLocalDataSource
import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.PreferencesLocalDataSource
import br.com.mykytadu.data.local.datasource.SecureStorageDriver
import br.com.mykytadu.data.local.datasource.SyncQueueLocalDataSource

actual fun provideLibraryLocalDataSource(): LibraryLocalDataSource =
    InMemoryLibraryLocalDataSource()

actual fun providePreferencesLocalDataSource(): PreferencesLocalDataSource =
    InMemoryPreferencesLocalDataSource()

actual fun provideSecureStorageDriver(): SecureStorageDriver =
    InMemorySecureStorageDriver()

actual fun provideSyncQueueLocalDataSource(): SyncQueueLocalDataSource =
    InMemorySyncQueueLocalDataSource()
