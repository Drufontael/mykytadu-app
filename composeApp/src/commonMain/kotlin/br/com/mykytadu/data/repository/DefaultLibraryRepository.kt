package br.com.mykytadu.data.repository

import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.SyncQueueLocalDataSource
import br.com.mykytadu.data.local.entity.SyncQueueEntryEntity
import br.com.mykytadu.data.mapper.toDomain
import br.com.mykytadu.data.mapper.toEntity
import br.com.mykytadu.data.remote.dto.sync.SyncMutationType
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
 * Quando fornecido, enfileira mutações em [SyncQueueLocalDataSource] para sincronização posterior.
 */
class DefaultLibraryRepository(
    private val localDataSource: LibraryLocalDataSource,
    private val syncQueueLocalDataSource: SyncQueueLocalDataSource? = null,
) : LibraryRepository {

    override suspend fun saveEntry(entry: LibraryEntry): RepositoryResult<Unit> =
        executeCatching {
            val entity = entry.toEntity()
            localDataSource.upsertEntry(entity)
            syncQueueLocalDataSource?.enqueue(
                SyncQueueEntryEntity(
                    id = "op-save-${entry.animeId.value}-${entry.updatedAtEpochMillis}",
                    animeId = entry.animeId.value,
                    type = SyncMutationType.UPSERT,
                    entryEntity = entity,
                    timestampEpochMillis = entry.updatedAtEpochMillis,
                )
            )
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
            val existing = localDataSource.getEntryById(id.value)
            val removed = localDataSource.deleteEntry(id.value)
            if (removed && existing != null) {
                syncQueueLocalDataSource?.enqueue(
                    SyncQueueEntryEntity(
                        id = "op-del-${existing.animeId}-${existing.updatedAtEpochMillis}",
                        animeId = existing.animeId,
                        type = SyncMutationType.DELETE,
                        entryEntity = null,
                        timestampEpochMillis = existing.updatedAtEpochMillis,
                    )
                )
            }
            removed
        }

    override suspend fun deleteEntryByAnimeId(animeId: CatalogAnimeId): RepositoryResult<Boolean> =
        executeCatching {
            val existing = localDataSource.getEntryByAnimeId(animeId.value)
            val removed = localDataSource.deleteEntryByAnimeId(animeId.value)
            if (removed) {
                val timestamp = existing?.updatedAtEpochMillis ?: 0L
                syncQueueLocalDataSource?.enqueue(
                    SyncQueueEntryEntity(
                        id = "op-del-${animeId.value}-$timestamp",
                        animeId = animeId.value,
                        type = SyncMutationType.DELETE,
                        entryEntity = null,
                        timestampEpochMillis = timestamp,
                    )
                )
            }
            removed
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
