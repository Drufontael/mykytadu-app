package br.com.mykytadu.core.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppDeepLinkTest {

    @Test
    fun shouldResolveKnownDeepLinks() {
        assertEquals(AppRoute.Home, AppDeepLink.resolve("mykytadu://app/home"))
        assertEquals(AppRoute.Search, AppDeepLink.resolve("mykytadu://app/search"))
        assertEquals(AppRoute.Library, AppDeepLink.resolve("mykytadu://app/library"))
        assertEquals(AppRoute.Profile, AppDeepLink.resolve("mykytadu://app/profile"))
        assertEquals(AppRoute.Settings, AppDeepLink.resolve("mykytadu://app/settings"))
    }

    @Test
    fun shouldAcceptTrailingSlash() {
        assertEquals(
            AppRoute.Home,
            AppDeepLink.resolve("mykytadu://app/home/")
        )
    }

    @Test
    fun shouldReturnNullForUnknownDeepLink() {
        assertNull(
            AppDeepLink.resolve("mykytadu://app/unknown")
        )
    }
}