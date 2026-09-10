package br.com.mykytadu.core.navigation

/**
 * Optional boundary between the shared Navigation 3 back stack and a platform history.
 * Platforms that do not expose browser history leave this bridge absent.
 */
interface NavigationHistoryBridge {
    val initialBackStack: List<AppRoute>

    fun bind(onBrowserNavigation: (List<AppRoute>) -> Unit): () -> Unit

    fun onAppNavigation(
        backStack: List<AppRoute>,
        mutation: NavigationMutation,
    )

    /** Returns true when the platform will restore the back stack asynchronously. */
    fun requestBrowserBack(): Boolean
}

enum class NavigationMutation {
    PUSH,
    REPLACE,
}
