package br.com.mykytadu.domain.model.profile

import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryStatus

data class UserStats(
    val totalAnimeCount: Int = 0,
    val watchingCount: Int = 0,
    val completedCount: Int = 0,
    val planningCount: Int = 0,
    val pausedCount: Int = 0,
    val droppedCount: Int = 0,
    val favoriteCount: Int = 0,
    val totalEpisodesWatched: Int = 0,
    val estimatedHoursWatched: Double = 0.0,
    val averageScore: Double? = null,
) {
    companion object {
        const val MINUTES_PER_EPISODE = 24.0

        fun fromEntries(entries: List<LibraryEntry>): UserStats {
            if (entries.isEmpty()) return UserStats()

            var watching = 0
            var completed = 0
            var planning = 0
            var paused = 0
            var dropped = 0
            var favorites = 0
            var episodes = 0
            var ratingSum = 0
            var ratingCount = 0

            for (entry in entries) {
                when (entry.status) {
                    LibraryStatus.WATCHING -> watching++
                    LibraryStatus.COMPLETED -> completed++
                    LibraryStatus.PLANNING -> planning++
                    LibraryStatus.PAUSED -> paused++
                    LibraryStatus.DROPPED -> dropped++
                }

                if (entry.isFavorite) {
                    favorites++
                }

                episodes += entry.progress.current

                entry.rating?.let { rating ->
                    ratingSum += rating.value
                    ratingCount++
                }
            }

            val hours = (episodes * MINUTES_PER_EPISODE) / 60.0
            val avgScore = if (ratingCount > 0) ratingSum.toDouble() / ratingCount else null

            return UserStats(
                totalAnimeCount = entries.size,
                watchingCount = watching,
                completedCount = completed,
                planningCount = planning,
                pausedCount = paused,
                droppedCount = dropped,
                favoriteCount = favorites,
                totalEpisodesWatched = episodes,
                estimatedHoursWatched = hours,
                averageScore = avgScore,
            )
        }
    }
}
