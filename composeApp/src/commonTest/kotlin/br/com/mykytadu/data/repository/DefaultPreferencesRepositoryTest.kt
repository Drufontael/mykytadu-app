package br.com.mykytadu.data.repository

import br.com.mykytadu.data.local.datasource.InMemoryPreferencesLocalDataSource
import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.TitleLanguagePreference
import br.com.mykytadu.domain.model.UserPreferences
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DefaultPreferencesRepositoryTest {

    @Test
    fun `getPreferences retorna configuracoes padrao iniciais`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())

        val result = repository.getPreferences()
        assertIs<RepositoryResult.Success<UserPreferences>>(result)
        assertEquals(ThemePreference.SYSTEM, result.value.theme)
        assertEquals(TitleLanguagePreference.ROMAJI, result.value.titleLanguage)
        assertEquals(false, result.value.adultContent)
        assertEquals(AppLanguagePreference.SYSTEM, result.value.appLanguage)
    }

    @Test
    fun `updateTheme atualiza apenas o tema e emite no fluxo reativo`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())

        val updateResult = repository.updateTheme(ThemePreference.DARK)
        assertIs<RepositoryResult.Success<Unit>>(updateResult)

        val current = repository.observePreferences().first()
        assertEquals(ThemePreference.DARK, current.theme)
        assertEquals(TitleLanguagePreference.ROMAJI, current.titleLanguage)
    }

    @Test
    fun `updateTitleLanguage atualiza a preferencia de titulo de forma consistente`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())

        val updateResult = repository.updateTitleLanguage(TitleLanguagePreference.ENGLISH)
        assertIs<RepositoryResult.Success<Unit>>(updateResult)

        val current = repository.getPreferences()
        assertIs<RepositoryResult.Success<UserPreferences>>(current)
        assertEquals(TitleLanguagePreference.ENGLISH, current.value.titleLanguage)
    }

    @Test
    fun `updateAdultContent atualiza a flag de conteudo adulto`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())

        val updateResult = repository.updateAdultContent(true)
        assertIs<RepositoryResult.Success<Unit>>(updateResult)

        val current = repository.getPreferences()
        assertIs<RepositoryResult.Success<UserPreferences>>(current)
        assertTrue(current.value.adultContent)
    }

    @Test
    fun `updateAppLanguage atualiza a preferencia de idioma`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())

        val updateResult = repository.updateAppLanguage(AppLanguagePreference.PT_BR)
        assertIs<RepositoryResult.Success<Unit>>(updateResult)

        val current = repository.getPreferences()
        assertIs<RepositoryResult.Success<UserPreferences>>(current)
        assertEquals(AppLanguagePreference.PT_BR, current.value.appLanguage)
    }

    @Test
    fun `savePreferences substitui todo o conjunto de preferencias`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())
        val custom = UserPreferences(
            theme = ThemePreference.LIGHT,
            titleLanguage = TitleLanguagePreference.NATIVE,
            adultContent = true,
            appLanguage = AppLanguagePreference.EN_US,
        )

        val saveResult = repository.savePreferences(custom)
        assertIs<RepositoryResult.Success<Unit>>(saveResult)

        val current = repository.observePreferences().first()
        assertEquals(custom, current)
    }
}
