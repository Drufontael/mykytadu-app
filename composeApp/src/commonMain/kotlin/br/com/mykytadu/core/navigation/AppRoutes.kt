package br.com.mykytadu.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {

    @Serializable
    data object Splash : AppRoute

    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Search : AppRoute

    @Serializable
    data object AnimeDetails : AppRoute

    @Serializable
    data object Library : AppRoute

    @Serializable
    data object Profile : AppRoute

    @Serializable
    data object Settings : AppRoute
}