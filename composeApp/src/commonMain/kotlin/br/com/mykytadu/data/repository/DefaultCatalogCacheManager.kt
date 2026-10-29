package br.com.mykytadu.data.repository

import br.com.mykytadu.data.local.datasource.CatalogCacheLocalDataSource
import br.com.mykytadu.domain.repository.CatalogCacheManager

class DefaultCatalogCacheManager(
    private val catalogCacheLocalDataSource: CatalogCacheLocalDataSource,
) : CatalogCacheManager {

    override suspend fun clearCatalogCache() {
        catalogCacheLocalDataSource.clearCatalogCache()
    }

    override suspend fun clearExpired(currentTimeMillis: Long) {
        catalogCacheLocalDataSource.clearExpired(currentTimeMillis)
    }

    override suspend fun getCachedSearchesCount(): Int {
        return catalogCacheLocalDataSource.getSearchEntriesCount()
    }

    override suspend fun getCachedDetailsCount(): Int {
        return catalogCacheLocalDataSource.getDetailsEntriesCount()
    }
}
