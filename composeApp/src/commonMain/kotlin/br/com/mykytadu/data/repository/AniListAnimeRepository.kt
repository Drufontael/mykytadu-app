package br.com.mykytadu.data.repository

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.local.datasource.CatalogCacheLocalDataSource
import br.com.mykytadu.data.mapper.toAniListAnimeIdOrNull
import br.com.mykytadu.data.mapper.toAnimeDetails
import br.com.mykytadu.data.mapper.toPagedAnimeSummaries
import br.com.mykytadu.data.mapper.toRepositoryFailure
import br.com.mykytadu.data.remote.api.AnimeApi
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.model.cache.CachePolicy
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult

internal class AniListAnimeRepository(
    private val animeApi: AnimeApi,
    private val catalogCacheDataSource: CatalogCacheLocalDataSource? = null,
    private val timeProvider: () -> Long = { kotlin.time.Clock.System.now().toEpochMilliseconds() },
) : AnimeRepository {

    override suspend fun searchAnime(
        query: String,
        page: Int,
        perPage: Int,
    ): RepositoryResult<PagedResult<AnimeSummary>> {
        val normalizedQuery = query.trim()

        if (normalizedQuery.isEmpty() || page <= 0 || perPage <= 0) {
            return RepositoryResult.Failure(RepositoryFailure.InvalidInput)
        }

        val currentTime = timeProvider()
        val cached = catalogCacheDataSource?.getSearch(normalizedQuery, page, perPage)

        // Se houver cache fresco (dentro do TTL), retorna imediatamente sem chamar a rede
        if (cached != null && !cached.isExpired(currentTime)) {
            return RepositoryResult.Success(cached.pagedResult)
        }

        return when (
            val result = animeApi.searchAnime(
                search = normalizedQuery,
                page = page,
                perPage = perPage,
            )
        ) {
            is NetworkResult.Success -> {
                val mapped = mapDomainValue { result.value.toPagedAnimeSummaries() }
                if (mapped is RepositoryResult.Success) {
                    val now = timeProvider()
                    catalogCacheDataSource?.putSearch(
                        query = normalizedQuery,
                        page = page,
                        perPage = perPage,
                        result = mapped.value,
                        cachedAt = now,
                        expiresAt = now + CachePolicy.DEFAULT_SEARCH_TTL_MILLIS,
                    )
                }
                mapped
            }

            is NetworkResult.Failure -> {
                // Fallback offline: se a rede falhar e houver cache (mesmo expirado), retorna o cache existente com indicação de offline
                if (cached != null) {
                    RepositoryResult.Success(
                        PagedResult(
                            items = cached.pagedResult.items,
                            pageInfo = cached.pagedResult.pageInfo,
                            isOfflineFallback = true,
                        ),
                    )
                } else {
                    result.error.toRepositoryFailure()
                }
            }
        }
    }

    override suspend fun getAnimeDetails(
        id: CatalogAnimeId,
    ): RepositoryResult<AnimeDetails> {
        val aniListId = id.toAniListAnimeIdOrNull()
            ?: return RepositoryResult.Failure(RepositoryFailure.InvalidInput)

        val currentTime = timeProvider()
        val cached = catalogCacheDataSource?.getAnimeDetails(id)

        // Se houver cache fresco, retorna imediatamente
        if (cached != null && !cached.isExpired(currentTime)) {
            return RepositoryResult.Success(cached.details)
        }

        return when (val result = animeApi.getAnimeDetails(aniListId)) {
            is NetworkResult.Success -> {
                val mapped = mapDomainValue { result.value.toAnimeDetails() }
                if (mapped is RepositoryResult.Success) {
                    val now = timeProvider()
                    catalogCacheDataSource?.putAnimeDetails(
                        details = mapped.value,
                        cachedAt = now,
                        expiresAt = now + CachePolicy.DEFAULT_DETAILS_TTL_MILLIS,
                    )
                }
                mapped
            }

            is NetworkResult.Failure -> {
                // Fallback offline: diante de erro de rede, utiliza detalhes em cache com indicação de offline
                if (cached != null) {
                    RepositoryResult.Success(cached.details.copy(isOfflineFallback = true))
                } else {
                    result.error.toRepositoryFailure()
                }
            }
        }
    }

    override suspend fun getTrendingAnime(
        page: Int,
        perPage: Int,
    ): RepositoryResult<PagedResult<AnimeSummary>> {
        if (page <= 0 || perPage <= 0) {
            return RepositoryResult.Failure(RepositoryFailure.InvalidInput)
        }

        val trendingCacheKey = "__trending__"
        val currentTime = timeProvider()
        val cached = catalogCacheDataSource?.getSearch(trendingCacheKey, page, perPage)

        if (cached != null && !cached.isExpired(currentTime)) {
            return RepositoryResult.Success(cached.pagedResult)
        }

        return when (
            val result = animeApi.getTrendingAnime(
                page = page,
                perPage = perPage,
            )
        ) {
            is NetworkResult.Success -> {
                val mapped = mapDomainValue { result.value.toPagedAnimeSummaries() }
                if (mapped is RepositoryResult.Success) {
                    val now = timeProvider()
                    catalogCacheDataSource?.putSearch(
                        query = trendingCacheKey,
                        page = page,
                        perPage = perPage,
                        result = mapped.value,
                        cachedAt = now,
                        expiresAt = now + CachePolicy.DEFAULT_SEARCH_TTL_MILLIS,
                    )
                }
                mapped
            }

            is NetworkResult.Failure -> {
                if (cached != null) {
                    RepositoryResult.Success(
                        PagedResult(
                            items = cached.pagedResult.items,
                            pageInfo = cached.pagedResult.pageInfo,
                            isOfflineFallback = true,
                        ),
                    )
                } else {
                    result.error.toRepositoryFailure()
                }
            }
        }
    }

    private inline fun <T> mapDomainValue(
        mapper: () -> T,
    ): RepositoryResult<T> =
        try {
            RepositoryResult.Success(mapper())
        } catch (cause: IllegalArgumentException) {
            RepositoryResult.Failure(
                reason = RepositoryFailure.InvalidData,
                cause = cause,
            )
        }
}

