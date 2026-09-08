package br.com.mykytadu.domain.model

/** Detalhes do catálogo. As coleções mantêm snapshots das listas recebidas. */
class AnimeDetails(
    val id: AniListAnimeId,
    val idMal: Int? = null,
    val titles: AnimeTitles = AnimeTitles(),
    val images: AnimeImages = AnimeImages(),
    val description: String? = null,
    val format: AnimeFormat? = null,
    val status: AnimeReleaseStatus? = null,
    val episodes: Int? = null,
    /** Duração de um episódio, em minutos, quando informada. */
    val duration: Int? = null,
    val season: AnimeSeason? = null,
    val seasonYear: Int? = null,
    val isAdult: Boolean? = null,
    val startDate: PartialDate? = null,
    val endDate: PartialDate? = null,
    genres: List<String> = emptyList(),
    val averageScore: Int? = null,
    studios: List<Studio> = emptyList(),
    val trailer: Trailer? = null,
    relations: List<AnimeRelation> = emptyList(),
) {
    val genres: List<String> = genres.toList()
    val studios: List<Studio> = studios.toList()
    val relations: List<AnimeRelation> = relations.toList()
}
