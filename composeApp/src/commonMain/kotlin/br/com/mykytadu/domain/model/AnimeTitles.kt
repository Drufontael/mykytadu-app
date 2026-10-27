package br.com.mykytadu.domain.model

/** Preserva os títulos sem selecionar idioma ou fornecer texto de apresentação. */
class AnimeTitles(
    val romaji: String? = null,
    val english: String? = null,
    val native: String? = null,
    synonyms: List<String> = emptyList(),
) {
    /** Snapshot dos sinônimos, preservando ordem e conteúdo. */
    val synonyms: List<String> = synonyms.toList()
}

/**
 * Resolve o título de apresentação do anime priorizando a preferência de idioma informada
 * e aplicando fallbacks seguros entre os títulos disponíveis e sinônimos.
 */
fun AnimeTitles.resolveDisplayTitle(
    preference: TitleLanguagePreference = TitleLanguagePreference.ENGLISH,
    unavailableTitle: String,
): String {
    val candidates: Sequence<String?> = when (preference) {
        TitleLanguagePreference.ROMAJI -> sequenceOf(romaji, english, native)
        TitleLanguagePreference.ENGLISH -> sequenceOf(english, romaji, native)
        TitleLanguagePreference.NATIVE -> sequenceOf(native, romaji, english)
    }

    return candidates
        .plus(synonyms.asSequence())
        .mapNotNull { candidate -> candidate?.trim()?.takeIf(String::isNotEmpty) }
        .firstOrNull()
        ?: unavailableTitle
}
