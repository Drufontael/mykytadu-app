package br.com.mykytadu.data.local.entity

/**
 * Entidade de armazenamento local para preferências do usuário.
 * Mantém valores serializados como tipos primitivos para isolamento físico.
 */
data class PreferencesEntity(
    val theme: String = "SYSTEM",
    val titleLanguage: String = "ROMAJI",
    val adultContent: Boolean = false,
    val appLanguage: String = "SYSTEM",
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
) {
    companion object {
        const val CURRENT_SCHEMA_VERSION = 1
    }
}
