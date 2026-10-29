package br.com.mykytadu.features.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.data.local.datasource.InMemorySecureStorageDriver
import br.com.mykytadu.data.remote.api.AuthApi
import br.com.mykytadu.data.remote.dto.auth.LoginRequestDto
import br.com.mykytadu.data.remote.dto.auth.LoginResponseDto
import br.com.mykytadu.data.remote.dto.auth.LogoutRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshResponseDto
import br.com.mykytadu.data.remote.dto.auth.RegisterRequestDto
import br.com.mykytadu.data.remote.dto.auth.RegisterResponseDto
import br.com.mykytadu.data.remote.dto.auth.UserDto
import br.com.mykytadu.data.repository.DefaultAuthRepository
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.features.profile.ProfileScreen
import br.com.mykytadu.features.splash.SplashScreen
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class AuthEndToEndUiTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `fluxo de login completo autentica usuario e navega com sucesso`() {
        val storage = InMemorySecureStorageDriver()
        val fakeApi = FakeEndToEndAuthApi()
        val repository = DefaultAuthRepository(fakeApi, storage)
        val viewModel = AuthViewModel(repository)
        var loginSuccessCalled = false

        compose.setContent {
            AppTheme {
                Box(Modifier.size(800.dp, 800.dp)) {
                    LoginScreen(
                        onLoginSuccess = { loginSuccessCalled = true },
                        viewModel = viewModel,
                    )
                }
            }
        }

        // 1. Verificar estado inicial
        compose.onNodeWithTag("auth_screen").assertIsDisplayed()
        compose.onNodeWithTag("auth_input_email").performTextInput("user@test.com")
        compose.onNodeWithTag("auth_input_password").performTextInput("pass123")

        // 2. Submeter
        compose.onNodeWithTag("auth_submit_button").performClick()
        compose.waitForIdle()

        // 3. Validar sucesso
        assertTrue(loginSuccessCalled)
        assertIs<AuthState.Authenticated>(repository.authState.value)
        assertEquals("refresh-token-test", runBlocking { storage.getRefreshToken() })
    }

    @Test
    fun `fluxo de cadastro completo registra usuario e conclui autenticado`() {
        val storage = InMemorySecureStorageDriver()
        val fakeApi = FakeEndToEndAuthApi()
        val repository = DefaultAuthRepository(fakeApi, storage)
        val viewModel = AuthViewModel(repository)
        var loginSuccessCalled = false

        compose.setContent {
            AppTheme {
                Box(Modifier.size(800.dp, 800.dp)) {
                    LoginScreen(
                        onLoginSuccess = { loginSuccessCalled = true },
                        viewModel = viewModel,
                    )
                }
            }
        }

        // 1. Mudar para o modo Cadastro
        compose.onNodeWithTag("auth_toggle_mode_button").performClick()
        compose.waitForIdle()

        // 2. Preencher formulário de cadastro
        compose.onNodeWithTag("auth_input_name").performTextInput("Novo Usuario")
        compose.onNodeWithTag("auth_input_email").performTextInput("novo@test.com")
        compose.onNodeWithTag("auth_input_password").performTextInput("secret123")

        // 3. Submeter cadastro
        compose.onNodeWithTag("auth_submit_button").performClick()
        compose.waitForIdle()

        // 4. Validar sucesso
        assertTrue(loginSuccessCalled)
        assertIs<AuthState.Authenticated>(repository.authState.value)
    }

    @Test
    fun `splash screen com refresh token valido restaura sessao e navega para home`() {
        val storage = InMemorySecureStorageDriver().apply {
            runBlocking { saveRefreshToken("valid-stored-token") }
        }
        val fakeApi = FakeEndToEndAuthApi()
        val repository = DefaultAuthRepository(fakeApi, storage)

        var navigatedToHome = false
        var navigatedToLogin = false

        compose.setContent {
            AppTheme {
                Box(Modifier.size(800.dp, 800.dp)) {
                    SplashScreen(
                        onNavigateToLogin = { navigatedToLogin = true },
                        onNavigateToHome = { navigatedToHome = true },
                        authRepository = repository,
                    )
                }
            }
        }

        compose.waitForIdle()
        assertTrue(navigatedToHome)
        assertEquals(false, navigatedToLogin)
        assertIs<AuthState.Authenticated>(repository.authState.value)
    }

    @Test
    fun `splash screen sem refresh token navega para login`() {
        val storage = InMemorySecureStorageDriver()
        val fakeApi = FakeEndToEndAuthApi()
        val repository = DefaultAuthRepository(fakeApi, storage)

        var navigatedToHome = false
        var navigatedToLogin = false

        compose.setContent {
            AppTheme {
                Box(Modifier.size(800.dp, 800.dp)) {
                    SplashScreen(
                        onNavigateToLogin = { navigatedToLogin = true },
                        onNavigateToHome = { navigatedToHome = true },
                        authRepository = repository,
                    )
                }
            }
        }

        compose.waitForIdle()
        assertTrue(navigatedToLogin)
        assertEquals(false, navigatedToHome)
        assertIs<AuthState.Unauthenticated>(repository.authState.value)
    }

    @Test
    fun `logout na tela de perfil limpa sessao no storage e define estado unauthenticated`() {
        val storage = InMemorySecureStorageDriver().apply {
            runBlocking { saveRefreshToken("user-refresh-token") }
        }
        val fakeApi = FakeEndToEndAuthApi()
        val repository = DefaultAuthRepository(fakeApi, storage)

        runBlocking {
            repository.login("user@test.com", "pass")
        }

        compose.setContent {
            AppTheme {
                Box(Modifier.size(800.dp, 800.dp)) {
                    ProfileScreen(
                        onNavigateToSettings = {},
                        authRepository = repository,
                    )
                }
            }
        }

        compose.onNodeWithTag("profile_logout_button").performScrollTo().performClick()
        compose.waitForIdle()

        assertNull(runBlocking { storage.getRefreshToken() })
        assertIs<AuthState.Unauthenticated>(repository.authState.value)
    }

    private class FakeEndToEndAuthApi : AuthApi {
        override suspend fun login(request: LoginRequestDto): br.com.mykytadu.core.network.NetworkResult<LoginResponseDto> =
            br.com.mykytadu.core.network.NetworkResult.Success(
                LoginResponseDto(
                    accessToken = "access-token-test",
                    refreshToken = "refresh-token-test",
                    expiresIn = 600,
                    user = UserDto(id = "usr-test", email = request.email, name = "Usuario Teste")
                )
            )

        override suspend fun register(request: RegisterRequestDto): br.com.mykytadu.core.network.NetworkResult<RegisterResponseDto> =
            br.com.mykytadu.core.network.NetworkResult.Success(
                RegisterResponseDto(id = "usr-new", email = request.email, name = request.name)
            )

        override suspend fun refresh(request: RefreshRequestDto): br.com.mykytadu.core.network.NetworkResult<RefreshResponseDto> =
            br.com.mykytadu.core.network.NetworkResult.Success(
                RefreshResponseDto(
                    accessToken = "new-access-token",
                    refreshToken = "new-rotated-token",
                    expiresIn = 600
                )
            )

        override suspend fun logout(request: LogoutRequestDto): br.com.mykytadu.core.network.NetworkResult<Unit> =
            br.com.mykytadu.core.network.NetworkResult.Success(Unit)

        override suspend fun getProfile(accessToken: String): br.com.mykytadu.core.network.NetworkResult<UserDto> =
            br.com.mykytadu.core.network.NetworkResult.Success(
                UserDto(id = "usr-restored", email = "restored@test.com", name = "Usuario Restaurado")
            )
    }
}
