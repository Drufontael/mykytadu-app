package br.com.mykytadu.core.navigation

import br.com.mykytadu.features.profile.ProfileScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import br.com.mykytadu.presentation.MainNavigationRail
import br.com.mykytadu.core.layout.ResponsiveLayout
import br.com.mykytadu.core.layout.ResponsiveLayoutTokens
import br.com.mykytadu.core.layout.responsiveLayoutFor
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
fun AppNavigation(
    navigationHistoryBridge: NavigationHistoryBridge? = null,
) {
    val backStack = rememberNavBackStack(
        navigationConfig,
        *(navigationHistoryBridge?.initialBackStack ?: listOf(AppRoute.Splash)).toTypedArray(),
    )
    val currentRoute = backStack.last() as AppRoute

    fun updateBackStack(
        mutation: NavigationMutation,
        update: () -> Unit,
    ) {
        update()
        navigationHistoryBridge?.onAppNavigation(backStack.map { it as AppRoute }, mutation)
    }

    DisposableEffect(navigationHistoryBridge) {
        val dispose = navigationHistoryBridge?.bind { restoredBackStack ->
            backStack.clear()
            backStack.addAll(restoredBackStack)
        }

        onDispose {
            dispose?.invoke()
        }
    }

    val showMainNavigation = MainDestination.entries.any {
        it.route == currentRoute
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val layout = responsiveLayoutFor(maxWidth)
        val onMainDestination = { route: AppRoute ->
            if (route != currentRoute) {
                updateBackStack(NavigationMutation.PUSH) {
                    backStack.removeLastOrNull()
                    backStack.add(route)
                }
            }
        }

        Scaffold(
            bottomBar = {
                if (showMainNavigation && layout == ResponsiveLayout.COMPACT) {
                    MainNavigationBar(currentRoute = currentRoute, onNavigate = onMainDestination)
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (showMainNavigation && layout == ResponsiveLayout.EXPANDED) {
                    MainNavigationRail(currentRoute = currentRoute, onNavigate = onMainDestination)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = androidx.compose.ui.Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = ResponsiveLayoutTokens.contentMaxWidth)
                    ) {
                        NavDisplay(
                backStack = backStack,
                onBack = {
                    if (navigationHistoryBridge == null) {
                        backStack.removeLastOrNull()
                    } else if (!navigationHistoryBridge.requestBrowserBack() && backStack.size > 1) {
                        updateBackStack(NavigationMutation.REPLACE) {
                            backStack.removeLastOrNull()
                        }
                    }
                },
                entryProvider = entryProvider {
                    entry<AppRoute.Splash> {
                        SplashScreen(
                            onNavigateToLogin = {
                                updateBackStack(NavigationMutation.REPLACE) {
                                    backStack.clear()
                                    backStack.add(AppRoute.Login)
                                }
                            }
                        )
                    }

                    entry<AppRoute.Login> {
                        LoginScreen(
                            onLoginSuccess = {
                                updateBackStack(NavigationMutation.REPLACE) {
                                    backStack.clear()
                                    backStack.add(AppRoute.Home)
                                }
                            }
                        )
                    }

                    entry<AppRoute.Home> {
                        HomeScreen()
                    }

                    entry<AppRoute.Search> {
                        SearchScreen(
                            onNavigateToAnimeDetails = {
                                updateBackStack(NavigationMutation.PUSH) {
                                    backStack.add(AppRoute.AnimeDetails)
                                }
                            }
                        )
                    }

                    entry<AppRoute.AnimeDetails> {
                        AnimeDetailsScreen()
                    }

                    entry<AppRoute.Library> {
                        LibraryScreen(
                            onNavigateToAnimeDetails = {
                                updateBackStack(NavigationMutation.PUSH) {
                                    backStack.add(AppRoute.AnimeDetails)
                                }
                            }
                        )
                    }

                    entry<AppRoute.Profile> {
                        ProfileScreen(
                            onNavigateToSettings = {
                                updateBackStack(NavigationMutation.PUSH) {
                                    backStack.add(AppRoute.Settings)
                                }
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
        }
    }
}
