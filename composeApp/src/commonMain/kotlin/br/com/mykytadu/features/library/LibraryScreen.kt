package br.com.mykytadu.features.library

import androidx.compose.runtime.Composable
import br.com.mykytadu.presentation.NavigationPlaceholder

@Composable
fun LibraryScreen(
    onNavigateToProfile: () -> Unit
) {
    NavigationPlaceholder(
        text = "Biblioteca",
        onClick = onNavigateToProfile
    )
}
