package br.com.mykytadu.data.mapper

import br.com.mykytadu.domain.model.CatalogAnimeId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class AniListAnimeIdMapperTest {

    @Test
    fun `deve converter id positivo da AniList para identidade opaca do catalogo`() {
        assertEquals(CatalogAnimeId("21579"), 21579.toCatalogAnimeId())
    }

    @Test
    fun `deve rejeitar id nao positivo recebido da AniList`() {
        listOf(0, -1, Int.MIN_VALUE).forEach { value ->
            assertFailsWith<IllegalArgumentException> {
                value.toCatalogAnimeId()
            }
        }
    }

    @Test
    fun `deve converter identidade compativel para id da AniList`() {
        assertEquals(21579, CatalogAnimeId("21579").toAniListAnimeIdOrNull())
        assertEquals(20, CatalogAnimeId("00020").toAniListAnimeIdOrNull())
        assertEquals(Int.MAX_VALUE, CatalogAnimeId(Int.MAX_VALUE.toString()).toAniListAnimeIdOrNull())
    }

    @Test
    fun `deve rejeitar identidade incompativel com id da AniList`() {
        listOf("catalog-key", "0", "-1", "2147483648", "+1", " 20 ").forEach { value ->
            assertNull(CatalogAnimeId(value).toAniListAnimeIdOrNull(), value)
        }
    }
}
