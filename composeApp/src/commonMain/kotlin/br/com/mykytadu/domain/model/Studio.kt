package br.com.mykytadu.domain.model

data class Studio(
    val id: Int,
    val name: String,
    val isAnimationStudio: Boolean? = null,
)
