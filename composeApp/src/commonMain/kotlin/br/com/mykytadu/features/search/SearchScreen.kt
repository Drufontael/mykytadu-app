package br.com.mykytadu.features.search

import androidx.compose.runtime.Composable
import br.com.mykytadu.presentation.NavigationPlaceholder

@Composable
fun SearchScreen(
    onNavigateToAnimeDetails: () -> Unit
) {
    NavigationPlaceholder(
        text = "Pesquisa",
        onClick = onNavigateToAnimeDetails
    )
}
