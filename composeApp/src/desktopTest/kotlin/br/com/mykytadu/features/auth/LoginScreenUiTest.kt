package br.com.mykytadu.features.auth

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.domain.model.auth.AuthFailure
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class LoginScreenUiTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `renderiza tela de login com campos de email e senha e botao de entrar`() {
        val state = AuthUiState(
            mode = AuthMode.LOGIN,
            email = "",
            password = "",
        )

        compose.setContent {
            AppTheme {
                LoginScreenContent(
                    uiState = state,
                    onEmailChange = {},
                    onPasswordChange = {},
                    onNameChange = {},
                    onModeToggle = {},
                    onSubmit = {},
                    onRetry = {},
                )
            }
        }

        compose.onNodeWithTag("auth_screen").assertIsDisplayed()
        compose.onNodeWithTag("auth_card").assertIsDisplayed()
        compose.onNodeWithTag("auth_title").assertIsDisplayed()
        compose.onNodeWithTag("auth_input_email").assertIsDisplayed()
        compose.onNodeWithTag("auth_input_password").assertIsDisplayed()
        compose.onNodeWithTag("auth_submit_button").assertIsDisplayed()
        compose.onNodeWithTag("auth_toggle_mode_button").assertIsDisplayed()
    }

    @Test
    fun `modo REGISTER exibe campo de nome`() {
        val state = AuthUiState(
            mode = AuthMode.REGISTER,
            email = "test@example.com",
            name = "Nome Teste",
        )

        compose.setContent {
            AppTheme {
                LoginScreenContent(
                    uiState = state,
                    onEmailChange = {},
                    onPasswordChange = {},
                    onNameChange = {},
                    onModeToggle = {},
                    onSubmit = {},
                    onRetry = {},
                )
            }
        }

        compose.onNodeWithTag("auth_input_name").assertIsDisplayed()
        compose.onNodeWithTag("auth_input_email").assertIsDisplayed()
        compose.onNodeWithTag("auth_input_password").assertIsDisplayed()
    }

    @Test
    fun `exibe banner de erro quando error esta presente no uiState`() {
        val state = AuthUiState(
            mode = AuthMode.LOGIN,
            error = AuthFailure.InvalidCredentials("Credenciais invalidas"),
        )

        compose.setContent {
            AppTheme {
                LoginScreenContent(
                    uiState = state,
                    onEmailChange = {},
                    onPasswordChange = {},
                    onNameChange = {},
                    onModeToggle = {},
                    onSubmit = {},
                    onRetry = {},
                )
            }
        }

        compose.onNodeWithTag("auth_error_banner").assertIsDisplayed()
    }

    @Test
    fun `clicar no botao de alternar modo invoca onModeToggle`() {
        var modeToggled = false
        val state = AuthUiState(mode = AuthMode.LOGIN)

        compose.setContent {
            AppTheme {
                LoginScreenContent(
                    uiState = state,
                    onEmailChange = {},
                    onPasswordChange = {},
                    onNameChange = {},
                    onModeToggle = { modeToggled = true },
                    onSubmit = {},
                    onRetry = {},
                )
            }
        }

        compose.onNodeWithTag("auth_toggle_mode_button").performClick()
        assertTrue(modeToggled)
    }

    @Test
    fun `clicar no botao submit invoca onSubmit`() {
        var submitted = false
        val state = AuthUiState(mode = AuthMode.LOGIN, email = "a@b.com", password = "123")

        compose.setContent {
            AppTheme {
                LoginScreenContent(
                    uiState = state,
                    onEmailChange = {},
                    onPasswordChange = {},
                    onNameChange = {},
                    onModeToggle = {},
                    onSubmit = { submitted = true },
                    onRetry = {},
                )
            }
        }

        compose.onNodeWithTag("auth_submit_button").performClick()
        assertTrue(submitted)
    }

    @Test
    fun `digitar no campo email invoca onEmailChange`() {
        var typedEmail = ""
        val state = AuthUiState(mode = AuthMode.LOGIN)

        compose.setContent {
            AppTheme {
                LoginScreenContent(
                    uiState = state,
                    onEmailChange = { typedEmail = it },
                    onPasswordChange = {},
                    onNameChange = {},
                    onModeToggle = {},
                    onSubmit = {},
                    onRetry = {},
                )
            }
        }

        compose.onNodeWithTag("auth_input_email").performTextInput("user@test.com")
        assertEquals("user@test.com", typedEmail)
    }
}
