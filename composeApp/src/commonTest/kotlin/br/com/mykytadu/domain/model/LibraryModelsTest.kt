package br.com.mykytadu.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LibraryModelsTest {

    @Test
    fun `LibraryEntryId aceita valor valido e rejeita vazio ou em branco`() {
        val id = LibraryEntryId("entry-1")
        assertEquals("entry-1", id.value)

        assertFailsWith<IllegalArgumentException> { LibraryEntryId("") }
        assertFailsWith<IllegalArgumentException> { LibraryEntryId("   ") }
    }

    @Test
    fun `LibraryEntryId e CatalogAnimeId possuem diferenciacao semantica`() {
        val entryId = LibraryEntryId("100")
        val animeId = CatalogAnimeId("100")
        // Tipos distintos não podem ser confundidos em tempo de compilação
        assertEquals("100", entryId.value)
        assertEquals("100", animeId.value)
    }

    @Test
    fun `LibraryStatus possui todos os status previstos no roadmap`() {
        val expected = listOf(
            LibraryStatus.PLANNING,
            LibraryStatus.WATCHING,
            LibraryStatus.PAUSED,
            LibraryStatus.COMPLETED,
            LibraryStatus.DROPPED,
        )
        assertEquals(expected, LibraryStatus.entries)
    }

    @Test
    fun `LibraryProgress aceita valores validos e rejeita negativos`() {
        val progress = LibraryProgress(current = 5, total = 12)
        assertEquals(5, progress.current)
        assertEquals(12, progress.total)

        assertFailsWith<IllegalArgumentException> {
            LibraryProgress(current = -1, total = 12)
        }
        assertFailsWith<IllegalArgumentException> {
            LibraryProgress(current = 0, total = -1)
        }
    }

    @Test
    fun `LibraryProgress trata explicitamente progresso que excede o total`() {
        val normal = LibraryProgress(current = 10, total = 12)
        assertFalse(normal.isOverTotal)
        assertEquals(10, normal.clampedCurrent)
        assertEquals(10f / 12f, normal.completionRatio)

        val exceeded = LibraryProgress(current = 15, total = 12)
        assertTrue(exceeded.isOverTotal)
        assertEquals(12, exceeded.clampedCurrent)
        assertEquals(1f, exceeded.completionRatio)

        val unknownTotal = LibraryProgress(current = 24, total = null)
        assertFalse(unknownTotal.isOverTotal)
        assertEquals(24, unknownTotal.clampedCurrent)
        assertNull(unknownTotal.completionRatio)
    }

    @Test
    fun `LibraryProgress incrementa e decrementa respeitando o piso zero`() {
        val initial = LibraryProgress(current = 0, total = 24)
        val incremented = initial.increment()
        assertEquals(1, incremented.current)

        val decremented = incremented.decrement()
        assertEquals(0, decremented.current)

        val atZero = decremented.decrement()
        assertEquals(0, atZero.current)
    }

    @Test
    fun `LibraryRating valida escala de 0 a 100`() {
        assertEquals(0, LibraryRating(0).value)
        assertEquals(85, LibraryRating(85).value)
        assertEquals(100, LibraryRating(100).value)

        assertFailsWith<IllegalArgumentException> { LibraryRating(-1) }
        assertFailsWith<IllegalArgumentException> { LibraryRating(101) }
    }

    @Test
    fun `LibraryEntry mantem integridade temporal e imutabilidade`() {
        val created = 1_000L
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("anime-20"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 1, total = 26),
            isFavorite = false,
            rating = LibraryRating(90),
            notes = "Excelente animação",
            createdAtEpochMillis = created,
            updatedAtEpochMillis = created,
        )

        assertEquals("lib-1", entry.id.value)
        assertEquals("anime-20", entry.animeId.value)
        assertEquals(LibraryStatus.WATCHING, entry.status)
        assertFalse(entry.isFavorite)

        // Atualização de status
        val updatedStatus = entry.withStatus(LibraryStatus.COMPLETED, updatedAt = 2_000L)
        assertEquals(LibraryStatus.COMPLETED, updatedStatus.status)
        assertEquals(2_000L, updatedStatus.updatedAtEpochMillis)
        assertEquals(created, updatedStatus.createdAtEpochMillis)

        // Alternar favorito
        val favorited = entry.withFavorite(true, updatedAt = 3_000L)
        assertTrue(favorited.isFavorite)

        // Atualização de notas (limpando em branco)
        val withBlankNotes = entry.withNotes("   ", updatedAt = 4_000L)
        assertNull(withBlankNotes.notes)
    }

    @Test
    fun `LibraryEntry rejeita updatedAt anterior a createdAt`() {
        assertFailsWith<IllegalArgumentException> {
            LibraryEntry(
                id = LibraryEntryId("lib-1"),
                animeId = CatalogAnimeId("anime-20"),
                status = LibraryStatus.PLANNING,
                createdAtEpochMillis = 2_000L,
                updatedAtEpochMillis = 1_000L,
            )
        }
    }

    @Test
    fun `LibraryEntry rejeita notas que excedem o tamanho maximo permitido`() {
        val longNotes = "a".repeat(LibraryEntry.MAX_NOTES_LENGTH + 1)
        assertFailsWith<IllegalArgumentException> {
            LibraryEntry(
                id = LibraryEntryId("lib-1"),
                animeId = CatalogAnimeId("anime-20"),
                status = LibraryStatus.PLANNING,
                notes = longNotes,
                createdAtEpochMillis = 1_000L,
            )
        }
    }
}
