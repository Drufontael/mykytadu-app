package br.com.mykytadu.features.library

import androidx.compose.runtime.Composable
import br.com.mykytadu.presentation.NavigationPlaceholder

@Composable
fun LibraryScreen(
    onNavigateToAnimeDetails: () -> Unit
) {
    NavigationPlaceholder(
        text = "Biblioteca",
        onClick = onNavigateToAnimeDetails
    )
}
