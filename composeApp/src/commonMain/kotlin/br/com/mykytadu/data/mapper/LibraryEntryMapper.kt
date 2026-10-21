package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus

fun LibraryEntry.toEntity(): LibraryEntryEntity =
    LibraryEntryEntity(
        id = id.value,
        animeId = animeId.value,
        status = status.name,
        progressCurrent = progress.current,
        progressTotal = progress.total,
        isFavorite = isFavorite,
        rating = rating?.value,
        notes = notes,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
        schemaVersion = LibraryEntryEntity.CURRENT_SCHEMA_VERSION,
    )

fun LibraryEntryEntity.toDomain(): LibraryEntry {
    val domainStatus = try {
        LibraryStatus.valueOf(status.trim().uppercase())
    } catch (_: IllegalArgumentException) {
        LibraryStatus.PLANNING
    }

    val sanitizedCurrent = progressCurrent.coerceAtLeast(0)
    val sanitizedTotal = progressTotal?.coerceAtLeast(0)
    val domainProgress = LibraryProgress(current = sanitizedCurrent, total = sanitizedTotal)

    val sanitizedRating = rating?.takeIf { it in 0..100 }?.let { LibraryRating(it) }

    val sanitizedNotes = notes?.trim()?.takeIf { it.isNotEmpty() }?.let {
        if (it.length > LibraryEntry.MAX_NOTES_LENGTH) it.take(LibraryEntry.MAX_NOTES_LENGTH) else it
    }

    val safeCreatedAt = createdAtEpochMillis
    val safeUpdatedAt = if (updatedAtEpochMillis >= safeCreatedAt) updatedAtEpochMillis else safeCreatedAt

    val safeId = id.trim().ifEmpty { "generated-${animeId.trim().ifEmpty { "unknown" }}" }
    val safeAnimeId = animeId.trim().ifEmpty { "unknown" }

    return LibraryEntry(
        id = LibraryEntryId(safeId),
        animeId = CatalogAnimeId(safeAnimeId),
        status = domainStatus,
        progress = domainProgress,
        isFavorite = isFavorite,
        rating = sanitizedRating,
        notes = sanitizedNotes,
        createdAtEpochMillis = safeCreatedAt,
        updatedAtEpochMillis = safeUpdatedAt,
    )
}
