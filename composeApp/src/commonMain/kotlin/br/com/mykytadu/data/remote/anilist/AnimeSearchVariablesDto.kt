package br.com.mykytadu.data.remote.anilist.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnimeSearchVariablesDto(
    val search: String,
    val page: Int,
    val perPage: Int,
)