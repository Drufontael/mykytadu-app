package br.com.mykytadu.domain.model

/**
 * Referência não recursiva à obra relacionada, que também pode ser mangá.
 * Reutiliza o identificador AniList existente; mediaType distingue a natureza da obra.
 */
data class AnimeRelation(
    val id: AniListAnimeId,
    val relationType: AnimeRelationType? = null,
    val mediaType: MediaType? = null,
    val titles: AnimeTitles = AnimeTitles(),
    val format: AnimeFormat? = null,
    val status: AnimeReleaseStatus? = null,
    /** A consulta de relações disponibiliza somente a capa medium. */
    val coverMedium: String? = null,
)
