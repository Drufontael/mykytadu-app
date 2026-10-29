package br.com.mykytadu.data.local.entity

import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.model.cache.CacheStatus

data class CachedSearchEntity(
    val query: String,
    val page: Int,
    val perPage: Int,
    val pagedResult: PagedResult<AnimeSummary>,
    val cachedAtEpochMillis: Long,
    val expiresAtEpochMillis: Long,
) {
    fun isExpired(currentTimeMillis: Long): Boolean = currentTimeMillis > expiresAtEpochMillis

    fun status(currentTimeMillis: Long): CacheStatus =
        if (isExpired(currentTimeMillis)) CacheStatus.STALE else CacheStatus.FRESH
}
