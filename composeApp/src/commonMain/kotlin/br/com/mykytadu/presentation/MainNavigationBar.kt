package br.com.mykytadu.presentation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import br.com.mykytadu.core.navigation.AppRoute
import br.com.mykytadu.core.navigation.MainDestination
import androidx.compose.material3.Icon
import br.com.mykytadu.presentation.components.icons.AppIcons

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

@Composable
fun MainNavigationRail(
    currentRoute: AppRoute,
    onNavigate: (AppRoute) -> Unit
) {
    NavigationRail {
        MainDestination.entries.forEach { destination ->
            NavigationRailItem(
                selected = currentRoute == destination.route,
                onClick = { onNavigate(destination.route) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(destination.label) }
            )
        }
    }
}

private val MainDestination.icon
    get() = when (this) {
        MainDestination.HOME -> AppIcons.Navigation.Home
        MainDestination.LIBRARY -> AppIcons.Anime.Favorite
        MainDestination.SEARCH -> AppIcons.Actions.Search
        MainDestination.PROFILE -> AppIcons.Navigation.Profile
    }
