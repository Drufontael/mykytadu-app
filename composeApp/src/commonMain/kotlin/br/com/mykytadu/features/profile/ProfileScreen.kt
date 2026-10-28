package br.com.mykytadu.features.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.auth_logout_button
import br.com.mykytadu.composeapp.generated.resources.settings_title
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.presentation.components.AppButton
import br.com.mykytadu.presentation.components.AppCard
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatformTools

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    authRepository: AuthRepository? = null,
) {
    val repository = authRepository ?: KoinPlatformTools.defaultContext().getOrNull()?.getOrNull<AuthRepository>()
    val authState = repository?.authState?.collectAsStateWithLifecycle()?.value
    val scope = rememberCoroutineScope()

    val userName = if (authState is AuthState.Authenticated) authState.user.name else "Usuário"
    val userEmail = if (authState is AuthState.Authenticated) authState.user.email else ""

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("profile_screen"),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            AppCard(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .padding(AppDimensions.padding.lg)
                    .testTag("profile_card"),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimensions.padding.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                ) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("profile_user_name"),
                    )

                    if (userEmail.isNotEmpty()) {
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("profile_user_email"),
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.spacing.md))

                    AppButton(
                        text = stringResource(Res.string.settings_title),
                        onClick = onNavigateToSettings,
                        modifier = Modifier.fillMaxWidth().testTag("profile_settings_button"),
                    )

                    if (repository != null) {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    repository.logout()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("profile_logout_button"),
                        ) {
                            Text(
                                text = stringResource(Res.string.auth_logout_button),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }
    }
}
