package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.remote.anilist.dto.AnimeRelationEdgeDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeRelationsDto
import br.com.mykytadu.domain.model.AnimeRelation
import br.com.mykytadu.domain.model.AniListAnimeId

/**
 * Descarta somente relações estruturalmente inválidas,
 * preservando a ordem das relações válidas.
 */
internal fun AnimeRelationsDto?.toAnimeRelations(): List<AnimeRelation> =
    this?.edges.orEmpty().mapNotNull { it.toAnimeRelation() }

/**
 * Uma relação sem nó ou com ID AniList inválido não pode ser
 * representada no domínio e é descartada individualmente.
 */
internal fun AnimeRelationEdgeDto.toAnimeRelation(): AnimeRelation? {
    val relatedAnime = node ?: return null
    if (relatedAnime.id <= 0) return null

    return AnimeRelation(
        id = AniListAnimeId(relatedAnime.id),
        relationType = relationType.toAnimeRelationType(),
        mediaType = relatedAnime.type.toMediaType(),
        titles = relatedAnime.title.toAnimeTitles(),
        format = relatedAnime.format.toAnimeFormat(),
        status = relatedAnime.status.toAnimeReleaseStatus(),
        coverMedium = relatedAnime.coverImage
            ?.medium
            .trimmedRelationValueOrNull(),
    )
}

private fun String?.trimmedRelationValueOrNull(): String? =
    this?.trim()?.takeIf { it.isNotEmpty() }