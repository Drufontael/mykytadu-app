package br.com.mykytadu.features.search

import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.TitleLanguagePreference
import br.com.mykytadu.domain.model.resolveDisplayTitle

internal fun AnimeSummary.displayTitle(
    unavailableTitle: String,
    preference: TitleLanguagePreference = TitleLanguagePreference.ENGLISH,
): String = titles.resolveDisplayTitle(preference, unavailableTitle)
