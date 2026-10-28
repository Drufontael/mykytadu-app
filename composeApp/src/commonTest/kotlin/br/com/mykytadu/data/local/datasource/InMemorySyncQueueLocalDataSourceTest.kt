package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import br.com.mykytadu.data.local.entity.SyncQueueEntryEntity
import br.com.mykytadu.data.remote.dto.sync.SyncMutationType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemorySyncQueueLocalDataSourceTest {

    @Test
    fun `deve enfileirar operacoes e observar contagem`() = runTest {
        val dataSource = InMemorySyncQueueLocalDataSource()

        assertEquals(0, dataSource.observePendingCount().first())

        val entry1 = SyncQueueEntryEntity(
            id = "op-1",
            animeId = "101",
            type = SyncMutationType.UPSERT,
            entryEntity = LibraryEntryEntity(
                id = "local-1",
                animeId = "101",
                status = "COMPLETED",
                createdAtEpochMillis = 1000L,
            ),
            timestampEpochMillis = 1000L,
        )

        dataSource.enqueue(entry1)

        assertEquals(1, dataSource.observePendingCount().first())
        val pending = dataSource.getPendingEntries()
        assertEquals(1, pending.size)
        assertEquals("101", pending.first().animeId)
        assertEquals(SyncMutationType.UPSERT, pending.first().type)
    }

    @Test
    fun `deve colapsar mutacoes sucessivas para o mesmo animeId`() = runTest {
        val dataSource = InMemorySyncQueueLocalDataSource()

        val entry1 = SyncQueueEntryEntity(
            id = "op-1",
            animeId = "101",
            type = SyncMutationType.UPSERT,
            entryEntity = LibraryEntryEntity(
                id = "local-1",
                animeId = "101",
                status = "WATCHING",
                createdAtEpochMillis = 1000L,
            ),
            timestampEpochMillis = 1000L,
        )
        val entry2 = SyncQueueEntryEntity(
            id = "op-2",
            animeId = "101",
            type = SyncMutationType.DELETE,
            entryEntity = null,
            timestampEpochMillis = 1500L,
        )

        dataSource.enqueue(entry1)
        dataSource.enqueue(entry2)

        val pending = dataSource.getPendingEntries()
        assertEquals(1, pending.size)
        assertEquals("op-2", pending.first().id)
        assertEquals(SyncMutationType.DELETE, pending.first().type)
    }

    @Test
    fun `deve remover entradas processadas por ids`() = runTest {
        val dataSource = InMemorySyncQueueLocalDataSource()

        val op1 = SyncQueueEntryEntity("op-1", "101", SyncMutationType.UPSERT, null, 1000L)
        val op2 = SyncQueueEntryEntity("op-2", "102", SyncMutationType.UPSERT, null, 1000L)
        dataSource.enqueue(op1)
        dataSource.enqueue(op2)

        assertEquals(2, dataSource.getPendingEntries().size)

        dataSource.removeEntries(listOf("op-1"))

        val remaining = dataSource.getPendingEntries()
        assertEquals(1, remaining.size)
        assertEquals("op-2", remaining.first().id)
    }

    @Test
    fun `deve limpar toda a fila`() = runTest {
        val dataSource = InMemorySyncQueueLocalDataSource()

        dataSource.enqueue(SyncQueueEntryEntity("op-1", "101", SyncMutationType.UPSERT, null, 1000L))
        dataSource.enqueue(SyncQueueEntryEntity("op-2", "102", SyncMutationType.UPSERT, null, 1000L))

        assertEquals(2, dataSource.getPendingEntries().size)

        dataSource.clear()

        assertTrue(dataSource.getPendingEntries().isEmpty())
        assertEquals(0, dataSource.observePendingCount().first())
    }
}
