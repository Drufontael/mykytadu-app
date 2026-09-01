package br.com.mykytadu.core.navigation

enum class RouteAccess {
    PUBLIC,
    PROTECTED
}

val AppRoute.access: RouteAccess
    get() = when (this) {
        AppRoute.Splash,
        AppRoute.Login -> RouteAccess.PUBLIC

        AppRoute.Home,
        AppRoute.Search,
        AppRoute.AnimeDetails,
        AppRoute.Library,
        AppRoute.Profile,
        AppRoute.Settings -> RouteAccess.PROTECTED
    }