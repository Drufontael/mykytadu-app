package br.com.mykytadu.domain.model.auth

/**
 * Representa o par de tokens de autenticação retornado pela API.
 * O accessToken tem tempo de vida curto e deve residir estritamente em memória volátil.
 * O refreshToken é persistido de forma segura via SecureStorageDriver nos targets nativos.
 */
data class AuthTokens(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresInSeconds: Long,
    val refreshToken: String? = null,
) {
    init {
        require(accessToken.isNotBlank()) { "accessToken cannot be blank" }
        require(expiresInSeconds > 0) { "expiresInSeconds must be positive" }
    }
}
