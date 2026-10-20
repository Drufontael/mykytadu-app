package br.com.mykytadu.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class LibraryRating(
    val value: Int,
) {
    init {
        require(value in 0..100) { "Rating value must be between 0 and 100." }
    }
}
