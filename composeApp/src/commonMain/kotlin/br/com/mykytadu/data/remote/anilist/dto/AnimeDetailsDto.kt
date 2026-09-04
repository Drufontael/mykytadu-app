package br.com.mykytadu.data.remote.anilist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeDetailsDataDto(
    @SerialName("Media")
    val media: AnimeDetailsDto? = null,
)

@Serializable
data class AnimeDetailsDto(
    val id: Int,
    val idMal: Int? = null,
    val title: AnimeTitleDto,
    val synonyms: List<String> = emptyList(),
    val description: String? = null,
    val coverImage: AnimeCoverImageDto? = null,
    val bannerImage: String? = null,
    val format: String? = null,
    val status: String? = null,
    val source: String? = null,
    val episodes: Int? = null,
    val duration: Int? = null,
    val season: String? = null,
    val seasonYear: Int? = null,
    val countryOfOrigin: String? = null,
    val isAdult: Boolean? = null,
    val startDate: AniListDateDto? = null,
    val endDate: AniListDateDto? = null,
    val genres: List<String> = emptyList(),
    val averageScore: Int? = null,
    val meanScore: Int? = null,
    val popularity: Int? = null,
    val favourites: Int? = null,
    val studios: AnimeStudioConnectionDto? = null,
    val trailer: AnimeTrailerDto? = null,
    val nextAiringEpisode: NextAiringEpisodeDto? = null,
    val relations: AnimeRelationsDto? = null,
    val externalLinks: List<AnimeExternalLinkDto> = emptyList(),
)