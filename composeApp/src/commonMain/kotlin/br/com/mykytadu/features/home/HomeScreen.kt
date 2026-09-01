package br.com.mykytadu.features.home

import androidx.compose.runtime.Composable
import br.com.mykytadu.presentation.NavigationPlaceholder

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
) {
    NavigationPlaceholder(
        text = "Home",
        onClick = onNavigateToSearch
    )
}
