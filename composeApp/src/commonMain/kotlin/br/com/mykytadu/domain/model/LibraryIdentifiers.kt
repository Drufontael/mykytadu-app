package br.com.mykytadu.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class LibraryEntryId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Library entry id must not be blank." }
    }
}
