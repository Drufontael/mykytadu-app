package br.com.mykytadu

import androidx.compose.runtime.Composable
import br.com.mykytadu.core.navigation.AppNavigation
import br.com.mykytadu.core.navigation.NavigationHistoryBridge

@Composable
fun App(
    navigationHistoryBridge: NavigationHistoryBridge? = null,
) {
    AppNavigation(navigationHistoryBridge)
}
