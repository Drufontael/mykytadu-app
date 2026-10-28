package br.com.mykytadu.features.settings

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.TitleLanguagePreference
import br.com.mykytadu.features.settings.model.SettingsUiState
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SettingsContentUiTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `SettingsContent renderiza todas as secoes e informacoes sobre o aplicativo`() {
        val state = SettingsUiState(
            theme = ThemePreference.SYSTEM,
            titleLanguage = TitleLanguagePreference.ROMAJI,
            adultContent = false,
            appLanguage = AppLanguagePreference.SYSTEM,
            appName = "MykytaDu",
            appVersion = "1.0.0",
        )

        compose.setContent {
            AppTheme {
                SettingsContent(
                    uiState = state,
                    onThemeSelected = {},
                    onTitleLanguageSelected = {},
                    onAdultContentChanged = {},
                    onAppLanguageSelected = {},
                    onNavigateBack = {},
                )
            }
        }

        // TopBar
        compose.onNodeWithText("Configurações").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").assertIsDisplayed()

        // Seção Aparência
        compose.onNodeWithText("Aparência").assertIsDisplayed()
        compose.onNodeWithText("Tema da interface").assertIsDisplayed()
        compose.onNodeWithTag("settings_theme_system").assertIsDisplayed()
        compose.onNodeWithTag("settings_theme_light").assertIsDisplayed()
        compose.onNodeWithTag("settings_theme_dark").assertIsDisplayed()

        // Seção Catálogo e Exibição
        compose.onNodeWithText("Catálogo e Exibição").assertIsDisplayed()
        compose.onNodeWithText("Idioma dos títulos").assertIsDisplayed()
        compose.onNodeWithTag("settings_title_language_romaji").assertIsDisplayed()
        compose.onNodeWithTag("settings_title_language_english").assertIsDisplayed()
        compose.onNodeWithTag("settings_title_language_native").assertIsDisplayed()
        compose.onNodeWithTag("settings_adult_content_switch").assertIsDisplayed().assertIsOff()

        // Seção Idioma do Aplicativo
        compose.onNodeWithText("Idioma do Aplicativo").assertIsDisplayed()
        compose.onNodeWithTag("settings_language_system").assertIsDisplayed()
        compose.onNodeWithTag("settings_language_pt_br").assertIsDisplayed()
        compose.onNodeWithTag("settings_language_en_us").assertIsDisplayed()

        // Seção Sobre
        compose.onNodeWithText("Sobre o MykytaDu").assertIsDisplayed()
        compose.onNodeWithText("MykytaDu").assertIsDisplayed()
        compose.onNodeWithText("Versão 1.0.0").assertIsDisplayed()
        compose.onNodeWithText("Assistente multiplataforma para acompanhamento de animes com persistência local-first.").assertIsDisplayed()
        compose.onNodeWithText("Código aberto em Kotlin e Compose Multiplatform.").assertIsDisplayed()

        // Garantir que nenhum botão ou controle de logout/sessão está presente
        compose.onNodeWithText("Sair").assertDoesNotExist()
        compose.onNodeWithText("Logout").assertDoesNotExist()
        compose.onNodeWithText("Desconectar").assertDoesNotExist()
    }

    @Test
    fun `SettingsContent dispara callbacks corretos ao selecionar opcoes de tema`() {
        var selectedTheme: ThemePreference? = null

        compose.setContent {
            AppTheme {
                SettingsContent(
                    uiState = SettingsUiState(theme = ThemePreference.SYSTEM),
                    onThemeSelected = { selectedTheme = it },
                    onTitleLanguageSelected = {},
                    onAdultContentChanged = {},
                    onAppLanguageSelected = {},
                    onNavigateBack = {},
                )
            }
        }

        compose.onNodeWithTag("settings_theme_dark").performClick()
        assertEquals(ThemePreference.DARK, selectedTheme)

        compose.onNodeWithTag("settings_theme_light").performClick()
        assertEquals(ThemePreference.LIGHT, selectedTheme)

        compose.onNodeWithTag("settings_theme_system").performClick()
        assertEquals(ThemePreference.SYSTEM, selectedTheme)
    }

    @Test
    fun `SettingsContent dispara callbacks corretos ao selecionar idioma dos titulos e alternar conteudo adulto`() {
        var selectedTitleLanguage: TitleLanguagePreference? = null
        var adultContentToggled: Boolean? = null

        compose.setContent {
            AppTheme {
                SettingsContent(
                    uiState = SettingsUiState(
                        titleLanguage = TitleLanguagePreference.ROMAJI,
                        adultContent = false,
                    ),
                    onThemeSelected = {},
                    onTitleLanguageSelected = { selectedTitleLanguage = it },
                    onAdultContentChanged = { adultContentToggled = it },
                    onAppLanguageSelected = {},
                    onNavigateBack = {},
                )
            }
        }

        compose.onNodeWithTag("settings_title_language_english").performClick()
        assertEquals(TitleLanguagePreference.ENGLISH, selectedTitleLanguage)

        compose.onNodeWithTag("settings_title_language_native").performClick()
        assertEquals(TitleLanguagePreference.NATIVE, selectedTitleLanguage)

        compose.onNodeWithTag("settings_adult_content_switch").performClick()
        assertEquals(true, adultContentToggled)
    }

    @Test
    fun `SettingsContent dispara callbacks corretos ao selecionar idioma do aplicativo`() {
        var selectedAppLanguage: AppLanguagePreference? = null

        compose.setContent {
            AppTheme {
                SettingsContent(
                    uiState = SettingsUiState(appLanguage = AppLanguagePreference.SYSTEM),
                    onThemeSelected = {},
                    onTitleLanguageSelected = {},
                    onAdultContentChanged = {},
                    onAppLanguageSelected = { selectedAppLanguage = it },
                    onNavigateBack = {},
                )
            }
        }

        compose.onNodeWithTag("settings_language_pt_br").performClick()
        assertEquals(AppLanguagePreference.PT_BR, selectedAppLanguage)

        compose.onNodeWithTag("settings_language_en_us").performClick()
        assertEquals(AppLanguagePreference.EN_US, selectedAppLanguage)
    }

    @Test
    fun `SettingsContent dispara callback ao clicar no botao de voltar`() {
        var backClicked = false

        compose.setContent {
            AppTheme {
                SettingsContent(
                    uiState = SettingsUiState(),
                    onThemeSelected = {},
                    onTitleLanguageSelected = {},
                    onAdultContentChanged = {},
                    onAppLanguageSelected = {},
                    onNavigateBack = { backClicked = true },
                )
            }
        }

        compose.onNodeWithContentDescription("Voltar").performClick()
        assertTrue(backClicked)
    }
}
