package br.com.mykytadu.domain.model

/** Endereços e cor opcionais, sem validação ou transformação de conteúdo. */
data class AnimeImages(
    val coverLarge: String? = null,
    val coverExtraLarge: String? = null,
    val banner: String? = null,
    val color: String? = null,
)
