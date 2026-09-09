package br.com.mykytadu.data.repository

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.mapper.toAnimeDetails
import br.com.mykytadu.data.mapper.toPagedAnimeSummaries
import br.com.mykytadu.data.mapper.toRepositoryFailure
import br.com.mykytadu.data.remote.api.AnimeApi
import br.com.mykytadu.domain.model.AniListAnimeId
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult

internal class AniListAnimeRepository(
    private val animeApi: AnimeApi,
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

        return when (
            val result = animeApi.searchAnime(
                search = normalizedQuery,
                page = page,
                perPage = perPage,
            )
        ) {
            is NetworkResult.Success ->
                mapDomainValue { result.value.toPagedAnimeSummaries() }

            is NetworkResult.Failure ->
                result.error.toRepositoryFailure()
        }
    }

    override suspend fun getAnimeDetails(
        id: AniListAnimeId,
    ): RepositoryResult<AnimeDetails> =
        when (val result = animeApi.getAnimeDetails(id.value)) {
            is NetworkResult.Success ->
                mapDomainValue { result.value.toAnimeDetails() }

            is NetworkResult.Failure ->
                result.error.toRepositoryFailure()
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