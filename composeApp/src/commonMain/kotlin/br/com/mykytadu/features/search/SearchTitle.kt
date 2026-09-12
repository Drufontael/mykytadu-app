package br.com.mykytadu.features.search

import br.com.mykytadu.domain.model.AnimeSummary

internal fun AnimeSummary.displayTitle(unavailableTitle: String): String =
    sequenceOf(
        titles.english,
        titles.romaji,
        titles.native,
    ).plus(titles.synonyms.asSequence())
        .mapNotNull { candidate -> candidate?.trim()?.takeIf(String::isNotEmpty) }
        .firstOrNull()
        ?: unavailableTitle
