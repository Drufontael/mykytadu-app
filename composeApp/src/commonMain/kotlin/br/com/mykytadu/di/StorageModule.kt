package br.com.mykytadu.di

import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.PreferencesLocalDataSource
import br.com.mykytadu.data.local.datasource.SecureStorageDriver
import br.com.mykytadu.data.local.datasource.SyncQueueLocalDataSource
import org.koin.dsl.module

expect fun provideLibraryLocalDataSource(): LibraryLocalDataSource
expect fun providePreferencesLocalDataSource(): PreferencesLocalDataSource
expect fun provideSecureStorageDriver(): SecureStorageDriver
expect fun provideSyncQueueLocalDataSource(): SyncQueueLocalDataSource

val StorageModule = module {
    single<LibraryLocalDataSource> {
        provideLibraryLocalDataSource()
    }

    single<PreferencesLocalDataSource> {
        providePreferencesLocalDataSource()
    }

    single<SecureStorageDriver> {
        provideSecureStorageDriver()
    }

    single<SyncQueueLocalDataSource> {
        provideSyncQueueLocalDataSource()
    }
}
