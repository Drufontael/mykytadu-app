package br.com.mykytadu.web.navigation

import br.com.mykytadu.core.navigation.AppRoute
import br.com.mykytadu.core.navigation.NavigationHistoryBridge
import br.com.mykytadu.core.navigation.NavigationMutation

/** Hash routing keeps reload and direct links independent of server-side SPA rewrites. */
internal object WebRouteCodec {
    private const val FALLBACK_FRAGMENT = "#/splash"

    fun encode(route: AppRoute): String =
        when (route) {
            AppRoute.Splash -> "#/splash"
            AppRoute.Login -> "#/login"
            AppRoute.Home -> "#/home"
            AppRoute.Search -> "#/search"
            AppRoute.AnimeDetails -> "#/anime-details"
            AppRoute.Library -> "#/library"
            AppRoute.Profile -> "#/profile"
            AppRoute.Settings -> "#/settings"
        }

    fun decode(fragment: String?): WebRouteResolution {
        val route = when (fragment?.trim()) {
            "#/splash" -> AppRoute.Splash
            "#/login" -> AppRoute.Login
            "#/home" -> AppRoute.Home
            "#/search" -> AppRoute.Search
            "#/anime-details" -> AppRoute.AnimeDetails
            "#/library" -> AppRoute.Library
            "#/profile" -> AppRoute.Profile
            "#/settings" -> AppRoute.Settings
            else -> null
        }

        if (route == null) {
            return WebRouteResolution(
                backStack = listOf(AppRoute.Splash),
                canonicalFragment = FALLBACK_FRAGMENT,
                isCanonical = false,
            )
        }

        return WebRouteResolution(
            backStack = route.toInitialBackStack(),
            canonicalFragment = encode(route),
            isCanonical = fragment == encode(route),
        )
    }

    private fun AppRoute.toInitialBackStack(): List<AppRoute> =
        when (this) {
            AppRoute.AnimeDetails -> listOf(AppRoute.Search, AppRoute.AnimeDetails)
            AppRoute.Settings -> listOf(AppRoute.Profile, AppRoute.Settings)
            else -> listOf(this)
        }
}

internal data class WebRouteResolution(
    val backStack: List<AppRoute>,
    val canonicalFragment: String,
    val isCanonical: Boolean,
)

internal interface WebBrowserHistoryPort {
    fun currentFragment(): String

    fun push(fragment: String)

    fun replace(fragment: String)

    fun goBack()

    fun addLocationChangeListener(onLocationChange: () -> Unit): () -> Unit
}

/**
 * Owns the browser-specific policy. Its only inputs and outputs are routes, fragments and
 * history commands, keeping it directly testable without a DOM.
 */
internal class WebNavigationHistoryController(
    private val history: WebBrowserHistoryPort,
) : NavigationHistoryBridge {
    private var entries = mutableListOf<String>()
    private var cursor = 0
    private var browserNavigation: ((List<AppRoute>) -> Unit)? = null
    private var removeListener: (() -> Unit)? = null

    private val initialResolution = history.currentFragment().let(WebRouteCodec::decode)
    private var lastObservedFragment = initialResolution.canonicalFragment

    override val initialBackStack: List<AppRoute> = initialResolution.backStack

    init {
        entries += initialResolution.canonicalFragment
        if (!initialResolution.isCanonical) {
            history.replace(initialResolution.canonicalFragment)
        }
    }

    override fun bind(onBrowserNavigation: (List<AppRoute>) -> Unit): () -> Unit {
        check(browserNavigation == null) { "Web navigation history is already bound." }

        browserNavigation = onBrowserNavigation
        removeListener = history.addLocationChangeListener(::restoreFromBrowser)

        return {
            removeListener?.invoke()
            removeListener = null
            browserNavigation = null
        }
    }

    override fun onAppNavigation(backStack: List<AppRoute>, mutation: NavigationMutation) {
        val fragment = WebRouteCodec.encode(backStack.last())
        if (entries[cursor] == fragment) return

        when (mutation) {
            NavigationMutation.PUSH -> {
                entries = entries.take(cursor + 1).toMutableList()
                entries += fragment
                cursor = entries.lastIndex
                lastObservedFragment = fragment
                history.push(fragment)
            }

            NavigationMutation.REPLACE -> {
                entries[cursor] = fragment
                lastObservedFragment = fragment
                history.replace(fragment)
            }
        }
    }

    override fun requestBrowserBack(): Boolean {
        if (cursor == 0) return false

        history.goBack()
        return true
    }

    private fun restoreFromBrowser() {
        val resolution = WebRouteCodec.decode(history.currentFragment())
        if (resolution.canonicalFragment == lastObservedFragment) return

        lastObservedFragment = resolution.canonicalFragment
        if (!resolution.isCanonical) {
            history.replace(resolution.canonicalFragment)
        }

        val existingIndex = entries.neighbouringIndexOf(resolution.canonicalFragment, cursor)
        if (existingIndex >= 0) {
            cursor = existingIndex
        } else {
            entries = mutableListOf(resolution.canonicalFragment)
            cursor = 0
        }

        browserNavigation?.invoke(resolution.backStack)
    }

    private fun List<String>.neighbouringIndexOf(fragment: String, currentIndex: Int): Int =
        when {
            currentIndex > 0 && get(currentIndex - 1) == fragment -> currentIndex - 1
            currentIndex < lastIndex && get(currentIndex + 1) == fragment -> currentIndex + 1
            else -> indexOfLast { it == fragment }
        }
}

internal expect fun createWebBrowserHistoryPort(): WebBrowserHistoryPort
