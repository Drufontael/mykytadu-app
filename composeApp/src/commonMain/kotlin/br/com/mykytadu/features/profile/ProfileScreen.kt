package br.com.mykytadu.features.profile

import androidx.compose.runtime.Composable
import br.com.mykytadu.presentation.NavigationPlaceholder

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit
) {
    NavigationPlaceholder(
        text = "Perfil",
        onClick = onNavigateToSettings
    )
}