package br.com.mykytadu.features.search

import androidx.compose.runtime.Composable
import br.com.mykytadu.presentation.NavigationPlaceholder

@Composable
fun SearchScreen(
    onNavigateToAnimeDatails: () -> Unit,
) {
    NavigationPlaceholder(
        text = "Pesquisa",
        onClick = onNavigateToAnimeDatails
    )
}
