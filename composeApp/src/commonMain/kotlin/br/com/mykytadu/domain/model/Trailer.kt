package br.com.mykytadu.domain.model

/** A validação das strings remotas pertence ao mapper. O site não é predefinido. */
data class Trailer(
    val id: String,
    val site: String,
    val thumbnail: String? = null,
)
