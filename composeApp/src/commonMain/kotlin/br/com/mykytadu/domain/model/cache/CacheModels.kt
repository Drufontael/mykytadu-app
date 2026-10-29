package br.com.mykytadu.domain.model.cache

/**
 * Define políticas padrão de validade e capacidade para o cache do catálogo.
 */
object CachePolicy {
    /**
     * Tempo de vida padrão para buscas (30 minutos).
     */
    const val DEFAULT_SEARCH_TTL_MILLIS: Long = 30 * 60 * 1000L

    /**
     * Tempo de vida padrão para detalhes de anime (24 horas).
     */
    const val DEFAULT_DETAILS_TTL_MILLIS: Long = 24 * 60 * 60 * 1000L

    /**
     * Limite máximo de consultas de busca armazenadas em cache (LRU).
     */
    const val MAX_SEARCH_ENTRIES: Int = 50

    /**
     * Limite máximo de animes detalhados armazenados em cache (LRU).
     */
    const val MAX_DETAILS_ENTRIES: Int = 200
}

/**
 * Indica o estado de frescor de um item recuperado do cache.
 */
enum class CacheStatus {
    /** O dado está dentro da janela de validade e é considerado fresco. */
    FRESH,

    /** O dado expirou, mas ainda pode ser utilizado como fallback offline. */
    STALE,
}

/**
 * Origem do dado retornado para a apresentação.
 */
enum class DataSourceOrigin {
    /** Dado fresco recém-obtido da rede externa. */
    NETWORK,

    /** Dado obtido do cache local dentro da validade (TTL ativo). */
    CACHE_FRESH,

    /** Dado expirado obtido do cache local diante de falha ou ausência de rede (fallback offline). */
    CACHE_STALE,
}

/**
 * Encapsula um valor associado aos seus metadados de ciclo de vida no cache.
 */
data class CachedValue<T>(
    val value: T,
    val cachedAtEpochMillis: Long,
    val expiresAtEpochMillis: Long,
) {
    fun isExpired(currentTimeMillis: Long): Boolean = currentTimeMillis > expiresAtEpochMillis

    fun status(currentTimeMillis: Long): CacheStatus =
        if (isExpired(currentTimeMillis)) CacheStatus.STALE else CacheStatus.FRESH
}
