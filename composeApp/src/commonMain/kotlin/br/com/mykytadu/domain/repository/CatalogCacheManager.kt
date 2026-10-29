package br.com.mykytadu.domain.repository

/**
 * Gerenciador de cache do catálogo e dados transitórios de navegação.
 *
 * Garante que a limpeza de cache seja isolada e controlada,
 * sem jamais interagir com ou remover registros persistentes da biblioteca do usuário.
 */
interface CatalogCacheManager {
    suspend fun clearCatalogCache()
    suspend fun clearExpired(currentTimeMillis: Long)
    suspend fun getCachedSearchesCount(): Int
    suspend fun getCachedDetailsCount(): Int
}
