package br.com.mykytadu.features.auth

import androidx.compose.runtime.Composable
import br.com.mykytadu.presentation.NavigationPlaceholder

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    NavigationPlaceholder(
        text = "Login",
        onClick = onLoginSuccess
    )
}
