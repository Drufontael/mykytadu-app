package br.com.mykytadu.core.navigation

enum class MainDestination(
    val label: String,
    val route: AppRoute
) {
    HOME(
        label = "Início",
        route = AppRoute.Home
    ),
    LIBRARY(
        label = "Biblioteca",
        route = AppRoute.Library
    ),
    SEARCH(
        label = "Buscar",
        route = AppRoute.Search
    ),
    PROFILE(
        label = "Perfil",
        route = AppRoute.Profile
    )
}