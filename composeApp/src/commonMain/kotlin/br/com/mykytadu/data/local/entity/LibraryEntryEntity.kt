package br.com.mykytadu.data.local.entity

import kotlinx.serialization.Serializable

/**
 * Entidade de persistência para armazenar uma entrada da biblioteca local.
 * Utiliza apenas tipos primitivos serializáveis e mantém schemaVersion para suporte a migrações.
 */
@Serializable
data class LibraryEntryEntity(
    val id: String,
    val animeId: String,
    val status: String,
    val progressCurrent: Int = 0,
    val progressTotal: Int? = null,
    val isFavorite: Boolean = false,
    val rating: Int? = null,
    val notes: String? = null,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long = createdAtEpochMillis,
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
) {
    companion object {
        const val CURRENT_SCHEMA_VERSION = 1
    }
}
