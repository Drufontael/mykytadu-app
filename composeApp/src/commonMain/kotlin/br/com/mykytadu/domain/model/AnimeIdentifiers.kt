package br.com.mykytadu.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class AniListAnimeId(
    val value: Int,
) {
    init {
        require(value > 0) { "AniList anime id must be positive." }
    }
}
