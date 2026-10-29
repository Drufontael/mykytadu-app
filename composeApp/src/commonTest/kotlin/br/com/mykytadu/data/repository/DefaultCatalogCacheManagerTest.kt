package br.com.mykytadu.data.repository

import br.com.mykytadu.data.local.datasource.InMemoryCatalogCacheLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemoryPreferencesLocalDataSource
import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import br.com.mykytadu.data.local.entity.PreferencesEntity
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PageInfo
import br.com.mykytadu.domain.model.PagedResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DefaultCatalogCacheManagerTest {

    private fun createPagedResult(items: List<AnimeSummary> = emptyList()) = PagedResult(
        items = items,
        pageInfo = PageInfo(
            currentPage = 1,
            lastPage = 1,
            hasNextPage = false,
            perPage = 20,
            total = items.size,
        ),
    )

    @Test
    fun `clearCatalogCache limpa apenas cache do catalogo e preserva biblioteca e preferencias intactas`() = runTest {
        val cacheDataSource = InMemoryCatalogCacheLocalDataSource()
        val libraryDataSource = InMemoryLibraryLocalDataSource()
        val preferencesDataSource = InMemoryPreferencesLocalDataSource()
        val cacheManager = DefaultCatalogCacheManager(cacheDataSource)

        // 1. Popula dados de cache temporários
        val pagedResult = createPagedResult(listOf(AnimeSummary(id = CatalogAnimeId("1"))))
        cacheDataSource.putSearch("Naruto", 1, 20, pagedResult, cachedAt = 1000L, expiresAt = 2000L)
        cacheDataSource.putAnimeDetails(
            details = AnimeDetails(id = CatalogAnimeId("1")),
            cachedAt = 1000L,
            expiresAt = 5000L,
        )

        // 2. Popula dados permanentes do usuário (biblioteca e preferências)
        val libraryEntry = LibraryEntryEntity(
            id = "lib-1",
            animeId = "1",
            status = "WATCHING",
            progressCurrent = 10,
            progressTotal = 220,
            isFavorite = true,
            rating = 90,
            notes = "Anime da infância",
            createdAtEpochMillis = 1000L,
        )
        libraryDataSource.upsertEntry(libraryEntry)
        preferencesDataSource.savePreferences(
            PreferencesEntity(theme = "DARK", adultContent = true),
        )

        // Verifica estado inicial antes da limpeza
        assertEquals(1, cacheManager.getCachedSearchesCount())
        assertEquals(1, cacheManager.getCachedDetailsCount())
        assertEquals(1, libraryDataSource.getAllEntries().size)

        // 3. Executa limpeza controlada do cache
        cacheManager.clearCatalogCache()

        // 4. Valida que o cache foi esvaziado
        assertEquals(0, cacheManager.getCachedSearchesCount())
        assertEquals(0, cacheManager.getCachedDetailsCount())

        // 5. ISOLAMENTO ESTRITO: Valida que a biblioteca do usuário e preferências estão 100% preservadas
        val preservedEntries = libraryDataSource.getAllEntries()
        assertEquals(1, preservedEntries.size)
        val preserved = preservedEntries.first()
        assertEquals("lib-1", preserved.id)
        assertEquals("1", preserved.animeId)
        assertEquals("WATCHING", preserved.status)
        assertEquals(10, preserved.progressCurrent)
        assertEquals(true, preserved.isFavorite)

        val preservedPrefs = preferencesDataSource.getPreferences()
        assertNotNull(preservedPrefs)
        assertEquals("DARK", preservedPrefs.theme)
        assertEquals(true, preservedPrefs.adultContent)
    }

    @Test
    fun `clearExpired remove apenas entradas expiradas no cache`() = runTest {
        val cacheDataSource = InMemoryCatalogCacheLocalDataSource()
        val cacheManager = DefaultCatalogCacheManager(cacheDataSource)

        val pagedResult = createPagedResult(listOf(AnimeSummary(id = CatalogAnimeId("1"))))
        // Busca 1 expirada em t=2000
        cacheDataSource.putSearch("Naruto", 1, 20, pagedResult, cachedAt = 1000L, expiresAt = 2000L)
        // Busca 2 fresca até t=5000
        cacheDataSource.putSearch("Bleach", 1, 20, pagedResult, cachedAt = 1000L, expiresAt = 5000L)

        assertEquals(2, cacheManager.getCachedSearchesCount())

        // Limpa com t=3000 -> apenas Naruto deve expirar
        cacheManager.clearExpired(currentTimeMillis = 3000L)

        assertEquals(1, cacheManager.getCachedSearchesCount())
    }
}
