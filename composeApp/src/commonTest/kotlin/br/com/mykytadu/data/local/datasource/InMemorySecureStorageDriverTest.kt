package br.com.mykytadu.data.local.datasource

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InMemorySecureStorageDriverTest {

    @Test
    fun `armazena, recupera e limpa refresh token com sucesso`() = runTest {
        val driver = InMemorySecureStorageDriver()
        assertNull(driver.getRefreshToken())

        driver.saveRefreshToken("token-xyz-123")
        assertEquals("token-xyz-123", driver.getRefreshToken())

        driver.clearRefreshToken()
        assertNull(driver.getRefreshToken())
    }

    @Test
    fun `inicializa com token preexistente opcional`() = runTest {
        val driver = InMemorySecureStorageDriver("initial-token")
        assertEquals("initial-token", driver.getRefreshToken())
    }
}
