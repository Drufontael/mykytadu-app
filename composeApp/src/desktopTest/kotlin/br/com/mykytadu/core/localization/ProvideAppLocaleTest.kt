package br.com.mykytadu.core.localization

import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.search_title
import br.com.mykytadu.domain.model.AppLanguagePreference
import org.jetbrains.compose.resources.stringResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale

@OptIn(ExperimentalTestApi::class)
class ProvideAppLocaleTest {

    @get:Rule
    val compose = createComposeRule()

    private var originalLocale: Locale = Locale.getDefault()

    @Before
    fun setUp() {
        originalLocale = Locale.getDefault()
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun `ProvideAppLocale aplica idioma ingles na interface`() {
        compose.setContent {
            ProvideAppLocale(AppLanguagePreference.EN_US) {
                Text(text = stringResource(Res.string.search_title))
            }
        }

        compose.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun `ProvideAppLocale aplica idioma espanhol na interface`() {
        compose.setContent {
            ProvideAppLocale(AppLanguagePreference.ES_ES) {
                Text(text = stringResource(Res.string.search_title))
            }
        }

        compose.onNodeWithText("Búsqueda").assertIsDisplayed()
    }

    @Test
    fun `ProvideAppLocale aplica idioma portugues na interface`() {
        compose.setContent {
            ProvideAppLocale(AppLanguagePreference.PT_BR) {
                Text(text = stringResource(Res.string.search_title))
            }
        }

        compose.onNodeWithText("Pesquisa").assertIsDisplayed()
    }
}
