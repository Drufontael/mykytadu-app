package br.com.mykytadu.features.splash

import androidx.compose.runtime.Composable
import br.com.mykytadu.presentation.NavigationPlaceholder

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {
    NavigationPlaceholder(
        text = "Splash",
        onClick = onNavigateToLogin
    )
}
