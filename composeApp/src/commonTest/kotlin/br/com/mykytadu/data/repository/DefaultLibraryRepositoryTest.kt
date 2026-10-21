package br.com.mykytadu.data.repository

import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultLibraryRepositoryTest {

    private fun createEntry(
        id: String = "entry-1",
        animeId: String = "anime-1",
        status: LibraryStatus = LibraryStatus.PLANNING,
        progressCurrent: Int = 0,
        progressTotal: Int? = 12,
        rating: Int? = null,
        createdAt: Long = 1000L,
    ) = LibraryEntry(
        id = LibraryEntryId(id),
        animeId = CatalogAnimeId(animeId),
        status = status,
        progress = LibraryProgress(current = progressCurrent, total = progressTotal),
        rating = rating?.let { LibraryRating(it) },
        createdAtEpochMillis = createdAt,
        updatedAtEpochMillis = createdAt,
    )

    @Test
    fun saveEntryAndGetByIdReturnsSuccess() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val repository = DefaultLibraryRepository(dataSource)
        val entry = createEntry(id = "e1", animeId = "a1")

        val saveResult = repository.saveEntry(entry)
        assertIs<RepositoryResult.Success<Unit>>(saveResult)

        val getResult = repository.getEntryById(LibraryEntryId("e1"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(getResult)
        assertEquals(entry, getResult.value)
    }

    @Test
    fun getEntryByAnimeIdReturnsMatchingEntry() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val repository = DefaultLibraryRepository(dataSource)
        val entry = createEntry(id = "e1", animeId = "a1")

        repository.saveEntry(entry)

        val getResult = repository.getEntryByAnimeId(CatalogAnimeId("a1"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(getResult)
        assertEquals(LibraryEntryId("e1"), getResult.value?.id)

        val notFound = repository.getEntryByAnimeId(CatalogAnimeId("non-existent"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(notFound)
        assertNull(notFound.value)
    }

    @Test
    fun getAllEntriesAndFilterByStatus() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val repository = DefaultLibraryRepository(dataSource)

        val e1 = createEntry(id = "e1", animeId = "a1", status = LibraryStatus.WATCHING)
        val e2 = createEntry(id = "e2", animeId = "a2", status = LibraryStatus.COMPLETED)
        val e3 = createEntry(id = "e3", animeId = "a3", status = LibraryStatus.WATCHING)

        repository.saveEntry(e1)
        repository.saveEntry(e2)
        repository.saveEntry(e3)

        val allResult = repository.getAllEntries()
        assertIs<RepositoryResult.Success<List<LibraryEntry>>>(allResult)
        assertEquals(3, allResult.value.size)

        val watchingResult = repository.getEntriesByStatus(LibraryStatus.WATCHING)
        assertIs<RepositoryResult.Success<List<LibraryEntry>>>(watchingResult)
        assertEquals(2, watchingResult.value.size)
        assertTrue(watchingResult.value.any { it.id == LibraryEntryId("e1") })
        assertTrue(watchingResult.value.any { it.id == LibraryEntryId("e3") })

        val pausedResult = repository.getEntriesByStatus(LibraryStatus.PAUSED)
        assertIs<RepositoryResult.Success<List<LibraryEntry>>>(pausedResult)
        assertTrue(pausedResult.value.isEmpty())
    }

    @Test
    fun deleteEntryByIdAndByAnimeId() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val repository = DefaultLibraryRepository(dataSource)

        val e1 = createEntry(id = "e1", animeId = "a1")
        val e2 = createEntry(id = "e2", animeId = "a2")
        repository.saveEntry(e1)
        repository.saveEntry(e2)

        val deleteByIdResult = repository.deleteEntry(LibraryEntryId("e1"))
        assertIs<RepositoryResult.Success<Boolean>>(deleteByIdResult)
        assertTrue(deleteByIdResult.value)

        val getAfterDelete = repository.getEntryById(LibraryEntryId("e1"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(getAfterDelete)
        assertNull(getAfterDelete.value)

        val deleteByAnimeResult = repository.deleteEntryByAnimeId(CatalogAnimeId("a2"))
        assertIs<RepositoryResult.Success<Boolean>>(deleteByAnimeResult)
        assertTrue(deleteByAnimeResult.value)

        val getAfterDeleteAnime = repository.getEntryByAnimeId(CatalogAnimeId("a2"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(getAfterDeleteAnime)
        assertNull(getAfterDeleteAnime.value)

        val deleteNonExistent = repository.deleteEntry(LibraryEntryId("e1"))
        assertIs<RepositoryResult.Success<Boolean>>(deleteNonExistent)
        assertFalse(deleteNonExistent.value)
    }

    @Test
    fun observeAllEntriesEmitsReactivity() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val repository = DefaultLibraryRepository(dataSource)

        val initial = repository.observeAllEntries().first()
        assertTrue(initial.isEmpty())

        repository.saveEntry(createEntry(id = "e1", animeId = "a1"))
        val afterSave = repository.observeAllEntries().first()
        assertEquals(1, afterSave.size)
        assertEquals(LibraryEntryId("e1"), afterSave.first().id)
    }

    @Test
    fun observeEntryByAnimeIdEmitsSpecificEntry() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val repository = DefaultLibraryRepository(dataSource)

        val initial = repository.observeEntryByAnimeId(CatalogAnimeId("a1")).first()
        assertNull(initial)

        repository.saveEntry(createEntry(id = "e1", animeId = "a1", status = LibraryStatus.WATCHING))
        val observed = repository.observeEntryByAnimeId(CatalogAnimeId("a1")).first()
        assertNotNull(observed)
        assertEquals(LibraryStatus.WATCHING, observed.status)
    }

    @Test
    fun observeEntriesByStatusEmitsFilteredList() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val repository = DefaultLibraryRepository(dataSource)

        val initial = repository.observeEntriesByStatus(LibraryStatus.COMPLETED).first()
        assertTrue(initial.isEmpty())

        repository.saveEntry(createEntry(id = "e1", animeId = "a1", status = LibraryStatus.COMPLETED))
        repository.saveEntry(createEntry(id = "e2", animeId = "a2", status = LibraryStatus.PLANNING))

        val observed = repository.observeEntriesByStatus(LibraryStatus.COMPLETED).first()
        assertEquals(1, observed.size)
        assertEquals(LibraryEntryId("e1"), observed.first().id)
    }

    @Test
    fun repositoryHandlesDataSourceExceptionsGracefully() = runTest {
        val failingDataSource = object : LibraryLocalDataSource {
            override suspend fun getEntryById(id: String): LibraryEntryEntity? =
                throw IllegalStateException("Disk read failure")

            override suspend fun getEntryByAnimeId(animeId: String): LibraryEntryEntity? =
                throw IllegalStateException("Disk read failure")

            override suspend fun getAllEntries(): List<LibraryEntryEntity> =
                throw IllegalStateException("Disk read failure")

            override suspend fun getEntriesByStatus(status: String): List<LibraryEntryEntity> =
                throw IllegalStateException("Disk read failure")

            override suspend fun upsertEntry(entry: LibraryEntryEntity) {
                throw IllegalStateException("Disk write failure")
            }

            override suspend fun deleteEntry(id: String): Boolean =
                throw IllegalStateException("Disk write failure")

            override suspend fun deleteEntryByAnimeId(animeId: String): Boolean =
                throw IllegalStateException("Disk write failure")

            override suspend fun clearAll() {}

            override fun observeAllEntries(): Flow<List<LibraryEntryEntity>> = emptyFlow()
            override fun observeEntryByAnimeId(animeId: String): Flow<LibraryEntryEntity?> = emptyFlow()
            override fun observeEntriesByStatus(status: String): Flow<List<LibraryEntryEntity>> = emptyFlow()
        }

        val repository = DefaultLibraryRepository(failingDataSource)

        val saveResult = repository.saveEntry(createEntry())
        assertIs<RepositoryResult.Failure>(saveResult)
        assertEquals(RepositoryFailure.Unknown, saveResult.reason)

        val getResult = repository.getEntryById(LibraryEntryId("e1"))
        assertIs<RepositoryResult.Failure>(getResult)
        assertEquals(RepositoryFailure.Unknown, getResult.reason)
    }
}
