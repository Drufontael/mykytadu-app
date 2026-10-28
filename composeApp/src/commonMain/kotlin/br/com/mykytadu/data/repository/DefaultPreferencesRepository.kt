package br.com.mykytadu.data.repository

import br.com.mykytadu.data.local.datasource.PreferencesLocalDataSource
import br.com.mykytadu.data.mapper.toDomain
import br.com.mykytadu.data.mapper.toEntity
import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.TitleLanguagePreference
import br.com.mykytadu.domain.model.UserPreferences
import br.com.mykytadu.domain.repository.PreferencesRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementação padrão de [PreferencesRepository] integrando com [PreferencesLocalDataSource].
 */
class DefaultPreferencesRepository(
    private val localDataSource: PreferencesLocalDataSource,
) : PreferencesRepository {

    override suspend fun getPreferences(): RepositoryResult<UserPreferences> =
        executeCatching {
            localDataSource.getPreferences().toDomain()
        }

    override suspend fun updateTheme(theme: ThemePreference): RepositoryResult<Unit> =
        executeCatching {
            val current = localDataSource.getPreferences().toDomain()
            localDataSource.savePreferences(current.copy(theme = theme).toEntity())
        }

    override suspend fun updateTitleLanguage(language: TitleLanguagePreference): RepositoryResult<Unit> =
        executeCatching {
            val current = localDataSource.getPreferences().toDomain()
            localDataSource.savePreferences(current.copy(titleLanguage = language).toEntity())
        }

    override suspend fun updateAdultContent(enabled: Boolean): RepositoryResult<Unit> =
        executeCatching {
            val current = localDataSource.getPreferences().toDomain()
            localDataSource.savePreferences(current.copy(adultContent = enabled).toEntity())
        }

    override suspend fun updateAppLanguage(language: AppLanguagePreference): RepositoryResult<Unit> =
        executeCatching {
            val current = localDataSource.getPreferences().toDomain()
            localDataSource.savePreferences(current.copy(appLanguage = language).toEntity())
        }

    override suspend fun savePreferences(preferences: UserPreferences): RepositoryResult<Unit> =
        executeCatching {
            localDataSource.savePreferences(preferences.toEntity())
        }

    override fun observePreferences(): Flow<UserPreferences> =
        localDataSource.observePreferences().map { it.toDomain() }

    private inline fun <T> executeCatching(block: () -> T): RepositoryResult<T> =
        try {
            RepositoryResult.Success(block())
        } catch (cause: IllegalArgumentException) {
            RepositoryResult.Failure(reason = RepositoryFailure.InvalidData, cause = cause)
        } catch (cause: Exception) {
            RepositoryResult.Failure(reason = RepositoryFailure.Unknown, cause = cause)
        }
}
