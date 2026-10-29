package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import br.com.mykytadu.data.remote.dto.sync.LibraryEntrySyncDto
import br.com.mykytadu.data.remote.dto.sync.SyncBatchRequestDto
import br.com.mykytadu.data.remote.dto.sync.SyncBatchResponseDto
import br.com.mykytadu.data.remote.dto.sync.SyncMutationDto
import br.com.mykytadu.data.remote.dto.sync.SyncMutationType
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LibrarySyncMapperTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `deve mapear LibraryEntryEntity para LibraryEntrySyncDto corretamente`() {
        val entity = LibraryEntryEntity(
            id = "local-1",
            animeId = "55",
            status = "WATCHING",
            progressCurrent = 4,
            progressTotal = 12,
            isFavorite = true,
            rating = 85,
            notes = "Muito bom",
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 2000L,
        )

        val dto = entity.toSyncDto()

        assertEquals("55", dto.animeId)
        assertEquals("WATCHING", dto.status)
        assertEquals(4, dto.progressCurrent)
        assertEquals(12, dto.progressTotal)
        assertTrue(dto.isFavorite)
        assertEquals(85, dto.rating)
        assertEquals("Muito bom", dto.notes)
        assertEquals(1000L, dto.createdAtEpochMillis)
        assertEquals(2000L, dto.updatedAtEpochMillis)
    }

    @Test
    fun `deve mapear LibraryEntrySyncDto para LibraryEntryEntity preservando id local quando fornecido`() {
        val dto = LibraryEntrySyncDto(
            animeId = "55",
            status = "COMPLETED",
            progressCurrent = 12,
            progressTotal = 12,
            isFavorite = false,
            rating = 90,
            notes = "Finalizado",
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 3000L,
        )

        val entity = dto.toEntity(existingLocalId = "existing-id")

        assertEquals("existing-id", entity.id)
        assertEquals("55", entity.animeId)
        assertEquals("COMPLETED", entity.status)
        assertEquals(12, entity.progressCurrent)
        assertEquals(12, entity.progressTotal)
        assertEquals(90, entity.rating)
        assertEquals(3000L, entity.updatedAtEpochMillis)
    }

    @Test
    fun `deve mapear LibraryEntrySyncDto para LibraryEntryEntity gerando id seguro quando ausente`() {
        val dto = LibraryEntrySyncDto(
            animeId = "99",
            status = "PLANNING",
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 1000L,
        )

        val entity = dto.toEntity()

        assertEquals("sync-99", entity.id)
        assertEquals("99", entity.animeId)
        assertEquals("PLANNING", entity.status)
    }

    @Test
    fun `deve serializar e desserializar SyncBatchRequestDto e SyncBatchResponseDto`() {
        val request = SyncBatchRequestDto(
            lastSyncedAtEpochMillis = 5000L,
            mutations = listOf(
                SyncMutationDto(
                    animeId = "55",
                    type = SyncMutationType.UPSERT,
                    payload = LibraryEntrySyncDto(
                        animeId = "55",
                        status = "WATCHING",
                        progressCurrent = 3,
                        createdAtEpochMillis = 1000L,
                        updatedAtEpochMillis = 2000L,
                    ),
                    timestampEpochMillis = 2000L,
                ),
                SyncMutationDto(
                    animeId = "77",
                    type = SyncMutationType.DELETE,
                    payload = null,
                    timestampEpochMillis = 2500L,
                )
            )
        )

        val encodedRequest = json.encodeToString(request)
        val decodedRequest = json.decodeFromString<SyncBatchRequestDto>(encodedRequest)

        assertEquals(5000L, decodedRequest.lastSyncedAtEpochMillis)
        assertEquals(2, decodedRequest.mutations.size)
        assertEquals(SyncMutationType.UPSERT, decodedRequest.mutations[0].type)
        assertEquals(SyncMutationType.DELETE, decodedRequest.mutations[1].type)

        val response = SyncBatchResponseDto(
            syncedAtEpochMillis = 6000L,
            appliedCount = 2,
            serverChanges = listOf(
                LibraryEntrySyncDto(
                    animeId = "88",
                    status = "COMPLETED",
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 5500L,
                )
            ),
            deletedAnimeIds = listOf("999"),
        )

        val encodedResponse = json.encodeToString(response)
        val decodedResponse = json.decodeFromString<SyncBatchResponseDto>(encodedResponse)

        assertEquals(6000L, decodedResponse.syncedAtEpochMillis)
        assertEquals(2, decodedResponse.appliedCount)
        assertEquals(1, decodedResponse.serverChanges.size)
        assertEquals("88", decodedResponse.serverChanges.first().animeId)
        assertEquals(listOf("999"), decodedResponse.deletedAnimeIds)
    }
}
