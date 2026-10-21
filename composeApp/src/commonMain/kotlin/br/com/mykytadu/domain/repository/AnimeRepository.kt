package br.com.mykytadu.domain.repository

import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PageInfo
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.result.RepositoryResult

interface AnimeRepository {

    suspend fun searchAnime(
        query: String,
        page: Int,
        perPage: Int,
    ): RepositoryResult<PagedResult<AnimeSummary>>

    suspend fun getAnimeDetails(
        id: CatalogAnimeId,
    ): RepositoryResult<AnimeDetails>

    suspend fun getTrendingAnime(
        page: Int = 1,
        perPage: Int = 10,
    ): RepositoryResult<PagedResult<AnimeSummary>> =
        RepositoryResult.Success(
            PagedResult(
                items = emptyList(),
                pageInfo = PageInfo(
                    currentPage = page,
                    lastPage = 1,
                    hasNextPage = false,
                    perPage = perPage,
                    total = 0,
                ),
            )
        )
}