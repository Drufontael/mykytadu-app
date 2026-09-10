@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package br.com.mykytadu.web.navigation

import kotlinx.browser.window
import org.w3c.dom.events.Event

internal actual fun createWebBrowserHistoryPort(): WebBrowserHistoryPort =
    WasmWebBrowserHistoryPort

private object WasmWebBrowserHistoryPort : WebBrowserHistoryPort {
    override fun currentFragment(): String = window.location.hash

    override fun push(fragment: String) {
        window.history.pushState(null, "", fragment)
    }

    override fun replace(fragment: String) {
        window.history.replaceState(null, "", fragment)
    }

    override fun goBack() {
        window.history.back()
    }

    override fun addLocationChangeListener(onLocationChange: () -> Unit): () -> Unit {
        val listener: (Event) -> Unit = { onLocationChange() }
        window.addEventListener("popstate", listener)
        window.addEventListener("hashchange", listener)

        return {
            window.removeEventListener("popstate", listener)
            window.removeEventListener("hashchange", listener)
        }
    }
}
