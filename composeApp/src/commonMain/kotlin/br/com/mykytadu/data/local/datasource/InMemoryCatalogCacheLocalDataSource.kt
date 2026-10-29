package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.CachedAnimeDetailsEntity
import br.com.mykytadu.data.local.entity.CachedSearchEntity
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.model.cache.CachePolicy
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementação em memória thread-safe de [CatalogCacheLocalDataSource] com política de descarte LRU.
 */
class InMemoryCatalogCacheLocalDataSource(
    private val maxSearchEntries: Int = CachePolicy.MAX_SEARCH_ENTRIES,
    private val maxDetailsEntries: Int = CachePolicy.MAX_DETAILS_ENTRIES,
) : CatalogCacheLocalDataSource {

    private val mutex = Mutex()

    // Chave composta para busca: "query::page::perPage"
    private val searchCache = LinkedHashMap<String, CachedSearchEntity>()
    private val detailsCache = LinkedHashMap<String, CachedAnimeDetailsEntity>()

    override suspend fun getSearch(query: String, page: Int, perPage: Int): CachedSearchEntity? =
        mutex.withLock {
            val key = searchKey(query, page, perPage)
            val entry = searchCache.remove(key)
            if (entry != null) {
                // Reinsere para marcar como mais recentemente usado (LRU)
                searchCache[key] = entry
            }
            entry
        }

    override suspend fun putSearch(
        query: String,
        page: Int,
        perPage: Int,
        result: PagedResult<AnimeSummary>,
        cachedAt: Long,
        expiresAt: Long,
    ) {
        mutex.withLock {
            val key = searchKey(query, page, perPage)
            searchCache.remove(key)

            // Se atingir o limite, descarta a entrada mais antiga (primeiro elemento do LinkedHashMap)
            while (searchCache.size >= maxSearchEntries) {
                val oldestKey = searchCache.keys.firstOrNull() ?: break
                searchCache.remove(oldestKey)
            }

            searchCache[key] = CachedSearchEntity(
                query = query,
                page = page,
                perPage = perPage,
                pagedResult = result,
                cachedAtEpochMillis = cachedAt,
                expiresAtEpochMillis = expiresAt,
            )
        }
    }

    override suspend fun getAnimeDetails(id: CatalogAnimeId): CachedAnimeDetailsEntity? =
        mutex.withLock {
            val key = id.value
            val entry = detailsCache.remove(key)
            if (entry != null) {
                // Reinsere para marcar como mais recentemente usado (LRU)
                detailsCache[key] = entry
            }
            entry
        }

    override suspend fun putAnimeDetails(
        details: AnimeDetails,
        cachedAt: Long,
        expiresAt: Long,
    ) {
        mutex.withLock {
            val key = details.id.value
            detailsCache.remove(key)

            // Se atingir o limite, descarta a entrada mais antiga
            while (detailsCache.size >= maxDetailsEntries) {
                val oldestKey = detailsCache.keys.firstOrNull() ?: break
                detailsCache.remove(oldestKey)
            }

            detailsCache[key] = CachedAnimeDetailsEntity(
                id = details.id,
                details = details,
                cachedAtEpochMillis = cachedAt,
                expiresAtEpochMillis = expiresAt,
            )
        }
    }

    override suspend fun clearExpired(currentTimeMillis: Long) {
        mutex.withLock {
            searchCache.entries.removeAll { it.value.isExpired(currentTimeMillis) }
            detailsCache.entries.removeAll { it.value.isExpired(currentTimeMillis) }
        }
    }

    override suspend fun clearCatalogCache() {
        mutex.withLock {
            searchCache.clear()
            detailsCache.clear()
        }
    }

    override suspend fun getSearchEntriesCount(): Int = mutex.withLock {
        searchCache.size
    }

    override suspend fun getDetailsEntriesCount(): Int = mutex.withLock {
        detailsCache.size
    }

    private fun searchKey(query: String, page: Int, perPage: Int): String =
        "${query.trim().lowercase()}::$page::$perPage"
}
