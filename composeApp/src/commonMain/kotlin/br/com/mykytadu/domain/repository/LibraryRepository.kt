package br.com.mykytadu.domain.repository

import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositório para acesso e manipulação da biblioteca pessoal local-first.
 * Não expõe entidades físicas de dados e opera estritamente com os modelos de domínio.
 */
interface LibraryRepository {

    suspend fun saveEntry(entry: LibraryEntry): RepositoryResult<Unit>

    suspend fun getEntryById(id: LibraryEntryId): RepositoryResult<LibraryEntry?>

    suspend fun getEntryByAnimeId(animeId: CatalogAnimeId): RepositoryResult<LibraryEntry?>

    suspend fun getAllEntries(): RepositoryResult<List<LibraryEntry>>

    suspend fun getEntriesByStatus(status: LibraryStatus): RepositoryResult<List<LibraryEntry>>

    suspend fun deleteEntry(id: LibraryEntryId): RepositoryResult<Boolean>

    suspend fun deleteEntryByAnimeId(animeId: CatalogAnimeId): RepositoryResult<Boolean>

    fun observeAllEntries(): Flow<List<LibraryEntry>>

    fun observeEntryByAnimeId(animeId: CatalogAnimeId): Flow<LibraryEntry?>

    fun observeEntriesByStatus(status: LibraryStatus): Flow<List<LibraryEntry>>
}
