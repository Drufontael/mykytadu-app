package br.com.mykytadu.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class AnimeIdentifiersTest {

    @Test
    fun `deve aceitar identificador nao vazio`() {
        assertEquals("catalog-key", CatalogAnimeId("catalog-key").value)
    }

    @Test
    fun `deve rejeitar identificador vazio`() {
        assertFailsWith<IllegalArgumentException> {
            CatalogAnimeId("")
        }
    }

    @Test
    fun `deve rejeitar identificador em branco`() {
        assertFailsWith<IllegalArgumentException> {
            CatalogAnimeId("   ")
        }
    }

    @Test
    fun `deve considerar identificadores com mesmo valor iguais`() {
        assertEquals(CatalogAnimeId("20"), CatalogAnimeId("20"))
    }

    @Test
    fun `deve diferenciar identificadores com valores diferentes`() {
        assertNotEquals(CatalogAnimeId("20"), CatalogAnimeId("21"))
    }
}
