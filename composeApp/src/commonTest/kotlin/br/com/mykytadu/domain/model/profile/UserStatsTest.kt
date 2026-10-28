package br.com.mykytadu.domain.model.profile

import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserStatsTest {

    @Test
    fun `fromEntries com lista vazia retorna UserStats zerado`() {
        val stats = UserStats.fromEntries(emptyList())

        assertEquals(0, stats.totalAnimeCount)
        assertEquals(0, stats.watchingCount)
        assertEquals(0, stats.completedCount)
        assertEquals(0, stats.planningCount)
        assertEquals(0, stats.pausedCount)
        assertEquals(0, stats.droppedCount)
        assertEquals(0, stats.favoriteCount)
        assertEquals(0, stats.totalEpisodesWatched)
        assertEquals(0.0, stats.estimatedHoursWatched)
        assertNull(stats.averageScore)
    }

    @Test
    fun `fromEntries com entradas calcula agregacoes corretamente`() {
        val entries = listOf(
            LibraryEntry(
                id = LibraryEntryId("1"),
                animeId = CatalogAnimeId("101"),
                status = LibraryStatus.WATCHING,
                progress = LibraryProgress(current = 6, total = 12),
                isFavorite = true,
                rating = LibraryRating(90),
                createdAtEpochMillis = 1000L,
            ),
            LibraryEntry(
                id = LibraryEntryId("2"),
                animeId = CatalogAnimeId("102"),
                status = LibraryStatus.COMPLETED,
                progress = LibraryProgress(current = 24, total = 24),
                isFavorite = true,
                rating = LibraryRating(100),
                createdAtEpochMillis = 1000L,
            ),
            LibraryEntry(
                id = LibraryEntryId("3"),
                animeId = CatalogAnimeId("103"),
                status = LibraryStatus.PLANNING,
                progress = LibraryProgress(current = 0, total = 12),
                isFavorite = false,
                rating = null,
                createdAtEpochMillis = 1000L,
            ),
            LibraryEntry(
                id = LibraryEntryId("4"),
                animeId = CatalogAnimeId("104"),
                status = LibraryStatus.PAUSED,
                progress = LibraryProgress(current = 4, total = 12),
                isFavorite = false,
                rating = LibraryRating(80),
                createdAtEpochMillis = 1000L,
            ),
            LibraryEntry(
                id = LibraryEntryId("5"),
                animeId = CatalogAnimeId("105"),
                status = LibraryStatus.DROPPED,
                progress = LibraryProgress(current = 1, total = 12),
                isFavorite = false,
                rating = null,
                createdAtEpochMillis = 1000L,
            ),
        )

        val stats = UserStats.fromEntries(entries)

        assertEquals(5, stats.totalAnimeCount)
        assertEquals(1, stats.watchingCount)
        assertEquals(1, stats.completedCount)
        assertEquals(1, stats.planningCount)
        assertEquals(1, stats.pausedCount)
        assertEquals(1, stats.droppedCount)
        assertEquals(2, stats.favoriteCount)
        // Episódios: 6 + 24 + 0 + 4 + 1 = 35
        assertEquals(35, stats.totalEpisodesWatched)
        // Horas: (35 * 24.0) / 60.0 = 14.0 horas
        assertEquals(14.0, stats.estimatedHoursWatched)
        // Média notas: (90 + 100 + 80) / 3 = 90.0
        assertEquals(90.0, stats.averageScore)
    }
}
