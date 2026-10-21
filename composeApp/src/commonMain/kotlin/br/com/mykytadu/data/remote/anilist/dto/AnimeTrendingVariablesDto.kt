package br.com.mykytadu.data.remote.anilist.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnimeTrendingVariablesDto(
    val page: Int,
    val perPage: Int,
)
