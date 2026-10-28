package br.com.mykytadu.features.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.domain.result.AuthResult
import org.koin.mp.KoinPlatformTools

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit = onNavigateToLogin,
    authRepository: AuthRepository? = null,
) {
    val repository = authRepository ?: KoinPlatformTools.defaultContext().getOrNull()?.getOrNull<AuthRepository>()

    LaunchedEffect(Unit) {
        if (repository != null) {
            when (val result = repository.restoreSession()) {
                is AuthResult.Success -> {
                    if (result.value != null) {
                        onNavigateToHome()
                    } else {
                        onNavigateToLogin()
                    }
                }
                is AuthResult.Failure -> {
                    onNavigateToLogin()
                }
            }
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().testTag("splash_screen"),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(AppDimensions.padding.lg),
        ) {
            Text(
                text = "MykytaDu",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("splash_title"),
            )
            Spacer(modifier = Modifier.height(AppDimensions.spacing.md))
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp).testTag("splash_loading"),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
            )
        }
    }
}
