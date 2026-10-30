package br.com.mykytadu.features.localization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.*
import br.com.mykytadu.core.localization.ProvideAppLocale
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.features.anime.AnimeDetailsContent
import org.jetbrains.compose.resources.stringResource
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class LocalizationEndToEndUiTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `troca dinamica de idioma reflete imediatamente em todos os catalogos da interface`() {
        var currentLanguage by mutableStateOf(AppLanguagePreference.PT_BR)

        compose.setContent {
            ProvideAppLocale(currentLanguage) {
                AppTheme {
                    Box(Modifier.size(600.dp, 800.dp)) {
                        Column {
                            Text(stringResource(Res.string.settings_title))
                            Text(stringResource(Res.string.search_title))
                            Text(stringResource(Res.string.library_title))
                            Text(stringResource(Res.string.settings_language_pt_br))
                            Text(stringResource(Res.string.settings_language_en_us))
                            Text(stringResource(Res.string.settings_language_es_es))
                        }
                    }
                }
            }
        }

        // Validação em Português
        compose.onNodeWithText("Configurações").assertIsDisplayed()
        compose.onNodeWithText("Pesquisa").assertIsDisplayed()
        compose.onNodeWithText("Biblioteca").assertIsDisplayed()

        // Alterna dinamicamente para Inglês
        currentLanguage = AppLanguagePreference.EN_US
        compose.waitForIdle()

        compose.onNodeWithText("Settings").assertIsDisplayed()
        compose.onNodeWithText("Search").assertIsDisplayed()
        compose.onNodeWithText("Library").assertIsDisplayed()

        // Alterna dinamicamente para Espanhol
        currentLanguage = AppLanguagePreference.ES_ES
        compose.waitForIdle()

        compose.onNodeWithText("Configuración").assertIsDisplayed()
        compose.onNodeWithText("Búsqueda").assertIsDisplayed()
        compose.onNodeWithText("Biblioteca").assertIsDisplayed()

        // Alterna de volta para Português
        currentLanguage = AppLanguagePreference.PT_BR
        compose.waitForIdle()

        compose.onNodeWithText("Configurações").assertIsDisplayed()
        compose.onNodeWithText("Pesquisa").assertIsDisplayed()
        compose.onNodeWithText("Biblioteca").assertIsDisplayed()
    }

    @Test
    fun `fluxo completo de traducao sob demanda de sinopse com badge e retorno ao original`() {
        val originalEnglishSynopsis = "A pirate crew sails the seas in search of the greatest treasure in the world."
        val translatedPortugueseSynopsis = "Uma tripulação pirata navega pelos mares em busca do maior tesouro do mundo."

        val details = AnimeDetails(
            id = CatalogAnimeId("100"),
            titles = AnimeTitles(english = "One Piece"),
            description = originalEnglishSynopsis,
        )

        var isShowingTranslation by mutableStateOf(false)
        var translatedDescription: String? by mutableStateOf(null)
        var translationToggleCalls = 0

        compose.setContent {
            ProvideAppLocale(AppLanguagePreference.PT_BR) {
                AppTheme {
                    Box(Modifier.size(800.dp, 900.dp)) {
                        AnimeDetailsContent(
                            details = details,
                            isShowingTranslation = isShowingTranslation,
                            translatedDescription = translatedDescription,
                            onToggleTranslation = {
                                translationToggleCalls++
                                if (isShowingTranslation) {
                                    isShowingTranslation = false
                                } else {
                                    translatedDescription = translatedPortugueseSynopsis
                                    isShowingTranslation = true
                                }
                            },
                        )
                    }
                }
            }
        }

        // 1. Estado inicial: texto original em exibição, botão para traduzir visível
        val toggleButton = compose.onNodeWithTag("translation_toggle_button")
        toggleButton.assertIsDisplayed()
        toggleButton.assertTextContains("Traduzir sinopse")
        compose.onNodeWithText(originalEnglishSynopsis).assertIsDisplayed()

        // 2. Dispara a tradução sob demanda
        toggleButton.performClick()
        compose.waitForIdle()

        assertEquals(1, translationToggleCalls)
        compose.onNodeWithTag("translation_badge").assertIsDisplayed()
        toggleButton.assertTextContains("Ver sinopse original")
        compose.onNodeWithText(translatedPortugueseSynopsis).assertIsDisplayed()

        // 3. Clica para ver original
        toggleButton.performClick()
        compose.waitForIdle()

        assertEquals(2, translationToggleCalls)
        toggleButton.assertTextContains("Traduzir sinopse")
        compose.onNodeWithText(originalEnglishSynopsis).assertIsDisplayed()

        // 4. Alterna de volta para a tradução previamente em cache
        toggleButton.performClick()
        compose.waitForIdle()

        assertEquals(3, translationToggleCalls)
        compose.onNodeWithTag("translation_badge").assertIsDisplayed()
        toggleButton.assertTextContains("Ver sinopse original")
        compose.onNodeWithText(translatedPortugueseSynopsis).assertIsDisplayed()
    }
}
