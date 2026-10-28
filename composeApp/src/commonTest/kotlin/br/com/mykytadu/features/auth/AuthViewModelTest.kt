package br.com.mykytadu.features.auth

import br.com.mykytadu.domain.model.auth.AuthFailure
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.auth.User
import br.com.mykytadu.domain.model.auth.UserId
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.result.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAuthRepository()
        viewModel = AuthViewModel(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `estado inicial deve estar no modo LOGIN com campos vazios`() {
        val state = viewModel.uiState.value
        assertEquals(AuthMode.LOGIN, state.mode)
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertEquals("", state.name)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `alterar email senha e nome deve atualizar uiState e limpar erro`() {
        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("123456")
        viewModel.onNameChanged("User Test")

        val state = viewModel.uiState.value
        assertEquals("user@example.com", state.email)
        assertEquals("123456", state.password)
        assertEquals("User Test", state.name)
        assertNull(state.error)
    }

    @Test
    fun `alternar modo para REGISTER e voltar para LOGIN deve atualizar uiState`() {
        viewModel.setMode(AuthMode.REGISTER)
        assertEquals(AuthMode.REGISTER, viewModel.uiState.value.mode)

        viewModel.setMode(AuthMode.LOGIN)
        assertEquals(AuthMode.LOGIN, viewModel.uiState.value.mode)
    }

    @Test
    fun `submit no modo LOGIN com campos vazios define erro de validacao`() {
        viewModel.submit()
        assertIs<AuthFailure.InvalidCredentials>(viewModel.uiState.value.error)

        viewModel.onEmailChanged("test@example.com")
        viewModel.submit()
        assertIs<AuthFailure.InvalidCredentials>(viewModel.uiState.value.error)
    }

    @Test
    fun `submit no modo REGISTER sem nome define erro de validacao`() {
        viewModel.setMode(AuthMode.REGISTER)
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("123456")
        viewModel.submit()

        assertIs<AuthFailure.RegistrationRejected>(viewModel.uiState.value.error)
    }

    @Test
    fun `submit no modo LOGIN com sucesso chama repository login e conclui sem erro`() = runTest {
        val user = User(id = UserId("u1"), email = "test@example.com", name = "Test")
        fakeRepository.loginResult = AuthResult.Success(user)

        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.submit()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
        assertEquals(user, (viewModel.uiState.value.authState as? AuthState.Authenticated)?.user)
    }

    @Test
    fun `submit no modo REGISTER com sucesso chama repository register`() = runTest {
        val user = User(id = UserId("u2"), email = "new@example.com", name = "New User")
        fakeRepository.registerResult = AuthResult.Success(user)

        viewModel.setMode(AuthMode.REGISTER)
        viewModel.onEmailChanged("new@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.onNameChanged("New User")
        viewModel.submit()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `submit com falha no repository atualiza estado de erro`() = runTest {
        fakeRepository.loginResult = AuthResult.Failure(AuthFailure.InvalidCredentials("Credenciais incorretas."))

        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("wrong")
        viewModel.submit()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        val error = assertIs<AuthFailure.InvalidCredentials>(viewModel.uiState.value.error)
        assertEquals("Credenciais incorretas.", error.message)
    }

    @Test
    fun `logout chama repository logout`() = runTest {
        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeRepository.logoutCalled)
    }

    private class FakeAuthRepository : AuthRepository {
        private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
        override val authState: StateFlow<AuthState> = _authState.asStateFlow()

        var loginResult: AuthResult<User> = AuthResult.Failure(AuthFailure.InvalidCredentials())
        var registerResult: AuthResult<User> = AuthResult.Failure(AuthFailure.RegistrationRejected())
        var logoutCalled = false

        override suspend fun login(email: String, password: String): AuthResult<User> {
            val result = loginResult
            if (result is AuthResult.Success) {
                _authState.value = AuthState.Authenticated(result.value)
            }
            return result
        }

        override suspend fun register(email: String, password: String, name: String): AuthResult<User> {
            val result = registerResult
            if (result is AuthResult.Success) {
                _authState.value = AuthState.Authenticated(result.value)
            }
            return result
        }

        override suspend fun logout(): AuthResult<Unit> {
            logoutCalled = true
            _authState.value = AuthState.Unauthenticated
            return AuthResult.Success(Unit)
        }

        override suspend fun restoreSession(): AuthResult<User?> = AuthResult.Success(null)
        override fun currentAccessToken(): String? = null
    }
}
