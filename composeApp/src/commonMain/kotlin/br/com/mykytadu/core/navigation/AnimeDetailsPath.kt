package br.com.mykytadu.core.navigation

import br.com.mykytadu.domain.model.CatalogAnimeId

/**
 * Preserves the current public URL contract based on positive decimal IDs.
 * This boundary does not constrain the provider-independent representation of [CatalogAnimeId].
 */
internal object AnimeDetailsPath {
    fun resolve(path: String): AppRoute.AnimeDetails? {
        if (!path.startsWith("/anime/")) return null
        val text = path.removePrefix("/anime/")
        if (text.isEmpty() || text.any { it !in '0'..'9' }) return null
        val value = text.toIntOrNull()?.takeIf { it > 0 } ?: return null
        return AppRoute.AnimeDetails(CatalogAnimeId(value.toString()))
    }
}
