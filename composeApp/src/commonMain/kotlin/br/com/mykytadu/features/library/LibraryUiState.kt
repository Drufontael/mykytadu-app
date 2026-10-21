package br.com.mykytadu.features.library

import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryStatus

enum class LibraryFilter {
    ALL,
    WATCHING,
    PLANNING,
    COMPLETED,
    PAUSED,
    DROPPED;

    fun matches(status: LibraryStatus): Boolean = when (this) {
        ALL -> true
        WATCHING -> status == LibraryStatus.WATCHING
        PLANNING -> status == LibraryStatus.PLANNING
        COMPLETED -> status == LibraryStatus.COMPLETED
        PAUSED -> status == LibraryStatus.PAUSED
        DROPPED -> status == LibraryStatus.DROPPED
    }
}

enum class LibrarySort {
    RECENT_UPDATED,
    TITLE,
    RATING,
    PROGRESS,
}

data class LibraryItemUiState(
    val id: LibraryEntryId,
    val animeId: CatalogAnimeId,
    val title: String,
    val coverUrl: String? = null,
    val status: LibraryStatus,
    val progressCurrent: Int,
    val progressTotal: Int? = null,
    val progressRatio: Float? = null,
    val isFavorite: Boolean = false,
    val rating: Int? = null,
    val notes: String? = null,
    val updatedAtEpochMillis: Long,
) {
    val progressLabel: String
        get() = progressTotal?.let { "$progressCurrent / $it" } ?: "$progressCurrent"
}

sealed interface LibraryUiState {
    data object Loading : LibraryUiState

    data class Content(
        val items: List<LibraryItemUiState>,
        val filter: LibraryFilter = LibraryFilter.ALL,
        val sort: LibrarySort = LibrarySort.RECENT_UPDATED,
        val statusCounts: Map<LibraryFilter, Int> = emptyMap(),
        val totalEntriesCount: Int = 0,
    ) : LibraryUiState {
        val isFilterEmpty: Boolean get() = items.isEmpty() && totalEntriesCount > 0
        val isLibraryEmpty: Boolean get() = totalEntriesCount == 0
    }

    data class Failure(
        val message: String? = null,
    ) : LibraryUiState
}
