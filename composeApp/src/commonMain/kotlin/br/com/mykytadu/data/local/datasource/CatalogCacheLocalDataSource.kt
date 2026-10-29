package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.CachedAnimeDetailsEntity
import br.com.mykytadu.data.local.entity.CachedSearchEntity
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PagedResult

/**
 * Contrato de persistência local para cache de buscas e detalhes de animes.
 */
interface CatalogCacheLocalDataSource {
    suspend fun getSearch(query: String, page: Int, perPage: Int): CachedSearchEntity?

    suspend fun putSearch(
        query: String,
        page: Int,
        perPage: Int,
        result: PagedResult<AnimeSummary>,
        cachedAt: Long,
        expiresAt: Long,
    )

    suspend fun getAnimeDetails(id: CatalogAnimeId): CachedAnimeDetailsEntity?

    suspend fun putAnimeDetails(
        details: AnimeDetails,
        cachedAt: Long,
        expiresAt: Long,
    )

    suspend fun clearExpired(currentTimeMillis: Long)

    suspend fun clearCatalogCache()

    suspend fun getSearchEntriesCount(): Int

    suspend fun getDetailsEntriesCount(): Int
}
