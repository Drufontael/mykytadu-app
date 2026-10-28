package br.com.mykytadu.data.remote.dto.problem

import kotlinx.serialization.Serializable

/**
 * Representação padronizada de RFC 9457 / RFC 7807 (Problem Details for HTTP APIs)
 * emitida pela API do MykytaDu em qualquer resposta 4xx/5xx.
 */
@Serializable
data class ProblemDetailsDto(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val code: String? = null,
    val detail: String? = null,
    val instance: String? = null,
    val traceId: String? = null,
    val errors: List<ProblemErrorDto> = emptyList(),
)

@Serializable
data class ProblemErrorDto(
    val field: String? = null,
    val code: String? = null,
    val message: String? = null,
)
