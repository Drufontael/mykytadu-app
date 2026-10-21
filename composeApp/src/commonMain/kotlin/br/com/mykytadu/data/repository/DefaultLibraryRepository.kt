package br.com.mykytadu.data.repository

import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.mapper.toDomain
import br.com.mykytadu.data.mapper.toEntity
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.repository.LibraryRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementação padrão de [LibraryRepository] baseada em [LibraryLocalDataSource].
 * Converte chamadas e modelos do domínio para as entidades e operações de persistência local.
 */
class DefaultLibraryRepository(
    private val localDataSource: LibraryLocalDataSource,
) : LibraryRepository {

    override suspend fun saveEntry(entry: LibraryEntry): RepositoryResult<Unit> =
        executeCatching {
            localDataSource.upsertEntry(entry.toEntity())
        }

    override suspend fun getEntryById(id: LibraryEntryId): RepositoryResult<LibraryEntry?> =
        executeCatching {
            localDataSource.getEntryById(id.value)?.toDomain()
        }

    override suspend fun getEntryByAnimeId(animeId: CatalogAnimeId): RepositoryResult<LibraryEntry?> =
        executeCatching {
            localDataSource.getEntryByAnimeId(animeId.value)?.toDomain()
        }

    override suspend fun getAllEntries(): RepositoryResult<List<LibraryEntry>> =
        executeCatching {
            localDataSource.getAllEntries().map { it.toDomain() }
        }

    override suspend fun getEntriesByStatus(status: LibraryStatus): RepositoryResult<List<LibraryEntry>> =
        executeCatching {
            localDataSource.getEntriesByStatus(status.name).map { it.toDomain() }
        }

    override suspend fun deleteEntry(id: LibraryEntryId): RepositoryResult<Boolean> =
        executeCatching {
            localDataSource.deleteEntry(id.value)
        }

    override suspend fun deleteEntryByAnimeId(animeId: CatalogAnimeId): RepositoryResult<Boolean> =
        executeCatching {
            localDataSource.deleteEntryByAnimeId(animeId.value)
        }

    override fun observeAllEntries(): Flow<List<LibraryEntry>> =
        localDataSource.observeAllEntries().map { list ->
            list.map { it.toDomain() }
        }

    override fun observeEntryByAnimeId(animeId: CatalogAnimeId): Flow<LibraryEntry?> =
        localDataSource.observeEntryByAnimeId(animeId.value).map { entity ->
            entity?.toDomain()
        }

    override fun observeEntriesByStatus(status: LibraryStatus): Flow<List<LibraryEntry>> =
        localDataSource.observeEntriesByStatus(status.name).map { list ->
            list.map { it.toDomain() }
        }

    private inline fun <T> executeCatching(block: () -> T): RepositoryResult<T> =
        try {
            RepositoryResult.Success(block())
        } catch (cause: IllegalArgumentException) {
            RepositoryResult.Failure(reason = RepositoryFailure.InvalidData, cause = cause)
        } catch (cause: Exception) {
            RepositoryResult.Failure(reason = RepositoryFailure.Unknown, cause = cause)
        }
}
