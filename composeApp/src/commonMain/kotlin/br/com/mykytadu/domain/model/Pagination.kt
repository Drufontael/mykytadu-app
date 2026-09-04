package br.com.mykytadu.domain.model

data class PageInfo(
    val currentPage: Int,
    val lastPage: Int?,
    val hasNextPage: Boolean,
    val perPage: Int,
    val total: Int?,
) {
    init {
        require(currentPage > 0) { "Current page must be positive." }
        require(perPage > 0) { "Items per page must be positive." }
        require(lastPage == null || lastPage > 0) {
            "Last page must be positive when present."
        }
        require(total == null || total >= 0) {
            "Total must not be negative when present."
        }
    }
}

class PagedResult<out T>(
    items: List<T>,
    val pageInfo: PageInfo,
) {
    val items: List<T> = items.toList()
}
