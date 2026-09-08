package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.remote.anilist.dto.AniListDateDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeCoverImageDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeStudioConnectionDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeStudioDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeTitleDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeTrailerDto
import br.com.mykytadu.domain.model.AnimeImages
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.PartialDate
import br.com.mykytadu.domain.model.Studio
import br.com.mykytadu.domain.model.Trailer

/** Sinônimos são fornecidos separadamente do DTO de título na operação de detalhes. */
internal fun AnimeTitleDto.toAnimeTitles(
    synonyms: List<String> = emptyList(),
): AnimeTitles = AnimeTitles(
    romaji = romaji.trimmedOrNull(),
    english = english.trimmedOrNull(),
    native = native.trimmedOrNull(),
    synonyms = synonyms.mapNotNull { it.trimmedOrNull() },
)

/** O banner é independente da capa e pode existir mesmo quando ela estiver ausente. */
internal fun AnimeCoverImageDto?.toAnimeImages(
    banner: String? = null,
): AnimeImages = AnimeImages(
    coverLarge = this?.large.trimmedOrNull(),
    coverExtraLarge = this?.extraLarge.trimmedOrNull(),
    banner = banner.trimmedOrNull(),
    color = this?.color.trimmedOrNull(),
)

/** Descarta somente esta data se vazia ou fora dos intervalos aceitos pelo domínio. */
internal fun AniListDateDto?.toPartialDate(): PartialDate? {
    if (this == null || (year == null && month == null && day == null)) return null
    if (month != null && month !in 1..12) return null
    if (day != null && day !in 1..31) return null
    return PartialDate(year = year, month = month, day = day)
}

internal fun AnimeStudioDto.toStudio(): Studio? {
    if (id <= 0) return null
    val studioName = name.trimmedOrNull() ?: return null
    return Studio(id = id, name = studioName, isAnimationStudio = isAnimationStudio)
}

/** Descarta nós inválidos individualmente, preservando ordem e duplicatas. */
internal fun AnimeStudioConnectionDto?.toStudios(): List<Studio> =
    this?.nodes.orEmpty().mapNotNull { it.toStudio() }

internal fun AnimeTrailerDto?.toTrailer(): Trailer? {
    if (this == null) return null
    val trailerId = id.trimmedOrNull() ?: return null
    val trailerSite = site.trimmedOrNull() ?: return null
    return Trailer(
        id = trailerId,
        site = trailerSite,
        thumbnail = thumbnail.trimmedOrNull(),
    )
}

private fun String?.trimmedOrNull(): String? = this?.trim()?.takeIf { it.isNotEmpty() }
