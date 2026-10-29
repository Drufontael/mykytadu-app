package br.com.mykytadu.features.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.data.local.datasource.InMemoryCatalogCacheLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemoryPreferencesLocalDataSource
import br.com.mykytadu.data.repository.DefaultCatalogCacheManager
import br.com.mykytadu.data.repository.DefaultPreferencesRepository
import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.TitleLanguagePreference
import br.com.mykytadu.domain.model.UserPreferences
import br.com.mykytadu.domain.repository.PreferencesRepository
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SettingsEndToEndUiTest {

    @get:Rule
    val compose = createComposeRule()

    private suspend fun PreferencesRepository.currentPrefs(): UserPreferences {
        val result = getPreferences()
        assertTrue(result is RepositoryResult.Success)
        return result.value
    }

    @Test
    fun `fluxo completo de configuracoes atualiza e persiste preferencias no repositorio reativamente`() {
        val dataSource = InMemoryPreferencesLocalDataSource()
        val repository = DefaultPreferencesRepository(dataSource)
        val viewModel = SettingsViewModel(repository)
        var backPressed = false

        compose.setContent {
            val state by viewModel.uiState.collectAsState()
            AppTheme {
                Box(Modifier.size(800.dp, 1000.dp)) {
                    SettingsContent(
                        uiState = state,
                        onThemeSelected = viewModel::setTheme,
                        onTitleLanguageSelected = viewModel::setTitleLanguage,
                        onAdultContentChanged = viewModel::setAdultContent,
                        onAppLanguageSelected = viewModel::setAppLanguage,
                        onNavigateBack = { backPressed = true },
                    )
                }
            }
        }

        // Estado inicial
        compose.onNodeWithText("Configurações").assertIsDisplayed()
        compose.onNodeWithTag("settings_adult_content_switch").assertIsOff()

        // 1. Modificar Tema para DARK
        compose.onNodeWithTag("settings_theme_dark").performClick()
        compose.waitForIdle()

        runBlocking {
            assertEquals(ThemePreference.DARK, repository.currentPrefs().theme)
        }

        // 2. Modificar Idioma dos Títulos para ENGLISH
        compose.onNodeWithTag("settings_title_language_english").performClick()
        compose.waitForIdle()

        runBlocking {
            assertEquals(TitleLanguagePreference.ENGLISH, repository.currentPrefs().titleLanguage)
        }

        // 3. Modificar Conteúdo Adulto
        compose.onNodeWithTag("settings_adult_content_switch").performClick()
        compose.waitForIdle()

        runBlocking {
            assertTrue(repository.currentPrefs().adultContent)
        }
        compose.onNodeWithTag("settings_adult_content_switch").assertIsOn()

        // 4. Modificar Idioma do Aplicativo para PT_BR
        compose.onNodeWithTag("settings_language_pt_br").performClick()
        compose.waitForIdle()

        runBlocking {
            assertEquals(AppLanguagePreference.PT_BR, repository.currentPrefs().appLanguage)
        }

        // 5. Testar botão Voltar
        compose.onNodeWithContentDescription("Voltar").performClick()
        assertTrue(backPressed)

        // 6. Verificar persistência reativa com fluxo do repositório
        runBlocking {
            val persisted = repository.observePreferences().first()
            assertEquals(ThemePreference.DARK, persisted.theme)
            assertEquals(TitleLanguagePreference.ENGLISH, persisted.titleLanguage)
            assertTrue(persisted.adultContent)
            assertEquals(AppLanguagePreference.PT_BR, persisted.appLanguage)
        }
    }

    @Test
    fun `preferencias com falha ou dados ausentes fazem fallback seguro para valores padrao`() {
        val dataSource = InMemoryPreferencesLocalDataSource()
        val repository = DefaultPreferencesRepository(dataSource)

        val prefs = runBlocking { repository.currentPrefs() }
        assertEquals(ThemePreference.SYSTEM, prefs.theme)
        assertEquals(TitleLanguagePreference.ROMAJI, prefs.titleLanguage)
        assertFalse(prefs.adultContent)
        assertEquals(AppLanguagePreference.SYSTEM, prefs.appLanguage)
    }

    @Test
    fun `limpar cache na tela de configuracoes exibe confirmacao e aciona gerenciador de cache`() {
        val prefsDataSource = InMemoryPreferencesLocalDataSource()
        val prefsRepository = DefaultPreferencesRepository(prefsDataSource)
        val cacheDataSource = InMemoryCatalogCacheLocalDataSource()
        val cacheManager = DefaultCatalogCacheManager(cacheDataSource)
        val viewModel = SettingsViewModel(prefsRepository, cacheManager)

        // Popula cache
        runBlocking {
            cacheDataSource.putAnimeDetails(
                details = br.com.mykytadu.domain.model.AnimeDetails(id = br.com.mykytadu.domain.model.CatalogAnimeId("100")),
                cachedAt = 1000L,
                expiresAt = 5000L,
            )
            assertEquals(1, cacheManager.getCachedDetailsCount())
        }

        compose.setContent {
            val state by viewModel.uiState.collectAsState()
            AppTheme {
                Box(Modifier.size(800.dp, 1000.dp)) {
                    SettingsContent(
                        uiState = state,
                        onThemeSelected = viewModel::setTheme,
                        onTitleLanguageSelected = viewModel::setTitleLanguage,
                        onAdultContentChanged = viewModel::setAdultContent,
                        onAppLanguageSelected = viewModel::setAppLanguage,
                        onClearCache = viewModel::clearCatalogCache,
                        onNavigateBack = {},
                    )
                }
            }
        }

        // Clica no botão de limpar cache
        compose.onNodeWithTag("settings_clear_cache_button").performClick()
        compose.waitForIdle()

        // Valida que o cache foi limpo
        runBlocking {
            assertEquals(0, cacheManager.getCachedDetailsCount())
        }

        // Valida que a mensagem de sucesso está visível na tela
        compose.onNodeWithTag("settings_cache_cleared_message").assertIsDisplayed()
    }
}
