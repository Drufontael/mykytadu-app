package br.com.mykytadu.data.local.datasource

/**
 * Driver para armazenamento seguro de credenciais e tokens sensíveis de longa duração (refreshToken).
 * Garante que tokens nunca sejam expostos em SharedPreferences comuns, localStorage ou arquivos de texto puro.
 */
interface SecureStorageDriver {
    suspend fun getRefreshToken(): String?
    suspend fun saveRefreshToken(token: String)
    suspend fun clearRefreshToken()
}
