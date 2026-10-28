package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import br.com.mykytadu.data.remote.dto.sync.LibraryEntrySyncDto
import br.com.mykytadu.domain.model.LibraryEntry

fun LibraryEntryEntity.toSyncDto(): LibraryEntrySyncDto =
    LibraryEntrySyncDto(
        animeId = animeId,
        status = status,
        progressCurrent = progressCurrent,
        progressTotal = progressTotal,
        isFavorite = isFavorite,
        rating = rating,
        notes = notes,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )

fun LibraryEntry.toSyncDto(): LibraryEntrySyncDto =
    toEntity().toSyncDto()

fun LibraryEntrySyncDto.toEntity(existingLocalId: String? = null): LibraryEntryEntity =
    LibraryEntryEntity(
        id = existingLocalId ?: "sync-$animeId",
        animeId = animeId,
        status = status,
        progressCurrent = progressCurrent,
        progressTotal = progressTotal,
        isFavorite = isFavorite,
        rating = rating,
        notes = notes,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
        schemaVersion = LibraryEntryEntity.CURRENT_SCHEMA_VERSION,
    )
