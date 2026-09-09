package br.com.mykytadu.domain.repository

import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.AniListAnimeId
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.result.RepositoryResult

interface AnimeRepository {

    suspend fun searchAnime(
        query: String,
        page: Int,
        perPage: Int,
    ): RepositoryResult<PagedResult<AnimeSummary>>

    suspend fun getAnimeDetails(
        id: AniListAnimeId,
    ): RepositoryResult<AnimeDetails>
}