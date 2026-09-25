package br.com.mykytadu.features.anime

import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeRelation
import br.com.mykytadu.domain.model.PartialDate
import br.com.mykytadu.domain.model.Trailer

internal fun AnimeDetails.displayTitle(unavailableTitle: String): String =
    sequenceOf(titles.english, titles.romaji, titles.native)
        .plus(titles.synonyms.asSequence())
        .mapNotNull { title -> title?.trim()?.takeIf(String::isNotEmpty) }
        .firstOrNull()
        ?: unavailableTitle

internal fun AnimeDetails.coverUrl(): String? =
    sequenceOf(images.coverExtraLarge, images.coverLarge)
        .mapNotNull { url -> url?.trim()?.takeIf(String::isNotEmpty) }
        .firstOrNull()

internal fun AnimeDetails.bannerUrl(): String? =
    images.banner?.trim()?.takeIf(String::isNotEmpty)

internal fun AnimeDetails.displayDescription(): String? =
    description
        ?.replace(BREAK_TAG, "\n")
        ?.replace(PARAGRAPH_END_TAG, "\n\n")
        ?.replace(HTML_TAG, "")
        ?.replace("&nbsp;", " ")
        ?.replace("&amp;", "&")
        ?.replace("&lt;", "<")
        ?.replace("&gt;", ">")
        ?.replace("&quot;", "\"")
        ?.replace("&#39;", "'")
        ?.lines()
        ?.joinToString("\n") { line -> line.trim() }
        ?.replace(EXCESS_BLANK_LINES, "\n\n")
        ?.trim()
        ?.takeIf(String::isNotEmpty)

internal fun AnimeDetails.displayGenres(): List<String> =
    genres.mapNotNull { it.trim().takeIf(String::isNotEmpty) }.distinct()

internal fun AnimeDetails.displayStudios(): List<String> =
    studios.mapNotNull { it.name.trim().takeIf(String::isNotEmpty) }.distinct()

internal fun PartialDate.displayValue(): String? {
    val parts = listOfNotNull(
        day?.toString()?.padStart(2, '0'),
        month?.toString()?.padStart(2, '0'),
        year?.toString(),
    )
    return parts.takeIf(List<String>::isNotEmpty)?.joinToString("/")
}

internal fun Trailer.externalUrl(): String? {
    val videoId = id.trim().takeIf { TRAILER_ID.matches(it) } ?: return null
    return when (site.trim().lowercase()) {
        "youtube" -> "https://www.youtube.com/watch?v=$videoId"
        "dailymotion" -> "https://www.dailymotion.com/video/$videoId"
        else -> null
    }
}

internal fun AnimeRelation.displayTitle(unavailableTitle: String): String =
    sequenceOf(titles.english, titles.romaji, titles.native)
        .plus(titles.synonyms.asSequence())
        .mapNotNull { title -> title?.trim()?.takeIf(String::isNotEmpty) }
        .firstOrNull()
        ?: unavailableTitle

internal fun AnimeRelation.coverUrl(): String? = coverMedium?.trim()?.takeIf(String::isNotEmpty)

private val BREAK_TAG = Regex("<br\\s*/?>", RegexOption.IGNORE_CASE)
private val PARAGRAPH_END_TAG = Regex("</p\\s*>", RegexOption.IGNORE_CASE)
private val HTML_TAG = Regex("<[^>]+>")
private val EXCESS_BLANK_LINES = Regex("\\n{3,}")
private val TRAILER_ID = Regex("[A-Za-z0-9_-]+")
