package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LibraryEntryMapperTest {

    @Test
    fun toEntityMapsAllFieldsAccurately() {
        val domain = LibraryEntry(
            id = LibraryEntryId("entry-123"),
            animeId = CatalogAnimeId("anime-456"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 12, total = 24),
            isFavorite = true,
            rating = LibraryRating(85),
            notes = "Ótimo anime até o momento.",
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 2000L,
        )

        val entity = domain.toEntity()

        assertEquals("entry-123", entity.id)
        assertEquals("anime-456", entity.animeId)
        assertEquals("WATCHING", entity.status)
        assertEquals(12, entity.progressCurrent)
        assertEquals(24, entity.progressTotal)
        assertTrue(entity.isFavorite)
        assertEquals(85, entity.rating)
        assertEquals("Ótimo anime até o momento.", entity.notes)
        assertEquals(1000L, entity.createdAtEpochMillis)
        assertEquals(2000L, entity.updatedAtEpochMillis)
        assertEquals(LibraryEntryEntity.CURRENT_SCHEMA_VERSION, entity.schemaVersion)
    }

    @Test
    fun toDomainMapsValidEntityAccurately() {
        val entity = LibraryEntryEntity(
            id = "entry-123",
            animeId = "anime-456",
            status = "COMPLETED",
            progressCurrent = 24,
            progressTotal = 24,
            isFavorite = false,
            rating = 90,
            notes = "Final excelente.",
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 3000L,
            schemaVersion = 1,
        )

        val domain = entity.toDomain()

        assertEquals(LibraryEntryId("entry-123"), domain.id)
        assertEquals(CatalogAnimeId("anime-456"), domain.animeId)
        assertEquals(LibraryStatus.COMPLETED, domain.status)
        assertEquals(24, domain.progress.current)
        assertEquals(24, domain.progress.total)
        assertFalse(domain.isFavorite)
        assertEquals(90, domain.rating?.value)
        assertEquals("Final excelente.", domain.notes)
        assertEquals(1000L, domain.createdAtEpochMillis)
        assertEquals(3000L, domain.updatedAtEpochMillis)
    }

    @Test
    fun toDomainSanitizesCorruptedOrUnknownStatusToPlanning() {
        val entity = LibraryEntryEntity(
            id = "entry-1",
            animeId = "anime-1",
            status = "INVALID_STATUS_FROM_STORAGE",
            createdAtEpochMillis = 1000L,
        )

        val domain = entity.toDomain()
        assertEquals(LibraryStatus.PLANNING, domain.status)
    }

    @Test
    fun toDomainSanitizesNegativeProgressValues() {
        val entity = LibraryEntryEntity(
            id = "entry-1",
            animeId = "anime-1",
            status = "WATCHING",
            progressCurrent = -10,
            progressTotal = -5,
            createdAtEpochMillis = 1000L,
        )

        val domain = entity.toDomain()
        assertEquals(0, domain.progress.current)
        assertEquals(0, domain.progress.total)
    }

    @Test
    fun toDomainSanitizesOutOfRangeRating() {
        val negativeEntity = LibraryEntryEntity(
            id = "entry-1",
            animeId = "anime-1",
            status = "WATCHING",
            rating = -5,
            createdAtEpochMillis = 1000L,
        )
        assertNull(negativeEntity.toDomain().rating)

        val excessiveEntity = LibraryEntryEntity(
            id = "entry-2",
            animeId = "anime-2",
            status = "WATCHING",
            rating = 150,
            createdAtEpochMillis = 1000L,
        )
        assertNull(excessiveEntity.toDomain().rating)
    }

    @Test
    fun toDomainSanitizesNotes() {
        val blankEntity = LibraryEntryEntity(
            id = "entry-1",
            animeId = "anime-1",
            status = "WATCHING",
            notes = "    ",
            createdAtEpochMillis = 1000L,
        )
        assertNull(blankEntity.toDomain().notes)

        val longText = "a".repeat(LibraryEntry.MAX_NOTES_LENGTH + 100)
        val excessiveEntity = LibraryEntryEntity(
            id = "entry-2",
            animeId = "anime-2",
            status = "WATCHING",
            notes = longText,
            createdAtEpochMillis = 1000L,
        )
        assertEquals(LibraryEntry.MAX_NOTES_LENGTH, excessiveEntity.toDomain().notes?.length)
    }

    @Test
    fun toDomainEnsuresUpdatedAtNeverPrecedesCreatedAt() {
        val entity = LibraryEntryEntity(
            id = "entry-1",
            animeId = "anime-1",
            status = "WATCHING",
            createdAtEpochMillis = 5000L,
            updatedAtEpochMillis = 2000L, // Menor que createdAt
        )

        val domain = entity.toDomain()
        assertEquals(5000L, domain.createdAtEpochMillis)
        assertEquals(5000L, domain.updatedAtEpochMillis)
    }

    @Test
    fun roundtripPreservesDomainEquivalence() {
        val original = LibraryEntry(
            id = LibraryEntryId("lib-77"),
            animeId = CatalogAnimeId("cat-88"),
            status = LibraryStatus.PAUSED,
            progress = LibraryProgress(current = 5, total = 12),
            isFavorite = true,
            rating = LibraryRating(75),
            notes = "Pausado no arco do exame.",
            createdAtEpochMillis = 123456789L,
            updatedAtEpochMillis = 987654321L,
        )

        val roundtrip = original.toEntity().toDomain()

        assertEquals(original, roundtrip)
    }
}
