package br.com.mykytadu.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import br.com.mykytadu.features.anime.AnimeDetailsScreen
import br.com.mykytadu.features.auth.LoginScreen
import br.com.mykytadu.features.home.HomeScreen
import br.com.mykytadu.features.library.LibraryScreen
import br.com.mykytadu.features.profile.ProfileScreen
import br.com.mykytadu.features.search.SearchScreen
import br.com.mykytadu.features.settings.SettingsScreen
import br.com.mykytadu.features.splash.SplashScreen
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
    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<AppRoute.Splash> {
                SplashScreen(
                    onNavigateToLogin = {
                        backStack.add(AppRoute.Login)
                    }
                )
            }

            entry<AppRoute.Login> {
                LoginScreen(
                    onNavigateToHome = {
                        backStack.add(AppRoute.Home)
                    }
                )
            }

            entry<AppRoute.Home> {
                HomeScreen(
                    onNavigateToSearch = {
                        backStack.add(AppRoute.Search)
                    }
                )
            }

            entry<AppRoute.Search> {
                SearchScreen(
                    onNavigateToAnimeDatails = {
                        backStack.add(AppRoute.AnimeDetails)
                    }
                )
            }

            entry<AppRoute.AnimeDetails> {
                AnimeDetailsScreen(
                    onNavigateToLibrary = {
                        backStack.add(AppRoute.Library)
                    }
                )
            }

            entry<AppRoute.Library> {
                LibraryScreen(
                    onNavigateToProfile = {
                        backStack.add(AppRoute.Profile)
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