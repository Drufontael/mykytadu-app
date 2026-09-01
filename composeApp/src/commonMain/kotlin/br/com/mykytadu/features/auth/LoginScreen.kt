package br.com.mykytadu.features.auth

import androidx.compose.runtime.Composable
import br.com.mykytadu.presentation.NavigationPlaceholder

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
) {
    NavigationPlaceholder(
        text = "Login",
        onClick = onNavigateToHome
    )
}
