package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.PreferencesEntity
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de fonte de dados local para preferências do usuário.
 */
interface PreferencesLocalDataSource {

    suspend fun getPreferences(): PreferencesEntity

    suspend fun savePreferences(preferences: PreferencesEntity)

    fun observePreferences(): Flow<PreferencesEntity>
}
