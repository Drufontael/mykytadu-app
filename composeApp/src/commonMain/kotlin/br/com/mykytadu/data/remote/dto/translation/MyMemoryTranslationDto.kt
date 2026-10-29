package br.com.mykytadu.data.remote.dto.translation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyMemoryTranslationResponseDto(
    @SerialName("responseData")
    val responseData: MyMemoryResponseDataDto? = null,
    @SerialName("responseStatus")
    val responseStatus: Int? = null,
    @SerialName("responseDetails")
    val responseDetails: String? = null,
)

@Serializable
data class MyMemoryResponseDataDto(
    @SerialName("translatedText")
    val translatedText: String? = null,
    @SerialName("match")
    val match: Double? = null,
)
