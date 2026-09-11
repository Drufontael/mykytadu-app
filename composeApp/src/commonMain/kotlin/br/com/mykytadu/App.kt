package br.com.mykytadu

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import br.com.mykytadu.core.navigation.AppNavigation
import br.com.mykytadu.core.navigation.NavigationHistoryBridge
import br.com.mykytadu.core.theme.AppTheme

@Composable
fun App(
    navigationHistoryBridge: NavigationHistoryBridge? = null,
) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AppNavigation(navigationHistoryBridge)
        }
    }
}
