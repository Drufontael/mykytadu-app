package br.com.mykytadu.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class CatalogAnimeId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Catalog anime id must not be blank." }
    }
}
