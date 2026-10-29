package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.model.cache.CacheStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class InMemoryCatalogCacheLocalDataSourceTest {

    private fun createPagedResult(items: List<AnimeSummary> = emptyList()) = PagedResult(
        items = items,
        pageInfo = br.com.mykytadu.domain.model.PageInfo(
            currentPage = 1,
            lastPage = 1,
            hasNextPage = false,
            perPage = 20,
            total = items.size,
        )
    )

    @Test
    fun `putSearch e getSearch armazenam e recuperam resultado paginado com status de frescor`() = runTest {
        val dataSource = InMemoryCatalogCacheLocalDataSource()
        val query = "naruto"
        val pagedResult = createPagedResult(listOf(AnimeSummary(id = CatalogAnimeId("1"))))

        dataSource.putSearch(
            query = query,
            page = 1,
            perPage = 20,
            result = pagedResult,
            cachedAt = 1000L,
            expiresAt = 3000L,
        )

        val cached = dataSource.getSearch("Naruto", 1, 20)
        assertNotNull(cached)
        assertEquals("naruto", cached.query)
        assertEquals(1, cached.pagedResult.items.size)
        assertEquals(CacheStatus.FRESH, cached.status(currentTimeMillis = 2000L))
        assertEquals(CacheStatus.STALE, cached.status(currentTimeMillis = 4000L))
    }

    @Test
    fun `putAnimeDetails e getAnimeDetails armazenam e recuperam detalhes`() = runTest {
        val dataSource = InMemoryCatalogCacheLocalDataSource()
        val id = CatalogAnimeId("10")
        val details = AnimeDetails(id = id)

        dataSource.putAnimeDetails(
            details = details,
            cachedAt = 1000L,
            expiresAt = 5000L,
        )

        val cached = dataSource.getAnimeDetails(id)
        assertNotNull(cached)
        assertEquals(id, cached.id)
        assertEquals(CacheStatus.FRESH, cached.status(currentTimeMillis = 2000L))
        assertEquals(CacheStatus.STALE, cached.status(currentTimeMillis = 6000L))
    }

    @Test
    fun `politica LRU descarta busca mais antiga quando limite maximo e atingido`() = runTest {
        val dataSource = InMemoryCatalogCacheLocalDataSource(maxSearchEntries = 2)

        dataSource.putSearch("query-1", 1, 10, createPagedResult(), 1000L, 5000L)
        dataSource.putSearch("query-2", 1, 10, createPagedResult(), 1000L, 5000L)

        // Acessa query-1 para torná-la mais recente que query-2
        dataSource.getSearch("query-1", 1, 10)

        // Insere terceira busca, deve descartar query-2
        dataSource.putSearch("query-3", 1, 10, createPagedResult(), 1000L, 5000L)

        assertEquals(2, dataSource.getSearchEntriesCount())
        assertNotNull(dataSource.getSearch("query-1", 1, 10))
        assertNotNull(dataSource.getSearch("query-3", 1, 10))
        assertNull(dataSource.getSearch("query-2", 1, 10))
    }

    @Test
    fun `clearExpired remove entradas expiradas e mantem validas`() = runTest {
        val dataSource = InMemoryCatalogCacheLocalDataSource()

        // Busca 1 expirada em T=2000
        dataSource.putSearch("expirada", 1, 10, createPagedResult(), 1000L, 2000L)
        // Busca 2 válida até T=5000
        dataSource.putSearch("valida", 1, 10, createPagedResult(), 1000L, 5000L)

        // Detalhe 1 expirado em T=2000
        dataSource.putAnimeDetails(AnimeDetails(CatalogAnimeId("1")), 1000L, 2000L)
        // Detalhe 2 válido até T=5000
        dataSource.putAnimeDetails(AnimeDetails(CatalogAnimeId("2")), 1000L, 5000L)

        dataSource.clearExpired(currentTimeMillis = 3000L)

        assertEquals(1, dataSource.getSearchEntriesCount())
        assertNull(dataSource.getSearch("expirada", 1, 10))
        assertNotNull(dataSource.getSearch("valida", 1, 10))

        assertEquals(1, dataSource.getDetailsEntriesCount())
        assertNull(dataSource.getAnimeDetails(CatalogAnimeId("1")))
        assertNotNull(dataSource.getAnimeDetails(CatalogAnimeId("2")))
    }

    @Test
    fun `clearCatalogCache remove todas as buscas e detalhes`() = runTest {
        val dataSource = InMemoryCatalogCacheLocalDataSource()
        dataSource.putSearch("q", 1, 10, createPagedResult(), 1000L, 5000L)
        dataSource.putAnimeDetails(AnimeDetails(CatalogAnimeId("1")), 1000L, 5000L)

        dataSource.clearCatalogCache()

        assertEquals(0, dataSource.getSearchEntriesCount())
        assertEquals(0, dataSource.getDetailsEntriesCount())
    }
}
