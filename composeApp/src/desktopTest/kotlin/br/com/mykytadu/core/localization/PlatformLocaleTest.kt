package br.com.mykytadu.core.localization

import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.search_title
import org.jetbrains.compose.resources.stringResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale

@OptIn(ExperimentalTestApi::class)
class PlatformLocaleTest {

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
    fun `stringResource respeita Locale default em ingles`() {
        Locale.setDefault(Locale.US)

        compose.setContent {
            Text(text = stringResource(Res.string.search_title))
        }

        compose.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun `stringResource respeita Locale default em espanhol`() {
        Locale.setDefault(Locale.forLanguageTag("es-ES"))

        compose.setContent {
            Text(text = stringResource(Res.string.search_title))
        }

        compose.onNodeWithText("Búsqueda").assertIsDisplayed()
    }

    @Test
    fun `stringResource respeita Locale default em portugues`() {
        Locale.setDefault(Locale.forLanguageTag("pt-BR"))

        compose.setContent {
            Text(text = stringResource(Res.string.search_title))
        }

        compose.onNodeWithText("Pesquisa").assertIsDisplayed()
    }
}
