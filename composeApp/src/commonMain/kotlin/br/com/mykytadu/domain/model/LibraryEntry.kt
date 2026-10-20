package br.com.mykytadu.domain.model

data class LibraryEntry(
    val id: LibraryEntryId,
    val animeId: CatalogAnimeId,
    val status: LibraryStatus,
    val progress: LibraryProgress = LibraryProgress(),
    val isFavorite: Boolean = false,
    val rating: LibraryRating? = null,
    val notes: String? = null,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long = createdAtEpochMillis,
) {
    init {
        require(updatedAtEpochMillis >= createdAtEpochMillis) {
            "Updated time ($updatedAtEpochMillis) cannot be before created time ($createdAtEpochMillis)."
        }
        require(notes == null || notes.length <= MAX_NOTES_LENGTH) {
            "Notes length exceeds maximum allowed ($MAX_NOTES_LENGTH)."
        }
    }

    fun withStatus(newStatus: LibraryStatus, updatedAt: Long): LibraryEntry =
        copy(status = newStatus, updatedAtEpochMillis = updatedAt)

    fun withProgress(newProgress: LibraryProgress, updatedAt: Long): LibraryEntry =
        copy(progress = newProgress, updatedAtEpochMillis = updatedAt)

    fun withFavorite(favorite: Boolean, updatedAt: Long): LibraryEntry =
        copy(isFavorite = favorite, updatedAtEpochMillis = updatedAt)

    fun withRating(newRating: LibraryRating?, updatedAt: Long): LibraryEntry =
        copy(rating = newRating, updatedAtEpochMillis = updatedAt)

    fun withNotes(newNotes: String?, updatedAt: Long): LibraryEntry =
        copy(notes = newNotes?.trim()?.ifEmpty { null }, updatedAtEpochMillis = updatedAt)

    companion object {
        const val MAX_NOTES_LENGTH = 5_000
    }
}
