package br.com.mykytadu.core.navigation

import br.com.mykytadu.domain.model.CatalogAnimeId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Navigation owns serialization; the domain ID remains independent of it. */
internal object CatalogAnimeIdNavSerializer : KSerializer<CatalogAnimeId> {
    override val descriptor = PrimitiveSerialDescriptor("CatalogAnimeId", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: CatalogAnimeId) = encoder.encodeString(value.value)

    override fun deserialize(decoder: Decoder): CatalogAnimeId {
        val value = decoder.decodeString()
        if (value.isBlank()) throw SerializationException("Catalog anime id must not be blank.")
        return CatalogAnimeId(value)
    }
}
