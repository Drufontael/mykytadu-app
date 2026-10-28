package br.com.mykytadu.data.local.datasource

/**
 * Implementação em memória volátil de SecureStorageDriver para testes e ambientes isolados.
 */
class InMemorySecureStorageDriver(
    initialRefreshToken: String? = null,
) : SecureStorageDriver {

    private var token: String? = initialRefreshToken

    override suspend fun getRefreshToken(): String? = token

    override suspend fun saveRefreshToken(token: String) {
        this.token = token
    }

    override suspend fun clearRefreshToken() {
        this.token = null
    }
}
