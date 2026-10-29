package br.com.mykytadu.di

import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.PreferencesLocalDataSource
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class StorageModuleTest {

    @Test
    fun `deve resolver LibraryLocalDataSource a partir do StorageModule`() {
        val koinApplication = koinApplication {
            modules(StorageModule)
        }

        try {
            val dataSource = koinApplication.koin.get<LibraryLocalDataSource>()

            assertNotNull(dataSource)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de LibraryLocalDataSource`() {
        val koinApplication = koinApplication {
            modules(StorageModule)
        }

        try {
            val dataSource1 = koinApplication.koin.get<LibraryLocalDataSource>()
            val dataSource2 = koinApplication.koin.get<LibraryLocalDataSource>()

            assertSame(dataSource1, dataSource2)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver PreferencesLocalDataSource a partir do StorageModule`() {
        val koinApplication = koinApplication {
            modules(StorageModule)
        }

        try {
            val dataSource = koinApplication.koin.get<PreferencesLocalDataSource>()

            assertNotNull(dataSource)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de PreferencesLocalDataSource`() {
        val koinApplication = koinApplication {
            modules(StorageModule)
        }

        try {
            val dataSource1 = koinApplication.koin.get<PreferencesLocalDataSource>()
            val dataSource2 = koinApplication.koin.get<PreferencesLocalDataSource>()

            assertSame(dataSource1, dataSource2)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver SecureStorageDriver a partir do StorageModule`() {
        val koinApplication = koinApplication {
            modules(StorageModule)
        }

        try {
            val driver = koinApplication.koin.get<br.com.mykytadu.data.local.datasource.SecureStorageDriver>()

            assertNotNull(driver)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de SecureStorageDriver`() {
        val koinApplication = koinApplication {
            modules(StorageModule)
        }

        try {
            val driver1 = koinApplication.koin.get<br.com.mykytadu.data.local.datasource.SecureStorageDriver>()
            val driver2 = koinApplication.koin.get<br.com.mykytadu.data.local.datasource.SecureStorageDriver>()

            assertSame(driver1, driver2)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver SyncQueueLocalDataSource a partir do StorageModule`() {
        val koinApplication = koinApplication {
            modules(StorageModule)
        }

        try {
            val dataSource = koinApplication.koin.get<br.com.mykytadu.data.local.datasource.SyncQueueLocalDataSource>()

            assertNotNull(dataSource)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de SyncQueueLocalDataSource`() {
        val koinApplication = koinApplication {
            modules(StorageModule)
        }

        try {
            val dataSource1 = koinApplication.koin.get<br.com.mykytadu.data.local.datasource.SyncQueueLocalDataSource>()
            val dataSource2 = koinApplication.koin.get<br.com.mykytadu.data.local.datasource.SyncQueueLocalDataSource>()

            assertSame(dataSource1, dataSource2)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve resolver CatalogCacheLocalDataSource a partir do StorageModule`() {
        val koinApplication = koinApplication {
            modules(StorageModule)
        }

        try {
            val dataSource = koinApplication.koin.get<br.com.mykytadu.data.local.datasource.CatalogCacheLocalDataSource>()

            assertNotNull(dataSource)
        } finally {
            koinApplication.close()
        }
    }

    @Test
    fun `deve retornar a mesma instancia de CatalogCacheLocalDataSource`() {
        val koinApplication = koinApplication {
            modules(StorageModule)
        }

        try {
            val dataSource1 = koinApplication.koin.get<br.com.mykytadu.data.local.datasource.CatalogCacheLocalDataSource>()
            val dataSource2 = koinApplication.koin.get<br.com.mykytadu.data.local.datasource.CatalogCacheLocalDataSource>()

            assertSame(dataSource1, dataSource2)
        } finally {
            koinApplication.close()
        }
    }
}

