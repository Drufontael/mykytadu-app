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
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.repository.AuthRepository
import org.koin.mp.KoinPlatformTools
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@OptIn(ExperimentalSerializationApi::class)
internal val navigationConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclassesOfSealed<AppRoute>()
        }
    }
}

@Composable
fun AppNavigation(
    navigationHistoryBridge: NavigationHistoryBridge? = null,
    authRepository: AuthRepository? = null,
) {
    val repository = authRepository ?: KoinPlatformTools.defaultContext().getOrNull()?.getOrNull<AuthRepository>()
    val backStack = rememberNavBackStack(
        navigationConfig,
        *(navigationHistoryBridge?.initialBackStack ?: listOf(AppRoute.Splash)).toTypedArray(),
    )
    val currentRoute = backStack.last() as AppRoute

    if (repository != null) {
        val authState by repository.authState.collectAsStateWithLifecycle()

        LaunchedEffect(currentRoute, authState) {
            if (authState is AuthState.Unauthenticated && currentRoute.access == RouteAccess.PROTECTED) {
                if (backStack.lastOrNull() != AppRoute.Login) {
                    backStack.clear()
                    backStack.add(AppRoute.Login)
                    navigationHistoryBridge?.onAppNavigation(listOf(AppRoute.Login), NavigationMutation.REPLACE)
                }
            } else if (authState is AuthState.Authenticated && currentRoute == AppRoute.Login) {
                if (backStack.lastOrNull() != AppRoute.Home) {
                    backStack.clear()
                    backStack.add(AppRoute.Home)
                    navigationHistoryBridge?.onAppNavigation(listOf(AppRoute.Home), NavigationMutation.REPLACE)
                }
            }
        }
    }

    fun updateBackStack(
        mutation: NavigationMutation,
        update: () -> Unit,
    ) {
        update()
        navigationHistoryBridge?.onAppNavigation(backStack.map { it as AppRoute }, mutation)
    }

    fun navigateBack() {
        if (backStack.size <= 1) return
        if (navigationHistoryBridge == null || !navigationHistoryBridge.requestBrowserBack()) {
            updateBackStack(NavigationMutation.REPLACE) {
                backStack.removeLastOrNull()
            }
        }
    }

    DisposableEffect(navigationHistoryBridge) {
        val dispose = navigationHistoryBridge?.bind { restoredBackStack ->
            backStack.restoreRoutes(restoredBackStack)
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
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                onBack = ::navigateBack,
                entryProvider = entryProvider {
                    entry<AppRoute.Splash> {
                        SplashScreen(
                            onNavigateToLogin = {
                                updateBackStack(NavigationMutation.REPLACE) {
                                    backStack.clear()
                                    backStack.add(AppRoute.Login)
                                }
                            },
                            onNavigateToHome = {
                                updateBackStack(NavigationMutation.REPLACE) {
                                    backStack.clear()
                                    backStack.add(AppRoute.Home)
                                }
                            },
                            authRepository = repository,
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
                        HomeScreen(
                            onAnimeSelected = { id ->
                                val route = AppRoute.AnimeDetails(id)
                                if (backStack.lastOrNull() == AppRoute.Home) {
                                    updateBackStack(NavigationMutation.PUSH) {
                                        backStack.add(route)
                                    }
                                }
                            },
                            onNavigateToSearch = {
                                onMainDestination(AppRoute.Search)
                            },
                        )
                    }

                    entry<AppRoute.Search> {
                        SearchScreen(
                            isCurrentRoute = currentRoute == AppRoute.Search,
                            onAnimeSelected = { id ->
                                val route = AppRoute.AnimeDetails(id)
                                if (backStack.lastOrNull() == AppRoute.Search) {
                                    updateBackStack(NavigationMutation.PUSH) {
                                        backStack.add(route)
                                    }
                                }
                            },
                        )
                    }

                    entry<AppRoute.AnimeDetails> { route ->
                        AnimeDetailsScreen(
                            id = route.id,
                            onBack = ::navigateBack,
                            onNavigateToAnime = { relatedId ->
                                if (backStack.lastOrNull() == route && relatedId != route.id) {
                                    updateBackStack(NavigationMutation.PUSH) {
                                        backStack.add(AppRoute.AnimeDetails(relatedId))
                                    }
                                }
                            },
                        )
                    }

                    entry<AppRoute.Library> {
                        LibraryScreen(
                            onAnimeSelected = { id ->
                                val route = AppRoute.AnimeDetails(id)
                                if (backStack.lastOrNull() == AppRoute.Library) {
                                    updateBackStack(NavigationMutation.PUSH) {
                                        backStack.add(route)
                                    }
                                }
                            },
                            onNavigateToSearch = {
                                onMainDestination(AppRoute.Search)
                            },
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
                        SettingsScreen(
                            onNavigateBack = ::navigateBack,
                        )
                    }
                }
                        )
                    }
                }
            }
        }
    }
}
