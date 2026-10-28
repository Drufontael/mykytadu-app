package br.com.mykytadu.domain.repository

import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.TitleLanguagePreference
import br.com.mykytadu.domain.model.UserPreferences
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de domínio para leitura, mutação e observação reativa das preferências do usuário.
 */
interface PreferencesRepository {

    suspend fun getPreferences(): RepositoryResult<UserPreferences>

    suspend fun updateTheme(theme: ThemePreference): RepositoryResult<Unit>

    suspend fun updateTitleLanguage(language: TitleLanguagePreference): RepositoryResult<Unit>

    suspend fun updateAdultContent(enabled: Boolean): RepositoryResult<Unit>

    suspend fun updateAppLanguage(language: AppLanguagePreference): RepositoryResult<Unit>

    suspend fun savePreferences(preferences: UserPreferences): RepositoryResult<Unit>

    fun observePreferences(): Flow<UserPreferences>
}
