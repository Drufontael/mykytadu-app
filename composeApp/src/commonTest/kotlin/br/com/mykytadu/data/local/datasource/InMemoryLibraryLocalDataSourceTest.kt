package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InMemoryLibraryLocalDataSourceTest {

    private fun createEntity(
        id: String = "entry-1",
        animeId: String = "anime-1",
        status: String = "PLANNING",
        progressCurrent: Int = 0,
        progressTotal: Int? = 12,
        createdAt: Long = 1000L,
    ) = LibraryEntryEntity(
        id = id,
        animeId = animeId,
        status = status,
        progressCurrent = progressCurrent,
        progressTotal = progressTotal,
        createdAtEpochMillis = createdAt,
        updatedAtEpochMillis = createdAt,
    )

    @Test
    fun upsertAndGetEntryById() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val entry = createEntity(id = "e1", animeId = "a1")

        dataSource.upsertEntry(entry)

        val retrieved = dataSource.getEntryById("e1")
        assertNotNull(retrieved)
        assertEquals("a1", retrieved.animeId)
    }

    @Test
    fun getEntryByAnimeIdReturnsMatchingEntry() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val entry = createEntity(id = "e1", animeId = "a1")

        dataSource.upsertEntry(entry)

        val retrieved = dataSource.getEntryByAnimeId("a1")
        assertNotNull(retrieved)
        assertEquals("e1", retrieved.id)
    }

    @Test
    fun getEntriesByStatusFiltersCorrectly() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val e1 = createEntity(id = "e1", animeId = "a1", status = "WATCHING")
        val e2 = createEntity(id = "e2", animeId = "a2", status = "COMPLETED")
        val e3 = createEntity(id = "e3", animeId = "a3", status = "WATCHING")

        dataSource.upsertEntry(e1)
        dataSource.upsertEntry(e2)
        dataSource.upsertEntry(e3)

        val watching = dataSource.getEntriesByStatus("WATCHING")
        assertEquals(2, watching.size)
        assertTrue(watching.any { it.id == "e1" })
        assertTrue(watching.any { it.id == "e3" })

        val completed = dataSource.getEntriesByStatus("completed")
        assertEquals(1, completed.size)
        assertEquals("e2", completed.first().id)
    }

    @Test
    fun upsertEnforcesSingleEntryPerAnimeId() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val original = createEntity(id = "e1", animeId = "a1", status = "PLANNING")
        val replacement = createEntity(id = "e2", animeId = "a1", status = "WATCHING")

        dataSource.upsertEntry(original)
        assertEquals(1, dataSource.getAllEntries().size)
        assertEquals("e1", dataSource.getEntryByAnimeId("a1")?.id)

        // Inserir com mesmo animeId mas novo id deve remover a entrada anterior
        dataSource.upsertEntry(replacement)
        val all = dataSource.getAllEntries()
        assertEquals(1, all.size)
        assertNull(dataSource.getEntryById("e1"))
        assertEquals("e2", dataSource.getEntryByAnimeId("a1")?.id)
        assertEquals("WATCHING", dataSource.getEntryByAnimeId("a1")?.status)
    }

    @Test
    fun deleteEntryByIdRemovesItem() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val entry = createEntity(id = "e1", animeId = "a1")

        dataSource.upsertEntry(entry)
        val deleted = dataSource.deleteEntry("e1")
        assertTrue(deleted)

        assertNull(dataSource.getEntryById("e1"))
        assertFalse(dataSource.deleteEntry("non-existent"))
    }

    @Test
    fun deleteEntryByAnimeIdRemovesItem() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val entry = createEntity(id = "e1", animeId = "a1")

        dataSource.upsertEntry(entry)
        val deleted = dataSource.deleteEntryByAnimeId("a1")
        assertTrue(deleted)

        assertNull(dataSource.getEntryByAnimeId("a1"))
        assertFalse(dataSource.deleteEntryByAnimeId("non-existent"))
    }

    @Test
    fun clearAllRemovesAllEntries() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        dataSource.upsertEntry(createEntity(id = "e1", animeId = "a1"))
        dataSource.upsertEntry(createEntity(id = "e2", animeId = "a2"))

        assertEquals(2, dataSource.getAllEntries().size)
        dataSource.clearAll()
        assertEquals(0, dataSource.getAllEntries().size)
    }

    @Test
    fun observeFlowsEmitUpdatedData() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()

        val initialAll = dataSource.observeAllEntries().first()
        assertTrue(initialAll.isEmpty())

        dataSource.upsertEntry(createEntity(id = "e1", animeId = "a1", status = "WATCHING"))

        val observedAnime = dataSource.observeEntryByAnimeId("a1").first()
        assertNotNull(observedAnime)
        assertEquals("e1", observedAnime.id)

        val observedStatus = dataSource.observeEntriesByStatus("WATCHING").first()
        assertEquals(1, observedStatus.size)
        assertEquals("e1", observedStatus.first().id)
    }
}
