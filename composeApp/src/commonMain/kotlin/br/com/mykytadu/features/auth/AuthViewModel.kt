package br.com.mykytadu.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mykytadu.domain.model.auth.AuthFailure
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.result.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.authState.collect { state ->
                _uiState.update { it.copy(authState = state) }
            }
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun setMode(mode: AuthMode) {
        _uiState.update { it.copy(mode = mode, error = null) }
    }

    fun submit() {
        val currentState = _uiState.value
        if (currentState.isLoading) return

        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(error = AuthFailure.InvalidCredentials("Informe seu e-mail.")) }
            return
        }
        if (currentState.password.isBlank()) {
            _uiState.update { it.copy(error = AuthFailure.InvalidCredentials("Informe sua senha.")) }
            return
        }
        if (currentState.mode == AuthMode.REGISTER && currentState.name.isBlank()) {
            _uiState.update { it.copy(error = AuthFailure.RegistrationRejected("Informe seu nome.")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = when (currentState.mode) {
                AuthMode.LOGIN -> authRepository.login(
                    email = currentState.email,
                    password = currentState.password,
                )
                AuthMode.REGISTER -> authRepository.register(
                    email = currentState.email,
                    password = currentState.password,
                    name = currentState.name,
                )
            }
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, error = null) }
                }
                is AuthResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, error = result.failure) }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
