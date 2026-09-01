package br.com.mykytadu.core.navigation

import br.com.mykytadu.features.profile.ProfileScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import br.com.mykytadu.features.anime.AnimeDetailsScreen
import br.com.mykytadu.features.auth.LoginScreen
import br.com.mykytadu.features.home.HomeScreen
import br.com.mykytadu.features.library.LibraryScreen
import br.com.mykytadu.features.search.SearchScreen
import br.com.mykytadu.features.settings.SettingsScreen
import br.com.mykytadu.features.splash.SplashScreen
import br.com.mykytadu.presentation.MainNavigationBar
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@OptIn(ExperimentalSerializationApi::class)
private val navigationConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclassesOfSealed<AppRoute>()
        }
    }
}

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(
        navigationConfig,
        AppRoute.Splash
    )
    val currentRoute = backStack.last() as AppRoute

    val showMainNavigation = MainDestination.entries.any {
        it.route == currentRoute
    }

    Scaffold(
        bottomBar = {
            if (showMainNavigation) {
                MainNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            backStack.removeLastOrNull()
                            backStack.add(route)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = {
                    backStack.removeLastOrNull()
                },
                entryProvider = entryProvider {
                    entry<AppRoute.Splash> {
                        SplashScreen(
                            onNavigateToLogin = {
                                backStack.clear()
                                backStack.add(AppRoute.Login)
                            }
                        )
                    }

                    entry<AppRoute.Login> {
                        LoginScreen(
                            onLoginSuccess = {
                                backStack.clear()
                                backStack.add(AppRoute.Home)
                            }
                        )
                    }

                    entry<AppRoute.Home> {
                        HomeScreen()
                    }

                    entry<AppRoute.Search> {
                        SearchScreen(
                            onNavigateToAnimeDetails = {
                                backStack.add(AppRoute.AnimeDetails)
                            }
                        )
                    }

                    entry<AppRoute.AnimeDetails> {
                        AnimeDetailsScreen()
                    }

                    entry<AppRoute.Library> {
                        LibraryScreen(
                            onNavigateToAnimeDetails = {
                                backStack.add(AppRoute.AnimeDetails)
                            }
                        )
                    }

                    entry<AppRoute.Profile> {
                        ProfileScreen(
                            onNavigateToSettings = {
                                backStack.add(AppRoute.Settings)
                            }
                        )
                    }

                    entry<AppRoute.Settings> {
                        SettingsScreen()
                    }
                }
            )
        }
    }
}
