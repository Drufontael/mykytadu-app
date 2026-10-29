package br.com.mykytadu.data.local.entity

import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.cache.CacheStatus

data class CachedAnimeDetailsEntity(
    val id: CatalogAnimeId,
    val details: AnimeDetails,
    val cachedAtEpochMillis: Long,
    val expiresAtEpochMillis: Long,
) {
    fun isExpired(currentTimeMillis: Long): Boolean = currentTimeMillis > expiresAtEpochMillis

    fun status(currentTimeMillis: Long): CacheStatus =
        if (isExpired(currentTimeMillis)) CacheStatus.STALE else CacheStatus.FRESH
}
