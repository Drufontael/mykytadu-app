package br.com.mykytadu.data.mapper

import br.com.mykytadu.domain.model.CatalogAnimeId

internal fun Int.toCatalogAnimeId(): CatalogAnimeId {
    require(this > 0) { "AniList anime id must be positive." }
    return CatalogAnimeId(toString())
}

internal fun CatalogAnimeId.toAniListAnimeIdOrNull(): Int? =
    value
        .takeIf { text -> text.all { it in '0'..'9' } }
        ?.toIntOrNull()
        ?.takeIf { it > 0 }
