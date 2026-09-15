package br.com.mykytadu.core.navigation

import br.com.mykytadu.domain.model.AniListAnimeId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Navigation owns serialization; the domain ID remains independent of it. */
internal object AniListAnimeIdNavSerializer : KSerializer<AniListAnimeId> {
    override val descriptor = PrimitiveSerialDescriptor("AniListAnimeId", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: AniListAnimeId) = encoder.encodeInt(value.value)

    override fun deserialize(decoder: Decoder): AniListAnimeId {
        val value = decoder.decodeInt()
        if (value <= 0) throw SerializationException("AniList anime id must be positive.")
        return AniListAnimeId(value)
    }
}
