package br.com.mykytadu.data.local.migration

import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LibrarySchemaMigrationTest {

    @Test
    fun migrateSetsCurrentSchemaVersionForLegacyEntity() {
        val legacy = LibraryEntryEntity(
            id = "entry-1",
            animeId = "anime-1",
            status = "PLANNING",
            createdAtEpochMillis = 1000L,
            schemaVersion = 0,
        )

        val migrated = LibrarySchemaMigration.migrate(legacy)

        assertEquals(LibraryEntryEntity.CURRENT_SCHEMA_VERSION, migrated.schemaVersion)
        assertEquals(legacy.id, migrated.id)
        assertEquals(legacy.animeId, migrated.animeId)
    }

    @Test
    fun isValidReturnsTrueForCompliantEntity() {
        val valid = LibraryEntryEntity(
            id = "entry-1",
            animeId = "anime-1",
            status = "PLANNING",
            progressCurrent = 1,
            progressTotal = 12,
            rating = 80,
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 2000L,
        )

        assertTrue(LibrarySchemaMigration.isValid(valid))
    }

    @Test
    fun isValidReturnsFalseForInvalidFields() {
        val blankId = LibraryEntryEntity(
            id = "   ",
            animeId = "anime-1",
            status = "PLANNING",
            createdAtEpochMillis = 1000L,
        )
        assertFalse(LibrarySchemaMigration.isValid(blankId))

        val negativeProgress = LibraryEntryEntity(
            id = "entry-1",
            animeId = "anime-1",
            status = "PLANNING",
            progressCurrent = -1,
            createdAtEpochMillis = 1000L,
        )
        assertFalse(LibrarySchemaMigration.isValid(negativeProgress))

        val invalidRating = LibraryEntryEntity(
            id = "entry-1",
            animeId = "anime-1",
            status = "PLANNING",
            rating = 105,
            createdAtEpochMillis = 1000L,
        )
        assertFalse(LibrarySchemaMigration.isValid(invalidRating))

        val invalidTimestamps = LibraryEntryEntity(
            id = "entry-1",
            animeId = "anime-1",
            status = "PLANNING",
            createdAtEpochMillis = 2000L,
            updatedAtEpochMillis = 1000L,
        )
        assertFalse(LibrarySchemaMigration.isValid(invalidTimestamps))
    }
}
