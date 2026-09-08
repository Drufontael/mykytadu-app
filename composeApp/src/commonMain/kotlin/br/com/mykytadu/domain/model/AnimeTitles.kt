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
