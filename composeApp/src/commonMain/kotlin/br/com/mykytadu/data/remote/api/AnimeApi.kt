package br.com.mykytadu.data.remote.api

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDto

interface AnimeApi {

    suspend fun searchAnime(
        search: String,
        page: Int,
        perPage: Int,
    ): NetworkResult<AnimeSearchPageDto>

    suspend fun getAnimeDetails(
        id: Int,
    ): NetworkResult<AnimeDetailsDto>
}