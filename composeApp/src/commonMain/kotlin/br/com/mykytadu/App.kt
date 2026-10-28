package br.com.mykytadu

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import br.com.mykytadu.core.navigation.AppNavigation
import br.com.mykytadu.core.navigation.NavigationHistoryBridge
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.data.local.datasource.InMemoryPreferencesLocalDataSource
import br.com.mykytadu.data.repository.DefaultPreferencesRepository
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.UserPreferences
import br.com.mykytadu.domain.repository.PreferencesRepository

@Composable
fun App(
    navigationHistoryBridge: NavigationHistoryBridge? = null,
    preferencesRepository: PreferencesRepository = runCatching {
        org.koin.mp.KoinPlatform.getKoin().get<PreferencesRepository>()
    }.getOrElse { DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource()) },
) {
    val preferences by preferencesRepository.observePreferences()
        .collectAsState(initial = UserPreferences())

    val isDarkTheme = when (preferences.theme) {
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }

    AppTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AppNavigation(navigationHistoryBridge)
        }
    }
}
