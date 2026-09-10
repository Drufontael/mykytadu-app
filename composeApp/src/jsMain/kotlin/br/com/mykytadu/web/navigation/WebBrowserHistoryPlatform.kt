package br.com.mykytadu.web.navigation

import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

internal actual fun createWebBrowserHistoryPort(): WebBrowserHistoryPort =
    JsWebBrowserHistoryPort

private object JsWebBrowserHistoryPort : WebBrowserHistoryPort {
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
        val listener = EventListener { _: Event -> onLocationChange() }
        window.addEventListener("popstate", listener)
        window.addEventListener("hashchange", listener)

        return {
            window.removeEventListener("popstate", listener)
            window.removeEventListener("hashchange", listener)
        }
    }
}
