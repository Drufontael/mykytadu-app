package br.com.mykytadu.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class AnimeIdentifiersTest {

    @Test
    fun `deve aceitar identificador positivo`() {
        assertEquals(1, AniListAnimeId(1).value)
    }

    @Test
    fun `deve rejeitar identificador igual a zero`() {
        assertFailsWith<IllegalArgumentException> {
            AniListAnimeId(0)
        }
    }

    @Test
    fun `deve rejeitar identificador negativo`() {
        assertFailsWith<IllegalArgumentException> {
            AniListAnimeId(-1)
        }
    }

    @Test
    fun `deve considerar identificadores com mesmo valor iguais`() {
        assertEquals(AniListAnimeId(20), AniListAnimeId(20))
    }

    @Test
    fun `deve diferenciar identificadores com valores diferentes`() {
        assertNotEquals(AniListAnimeId(20), AniListAnimeId(21))
    }
}
