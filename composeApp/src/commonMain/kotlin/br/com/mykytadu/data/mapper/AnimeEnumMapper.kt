package br.com.mykytadu.data.mapper

import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.domain.model.AnimeReleaseStatus
import br.com.mykytadu.domain.model.AnimeSeason
import br.com.mykytadu.domain.model.MediaType
import br.com.mykytadu.domain.model.AnimeRelationType

// Correspondência exata: ausência permanece null; qualquer valor não reconhecido vira UNKNOWN.

internal fun String?.toAnimeFormat(): AnimeFormat? = when (this) {
    null -> null
    "TV" -> AnimeFormat.TV
    "TV_SHORT" -> AnimeFormat.TV_SHORT
    "MOVIE" -> AnimeFormat.MOVIE
    "SPECIAL" -> AnimeFormat.SPECIAL
    "OVA" -> AnimeFormat.OVA
    "ONA" -> AnimeFormat.ONA
    "MUSIC" -> AnimeFormat.MUSIC
    "MANGA" -> AnimeFormat.MANGA
    "NOVEL" -> AnimeFormat.NOVEL
    "ONE_SHOT" -> AnimeFormat.ONE_SHOT
    else -> AnimeFormat.UNKNOWN
}

internal fun String?.toAnimeReleaseStatus(): AnimeReleaseStatus? = when (this) {
    null -> null
    "FINISHED" -> AnimeReleaseStatus.FINISHED
    "RELEASING" -> AnimeReleaseStatus.RELEASING
    "NOT_YET_RELEASED" -> AnimeReleaseStatus.NOT_YET_RELEASED
    "CANCELLED" -> AnimeReleaseStatus.CANCELLED
    "HIATUS" -> AnimeReleaseStatus.HIATUS
    else -> AnimeReleaseStatus.UNKNOWN
}

internal fun String?.toAnimeSeason(): AnimeSeason? = when (this) {
    null -> null
    "WINTER" -> AnimeSeason.WINTER
    "SPRING" -> AnimeSeason.SPRING
    "SUMMER" -> AnimeSeason.SUMMER
    "FALL" -> AnimeSeason.FALL
    else -> AnimeSeason.UNKNOWN
}

internal fun String?.toMediaType(): MediaType? = when (this) {
    null -> null
    "ANIME" -> MediaType.ANIME
    "MANGA" -> MediaType.MANGA
    else -> MediaType.UNKNOWN
}

internal fun String?.toAnimeRelationType(): AnimeRelationType? = when (this) {
    null -> null
    "ADAPTATION" -> AnimeRelationType.ADAPTATION
    "PREQUEL" -> AnimeRelationType.PREQUEL
    "SEQUEL" -> AnimeRelationType.SEQUEL
    "PARENT" -> AnimeRelationType.PARENT
    "SIDE_STORY" -> AnimeRelationType.SIDE_STORY
    "CHARACTER" -> AnimeRelationType.CHARACTER
    "SUMMARY" -> AnimeRelationType.SUMMARY
    "ALTERNATIVE" -> AnimeRelationType.ALTERNATIVE
    "SPIN_OFF" -> AnimeRelationType.SPIN_OFF
    "OTHER" -> AnimeRelationType.OTHER
    "SOURCE" -> AnimeRelationType.SOURCE
    "COMPILATION" -> AnimeRelationType.COMPILATION
    "CONTAINS" -> AnimeRelationType.CONTAINS
    else -> AnimeRelationType.UNKNOWN
}
