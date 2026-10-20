package br.com.mykytadu.domain.model

data class LibraryProgress(
    val current: Int = 0,
    val total: Int? = null,
) {
    init {
        require(current >= 0) { "Current progress must not be negative." }
        require(total == null || total >= 0) { "Total episodes must not be negative." }
    }

    val isOverTotal: Boolean
        get() = total != null && total > 0 && current > total

    val clampedCurrent: Int
        get() = total?.let { minOf(current, it) } ?: current

    val completionRatio: Float?
        get() = total?.takeIf { it > 0 }?.let { (current.toFloat() / it).coerceIn(0f, 1f) }

    fun increment(): LibraryProgress = copy(current = current + 1)

    fun decrement(): LibraryProgress = if (current > 0) copy(current = current - 1) else this

    fun withCurrent(newCurrent: Int): LibraryProgress = copy(current = newCurrent)

    fun withTotal(newTotal: Int?): LibraryProgress = copy(total = newTotal)
}
