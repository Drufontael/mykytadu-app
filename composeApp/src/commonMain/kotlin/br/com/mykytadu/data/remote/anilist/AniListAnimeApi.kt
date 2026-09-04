package br.com.mykytadu.data.remote.anilist

import br.com.mykytadu.core.network.GraphQlException
import br.com.mykytadu.core.network.InvalidRequestException
import br.com.mykytadu.core.network.InvalidResponseException
import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.core.network.safeNetworkCall
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchDataDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchVariablesDto
import br.com.mykytadu.data.remote.api.AnimeApi
import br.com.mykytadu.data.remote.graphql.GraphQlRequest
import br.com.mykytadu.data.remote.graphql.GraphQlResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDataDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsVariablesDto

class AniListAnimeApi(
    private val httpClient: HttpClient,
) : AnimeApi {

    override suspend fun searchAnime(
        search: String,
        page: Int,
        perPage: Int,
    ): NetworkResult<AnimeSearchPageDto> {

        val normalizedSearch = search.trim()

        if (normalizedSearch.isEmpty()) {
            return invalidRequest("Search term must not be blank.")
        }

        if (page < 1) {
            return invalidRequest("Page must be greater than or equal to 1.")
        }

        if (perPage < 1) {
            return invalidRequest("Per page must be greater than or equal to 1.")
        }

        val result = safeNetworkCall {
            httpClient.post(ANILIST_ENDPOINT) {
                setBody(
                    GraphQlRequest(
                        query = AniListQueries.searchAnime,
                        variables = AnimeSearchVariablesDto(
                            search = normalizedSearch,
                            page = page,
                            perPage = perPage,
                        ),
                    )
                )
            }.body<GraphQlResponse<AnimeSearchDataDto>>()
        }

        return when (result) {
            is NetworkResult.Failure -> result
            is NetworkResult.Success -> result.value.toSearchResult()
        }
    }

    override suspend fun getAnimeDetails(
        id: Int,
    ): NetworkResult<AnimeDetailsDto> {
        if (id < 1) {
            return invalidRequest(
                "Anime id must be greater than or equal to 1."
            )
        }

        val result = safeNetworkCall {
            httpClient.post(ANILIST_ENDPOINT) {
                setBody(
                    GraphQlRequest(
                        query = AniListQueries.getAnimeDetails,
                        variables = AnimeDetailsVariablesDto(id = id),
                    )
                )
            }.body<GraphQlResponse<AnimeDetailsDataDto>>()
        }

        return when (result) {
            is NetworkResult.Failure -> result
            is NetworkResult.Success -> result.value.toDetailsResult()
        }
    }

    private fun GraphQlResponse<AnimeSearchDataDto>.toSearchResult():
            NetworkResult<AnimeSearchPageDto> {
        val messages = errors.orEmpty().map { error -> error.message }

        if (messages.isNotEmpty()) {
            return NetworkResult.Failure(
                NetworkFailure.GraphQl(
                    messages = messages,
                    cause = GraphQlException(messages),
                )
            )
        }

        val page = data?.page
            ?: return NetworkResult.Failure(
                NetworkFailure.InvalidResponse(
                    message = "AniList response does not contain Page data.",
                    cause = InvalidResponseException(
                        "AniList response does not contain Page data."
                    ),
                )
            )

        if (page.pageInfo == null) {
            val message = "AniList response does not contain PageInfo data."

            return NetworkResult.Failure(
                NetworkFailure.InvalidResponse(
                    message = message,
                    cause = InvalidResponseException(message),
                )
            )
        }

        return NetworkResult.Success(page)
    }

    private fun GraphQlResponse<AnimeDetailsDataDto>.toDetailsResult():
            NetworkResult<AnimeDetailsDto> {
        val messages = errors.orEmpty().map { error -> error.message }

        if (messages.isNotEmpty()) {
            return NetworkResult.Failure(
                NetworkFailure.GraphQl(
                    messages = messages,
                    cause = GraphQlException(messages),
                )
            )
        }

        val anime = data?.media
            ?: return NetworkResult.Failure(
                NetworkFailure.InvalidResponse(
                    message = "AniList response does not contain Media data.",
                    cause = InvalidResponseException(
                        "AniList response does not contain Media data."
                    ),
                )
            )

        return NetworkResult.Success(anime)
    }

    private fun invalidRequest(
        message: String,
    ): NetworkResult.Failure =
        NetworkResult.Failure(
            NetworkFailure.InvalidRequest(
                message = message,
                cause = InvalidRequestException(message),
            )
        )

    private companion object {
        const val ANILIST_ENDPOINT = "https://graphql.anilist.co"
    }
}