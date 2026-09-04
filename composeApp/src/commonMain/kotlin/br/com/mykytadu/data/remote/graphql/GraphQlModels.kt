package br.com.mykytadu.data.remote.graphql

import kotlinx.serialization.Serializable

@Serializable
data class GraphQlRequest<T>(
    val query: String,
    val variables: T,
)

@Serializable
data class GraphQlResponse<T>(
    val data: T? = null,
    val errors: List<GraphQlErrorDto>? = null,
)

@Serializable
data class GraphQlErrorDto(
    val message: String,
)