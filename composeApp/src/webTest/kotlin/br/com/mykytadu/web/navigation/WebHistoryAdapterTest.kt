package br.com.mykytadu.web.navigation

import kotlinx.browser.window
import kotlin.test.Test
import kotlin.test.assertEquals

class WebHistoryAdapterTest {
    @Test
    fun `real adapter preserves technical query parameters in push and replace`() {
        val original = window.location.href
        val query = "?network-smoke=true&design-system-showcase=true"
        val port = createWebBrowserHistoryPort()
        try {
            window.history.replaceState(null, "", "$query#/search")
            port.push("#/anime/20")
            assertEquals(query, window.location.search)
            assertEquals("#/anime/20", port.currentFragment())
            port.replace("#/anime/21")
            assertEquals(query, window.location.search)
            assertEquals("#/anime/21", port.currentFragment())
        } finally {
            window.history.replaceState(null, "", original)
        }
    }
}
