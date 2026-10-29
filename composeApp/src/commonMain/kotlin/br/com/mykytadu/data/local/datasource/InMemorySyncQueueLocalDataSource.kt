package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.SyncQueueEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementação em memória e reativa de [SyncQueueLocalDataSource].
 * Garante acesso seguro e atômico via [Mutex] e [MutableStateFlow].
 *
 * Mutações consecutivas para o mesmo animeId são colapsadas para garantir
 * que apenas o estado final mais recente seja enviado.
 */
class InMemorySyncQueueLocalDataSource(
    initialEntries: List<SyncQueueEntryEntity> = emptyList(),
) : SyncQueueLocalDataSource {

    private val mutex = Mutex()
    private val queueFlow = MutableStateFlow(initialEntries)

    override suspend fun enqueue(entry: SyncQueueEntryEntity): Unit = mutex.withLock {
        val current = queueFlow.value.toMutableList()
        current.removeAll { it.animeId == entry.animeId }
        current.add(entry)
        queueFlow.value = current
    }

    override suspend fun getPendingEntries(): List<SyncQueueEntryEntity> = mutex.withLock {
        queueFlow.value.toList()
    }

    override suspend fun removeEntries(ids: List<String>): Unit = mutex.withLock {
        if (ids.isEmpty()) return@withLock
        val idSet = ids.toSet()
        queueFlow.value = queueFlow.value.filterNot { it.id in idSet }
    }

    override suspend fun clear(): Unit = mutex.withLock {
        queueFlow.value = emptyList()
    }

    override fun observePendingCount(): Flow<Int> =
        queueFlow.map { it.size }
}
