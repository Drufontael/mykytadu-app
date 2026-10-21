package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementação em memória e reativa de [LibraryLocalDataSource].
 * Garante acesso concorrente seguro via [Mutex] e atualizações atômicas
 * por [MutableStateFlow].
 *
 * Garante a restrição de integridade: no máximo 1 entrada por animeId.
 */
class InMemoryLibraryLocalDataSource(
    initialEntries: List<LibraryEntryEntity> = emptyList(),
) : LibraryLocalDataSource {

    private val mutex = Mutex()
    private val entriesFlow = MutableStateFlow<Map<String, LibraryEntryEntity>>(
        initialEntries.associateBy { it.id }
    )

    override suspend fun getEntryById(id: String): LibraryEntryEntity? = mutex.withLock {
        entriesFlow.value[id]
    }

    override suspend fun getEntryByAnimeId(animeId: String): LibraryEntryEntity? = mutex.withLock {
        entriesFlow.value.values.find { it.animeId == animeId }
    }

    override suspend fun getAllEntries(): List<LibraryEntryEntity> = mutex.withLock {
        entriesFlow.value.values.toList()
    }

    override suspend fun getEntriesByStatus(status: String): List<LibraryEntryEntity> = mutex.withLock {
        val normalized = status.trim().uppercase()
        entriesFlow.value.values.filter { it.status.trim().uppercase() == normalized }
    }

    override suspend fun upsertEntry(entry: LibraryEntryEntity): Unit = mutex.withLock {
        val currentMap = entriesFlow.value.toMutableMap()
        // Garantir unicidade por animeId: remove entrada anterior associada ao mesmo animeId se houver
        val existingWithSameAnime = currentMap.values.find { it.animeId == entry.animeId && it.id != entry.id }
        if (existingWithSameAnime != null) {
            currentMap.remove(existingWithSameAnime.id)
        }
        currentMap[entry.id] = entry
        entriesFlow.value = currentMap
    }

    override suspend fun deleteEntry(id: String): Boolean = mutex.withLock {
        val currentMap = entriesFlow.value.toMutableMap()
        val removed = currentMap.remove(id) != null
        if (removed) {
            entriesFlow.value = currentMap
        }
        removed
    }

    override suspend fun deleteEntryByAnimeId(animeId: String): Boolean = mutex.withLock {
        val currentMap = entriesFlow.value.toMutableMap()
        val existing = currentMap.values.find { it.animeId == animeId } ?: return@withLock false
        currentMap.remove(existing.id)
        entriesFlow.value = currentMap
        true
    }

    override suspend fun clearAll(): Unit = mutex.withLock {
        entriesFlow.value = emptyMap()
    }

    override fun observeAllEntries(): Flow<List<LibraryEntryEntity>> =
        entriesFlow.asStateFlow().map { it.values.toList() }

    override fun observeEntryByAnimeId(animeId: String): Flow<LibraryEntryEntity?> =
        entriesFlow.asStateFlow().map { map -> map.values.find { it.animeId == animeId } }

    override fun observeEntriesByStatus(status: String): Flow<List<LibraryEntryEntity>> {
        val normalized = status.trim().uppercase()
        return entriesFlow.asStateFlow().map { map ->
            map.values.filter { it.status.trim().uppercase() == normalized }
        }
    }
}
