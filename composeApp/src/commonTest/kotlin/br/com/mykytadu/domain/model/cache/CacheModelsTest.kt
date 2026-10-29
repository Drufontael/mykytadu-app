package br.com.mykytadu.domain.model.cache

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CacheModelsTest {

    @Test
    fun `CachedValue calcula expiracao e status corretamente`() {
        val now = 1000L
        val freshValue = CachedValue(
            value = "test-data",
            cachedAtEpochMillis = now,
            expiresAtEpochMillis = now + CachePolicy.DEFAULT_SEARCH_TTL_MILLIS,
        )

        assertFalse(freshValue.isExpired(now + 1000L))
        assertEquals(CacheStatus.FRESH, freshValue.status(now + 1000L))

        // Expirado após ultrapassar o TTL
        val afterExpiry = now + CachePolicy.DEFAULT_SEARCH_TTL_MILLIS + 1L
        assertTrue(freshValue.isExpired(afterExpiry))
        assertEquals(CacheStatus.STALE, freshValue.status(afterExpiry))
    }

    @Test
    fun `CachePolicy possui constantes com valores esperados`() {
        assertEquals(30 * 60 * 1000L, CachePolicy.DEFAULT_SEARCH_TTL_MILLIS)
        assertEquals(24 * 60 * 60 * 1000L, CachePolicy.DEFAULT_DETAILS_TTL_MILLIS)
        assertEquals(50, CachePolicy.MAX_SEARCH_ENTRIES)
        assertEquals(200, CachePolicy.MAX_DETAILS_ENTRIES)
    }

    @Test
    fun `DataSourceOrigin possui valores semanticos esperados`() {
        assertEquals(DataSourceOrigin.NETWORK, DataSourceOrigin.valueOf("NETWORK"))
        assertEquals(DataSourceOrigin.CACHE_FRESH, DataSourceOrigin.valueOf("CACHE_FRESH"))
        assertEquals(DataSourceOrigin.CACHE_STALE, DataSourceOrigin.valueOf("CACHE_STALE"))
    }
}
