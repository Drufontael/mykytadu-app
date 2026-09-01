package br.com.mykytadu.core.navigation

object AppDeepLink {

    const val SCHEME = "mykytadu"
    const val HOST = "app"

    private const val BASE_URI = "$SCHEME://$HOST"

    fun resolve(uri: String): AppRoute? =
        when (uri.trimEnd('/')) {
            "$BASE_URI/home" -> AppRoute.Home
            "$BASE_URI/search" -> AppRoute.Search
            "$BASE_URI/library" -> AppRoute.Library
            "$BASE_URI/profile" -> AppRoute.Profile
            "$BASE_URI/settings" -> AppRoute.Settings
            else -> null
        }
}