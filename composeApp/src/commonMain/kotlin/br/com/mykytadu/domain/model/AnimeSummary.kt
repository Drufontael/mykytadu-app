package br.com.mykytadu.domain.model

/** Dados de pesquisa, independentes do contrato de detalhes. */
data class AnimeSummary(
    val id: AniListAnimeId,
    val idMal: Int? = null,
    val titles: AnimeTitles = AnimeTitles(),
    val images: AnimeImages = AnimeImages(),
    val format: AnimeFormat? = null,
    val status: AnimeReleaseStatus? = null,
    val episodes: Int? = null,
    val season: AnimeSeason? = null,
    val seasonYear: Int? = null,
    val averageScore: Int? = null,
)
