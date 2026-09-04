package br.com.mykytadu.data.remote.anilist.dto

import kotlinx.serialization.Serializable

@Serializable
data class AniListDateDto(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null,
)

@Serializable
data class AnimeStudioConnectionDto(
    val nodes: List<AnimeStudioDto> = emptyList(),
)

@Serializable
data class AnimeStudioDto(
    val id: Int,
    val name: String,
    val isAnimationStudio: Boolean? = null,
)

@Serializable
data class AnimeTrailerDto(
    val id: String? = null,
    val site: String? = null,
    val thumbnail: String? = null,
)

@Serializable
data class NextAiringEpisodeDto(
    val episode: Int? = null,
    val airingAt: Int? = null,
    val timeUntilAiring: Int? = null,
)

@Serializable
data class AnimeRelationsDto(
    val edges: List<AnimeRelationEdgeDto> = emptyList(),
)

@Serializable
data class AnimeRelationEdgeDto(
    val relationType: String? = null,
    val node: AnimeRelationNodeDto? = null,
)

@Serializable
data class AnimeRelationNodeDto(
    val id: Int,
    val type: String? = null,
    val format: String? = null,
    val status: String? = null,
    val title: AnimeTitleDto,
    val coverImage: AnimeCoverImageDto? = null,
)

@Serializable
data class AnimeExternalLinkDto(
    val id: Int? = null,
    val site: String? = null,
    val url: String? = null,
    val type: String? = null,
)