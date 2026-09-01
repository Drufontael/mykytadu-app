package br.com.mykytadu.presentation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import br.com.mykytadu.core.navigation.AppRoute
import br.com.mykytadu.core.navigation.MainDestination

@Composable
fun MainNavigationBar(
    currentRoute: AppRoute,
    onNavigate: (AppRoute) -> Unit
) {
    NavigationBar {
        MainDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = {
                    onNavigate(destination.route)
                },
                icon = {},
                label = {
                    Text(destination.label)
                }
            )
        }
    }
}