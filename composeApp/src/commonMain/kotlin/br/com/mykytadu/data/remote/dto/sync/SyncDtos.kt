package br.com.mykytadu.data.remote.dto.sync

import kotlinx.serialization.Serializable

@Serializable
enum class SyncMutationType {
    UPSERT,
    DELETE,
}

@Serializable
data class LibraryEntrySyncDto(
    val animeId: String,
    val status: String,
    val progressCurrent: Int = 0,
    val progressTotal: Int? = null,
    val isFavorite: Boolean = false,
    val rating: Int? = null,
    val notes: String? = null,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Serializable
data class SyncMutationDto(
    val animeId: String,
    val type: SyncMutationType,
    val payload: LibraryEntrySyncDto? = null,
    val timestampEpochMillis: Long,
)

@Serializable
data class SyncBatchRequestDto(
    val lastSyncedAtEpochMillis: Long? = null,
    val mutations: List<SyncMutationDto> = emptyList(),
)

@Serializable
data class SyncBatchResponseDto(
    val syncedAtEpochMillis: Long,
    val appliedCount: Int = 0,
    val serverChanges: List<LibraryEntrySyncDto> = emptyList(),
    val deletedAnimeIds: List<String> = emptyList(),
)
