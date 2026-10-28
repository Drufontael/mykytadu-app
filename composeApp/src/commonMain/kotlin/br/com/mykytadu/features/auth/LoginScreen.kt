package br.com.mykytadu.features.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.auth_button_login
import br.com.mykytadu.composeapp.generated.resources.auth_button_register
import br.com.mykytadu.composeapp.generated.resources.auth_email_label
import br.com.mykytadu.composeapp.generated.resources.auth_email_placeholder
import br.com.mykytadu.composeapp.generated.resources.auth_error_generic
import br.com.mykytadu.composeapp.generated.resources.auth_error_invalid_credentials
import br.com.mykytadu.composeapp.generated.resources.auth_error_network
import br.com.mykytadu.composeapp.generated.resources.auth_error_rate_limited
import br.com.mykytadu.composeapp.generated.resources.auth_error_registration_rejected
import br.com.mykytadu.composeapp.generated.resources.auth_error_session_expired
import br.com.mykytadu.composeapp.generated.resources.auth_login_subtitle
import br.com.mykytadu.composeapp.generated.resources.auth_login_title
import br.com.mykytadu.composeapp.generated.resources.auth_name_label
import br.com.mykytadu.composeapp.generated.resources.auth_name_placeholder
import br.com.mykytadu.composeapp.generated.resources.auth_password_label
import br.com.mykytadu.composeapp.generated.resources.auth_password_placeholder
import br.com.mykytadu.composeapp.generated.resources.auth_register_subtitle
import br.com.mykytadu.composeapp.generated.resources.auth_register_title
import br.com.mykytadu.composeapp.generated.resources.auth_toggle_to_login
import br.com.mykytadu.composeapp.generated.resources.auth_toggle_to_register
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.domain.model.auth.AuthFailure
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.presentation.components.AppButton
import br.com.mykytadu.presentation.components.AppCard
import br.com.mykytadu.presentation.components.AppError
import br.com.mykytadu.presentation.components.AppTextField
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.authState) {
        if (uiState.authState is AuthState.Authenticated) {
            onLoginSuccess()
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onNameChange = viewModel::onNameChanged,
        onModeToggle = {
            val nextMode = if (uiState.mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN
            viewModel.setMode(nextMode)
        },
        onSubmit = viewModel::submit,
        onRetry = viewModel::submit,
        modifier = modifier,
    )
}

@Composable
fun LoginScreenContent(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onModeToggle: () -> Unit,
    onSubmit: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isRegister = uiState.mode == AuthMode.REGISTER

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("auth_screen"),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center,
        ) {
            AppCard(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .padding(AppDimensions.padding.lg)
                    .testTag("auth_card"),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimensions.padding.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                ) {
                    Text(
                        text = if (isRegister) {
                            stringResource(Res.string.auth_register_title)
                        } else {
                            stringResource(Res.string.auth_login_title)
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("auth_title"),
                    )

                    Text(
                        text = if (isRegister) {
                            stringResource(Res.string.auth_register_subtitle)
                        } else {
                            stringResource(Res.string.auth_login_subtitle)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(AppDimensions.spacing.sm))

                    if (isRegister) {
                        AppTextField(
                            value = uiState.name,
                            onValueChange = onNameChange,
                            label = stringResource(Res.string.auth_name_label),
                            placeholder = stringResource(Res.string.auth_name_placeholder),
                            singleLine = true,
                            enabled = !uiState.isLoading,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_input_name"),
                        )
                    }

                    AppTextField(
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        label = stringResource(Res.string.auth_email_label),
                        placeholder = stringResource(Res.string.auth_email_placeholder),
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("auth_input_email"),
                    )

                    AppTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        label = stringResource(Res.string.auth_password_label),
                        placeholder = stringResource(Res.string.auth_password_placeholder),
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onSubmit() }
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("auth_input_password"),
                    )

                    if (uiState.error != null) {
                        val errorMessage = when (val err = uiState.error) {
                            is AuthFailure.InvalidCredentials -> err.message ?: stringResource(Res.string.auth_error_invalid_credentials)
                            is AuthFailure.RegistrationRejected -> err.message ?: stringResource(Res.string.auth_error_registration_rejected)
                            is AuthFailure.SessionExpired -> err.message ?: stringResource(Res.string.auth_error_session_expired)
                            is AuthFailure.RateLimitExceeded -> stringResource(Res.string.auth_error_rate_limited)
                            is AuthFailure.NetworkError -> stringResource(Res.string.auth_error_network)
                            is AuthFailure.ServerError -> err.detail ?: stringResource(Res.string.auth_error_generic)
                            is AuthFailure.EmailVerificationRequired -> err.message ?: "Confirmação de e-mail necessária."
                            is AuthFailure.Unknown -> stringResource(Res.string.auth_error_generic)
                        }

                        AppError(
                            message = errorMessage,
                            onRetry = onRetry,
                            modifier = Modifier.fillMaxWidth().testTag("auth_error_banner"),
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.spacing.xs))

                    AppButton(
                        text = if (isRegister) {
                            stringResource(Res.string.auth_button_register)
                        } else {
                            stringResource(Res.string.auth_button_login)
                        },
                        onClick = onSubmit,
                        loading = uiState.isLoading,
                        modifier = Modifier.fillMaxWidth().testTag("auth_submit_button"),
                    )

                    TextButton(
                        onClick = onModeToggle,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.testTag("auth_toggle_mode_button"),
                    ) {
                        Text(
                            text = if (isRegister) {
                                stringResource(Res.string.auth_toggle_to_login)
                            } else {
                                stringResource(Res.string.auth_toggle_to_register)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}
