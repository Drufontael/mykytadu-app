package br.com.mykytadu.data.remote.anilist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeSearchDataDto(
    @SerialName("Page")
    val page: AnimeSearchPageDto? = null,
)

@Serializable
data class AnimeSearchPageDto(
    val pageInfo: PageInfoDto? = null,
    val media: List<AnimeSearchItemDto> = emptyList(),
)

@Serializable
data class PageInfoDto(
    val currentPage: Int,
    val lastPage: Int? = null,
    val hasNextPage: Boolean,
    val perPage: Int,
    val total: Int? = null,
)

@Serializable
data class AnimeSearchItemDto(
    val id: Int,
    val idMal: Int? = null,
    val title: AnimeTitleDto,
    val coverImage: AnimeCoverImageDto? = null,
    val format: String? = null,
    val status: String? = null,
    val episodes: Int? = null,
    val season: String? = null,
    val seasonYear: Int? = null,
    val averageScore: Int? = null,
)

@Serializable
data class AnimeTitleDto(
    val romaji: String? = null,
    val english: String? = null,
    val native: String? = null,
)

@Serializable
data class AnimeCoverImageDto(
    val extraLarge: String? = null,
    val large: String? = null,
    val medium: String? = null,
    val color: String? = null,
)