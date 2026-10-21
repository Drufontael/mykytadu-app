package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de fonte de dados local da biblioteca pessoal.
 * Opera exclusivamente com [LibraryEntryEntity], mantendo a persistência física
 * isolada dos modelos de domínio e da interface gráfica.
 */
interface LibraryLocalDataSource {

    suspend fun getEntryById(id: String): LibraryEntryEntity?

    suspend fun getEntryByAnimeId(animeId: String): LibraryEntryEntity?

    suspend fun getAllEntries(): List<LibraryEntryEntity>

    suspend fun getEntriesByStatus(status: String): List<LibraryEntryEntity>

    suspend fun upsertEntry(entry: LibraryEntryEntity)

    suspend fun deleteEntry(id: String): Boolean

    suspend fun deleteEntryByAnimeId(animeId: String): Boolean

    suspend fun clearAll()

    fun observeAllEntries(): Flow<List<LibraryEntryEntity>>

    fun observeEntryByAnimeId(animeId: String): Flow<LibraryEntryEntity?>

    fun observeEntriesByStatus(status: String): Flow<List<LibraryEntryEntity>>
}
