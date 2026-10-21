package br.com.mykytadu.data.local.migration

import br.com.mykytadu.data.local.entity.LibraryEntryEntity

/**
 * Utilitário responsável pela validação e migração defensiva de esquemas
 * de entidades armazenadas localmente.
 */
object LibrarySchemaMigration {

    /**
     * Normaliza e migra a entidade para [LibraryEntryEntity.CURRENT_SCHEMA_VERSION].
     * Trata esquemas de versões legadas ou saneamento defensivo de campos.
     */
    fun migrate(entity: LibraryEntryEntity): LibraryEntryEntity {
        var current = entity

        if (current.schemaVersion < 1) {
            current = current.copy(schemaVersion = 1)
        }

        return current
    }

    /**
     * Valida a integridade básica de uma entidade de armazenamento.
     */
    fun isValid(entity: LibraryEntryEntity): Boolean {
        return entity.id.isNotBlank() &&
            entity.animeId.isNotBlank() &&
            entity.status.isNotBlank() &&
            entity.progressCurrent >= 0 &&
            (entity.progressTotal == null || entity.progressTotal >= 0) &&
            (entity.rating == null || entity.rating in 0..100) &&
            entity.updatedAtEpochMillis >= entity.createdAtEpochMillis
    }
}
