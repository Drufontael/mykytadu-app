package br.com.mykytadu.core.navigation

import androidx.navigation3.runtime.NavKey
import br.com.mykytadu.domain.model.AniListAnimeId
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class AnimeDetailsRouteTest {
    private val json = Json { serializersModule = navigationConfig.serializersModule }
    private val stackSerializer = ListSerializer(PolymorphicSerializer(NavKey::class))

    @Test
    fun `polymorphic back stack preserves distinct details and positive Int limits`() {
        val stack = listOf<NavKey>(AppRoute.Search) + listOf(1, 20, Int.MAX_VALUE).map {
            AppRoute.AnimeDetails(AniListAnimeId(it))
        }
        assertEquals(stack, json.decodeFromString(stackSerializer, json.encodeToString(stackSerializer, stack)))
        assertNotEquals(stack[1], stack[2])
    }

    @Test
    fun `route deserialization refuses missing non positive and overflow IDs`() {
        listOf("{}", "{\"id\":0}", "{\"id\":-1}", "{\"id\":2147483648}", "{\"id\":\"bad\"}").forEach {
            assertFailsWith<IllegalArgumentException> {
                json.decodeFromString(AppRoute.AnimeDetails.serializer(), it)
            }
        }
    }

    @Test
    fun `shared deep link accepts decimal IDs and leading zeroes`() {
        listOf("1" to 1, "00020" to 20, "2147483647" to Int.MAX_VALUE).forEach { (text, value) ->
            assertEquals(AppRoute.AnimeDetails(AniListAnimeId(value)), AppDeepLink.resolve("mykytadu://app/anime/$text"))
        }
    }

    @Test
    fun `shared deep link refuses incomplete and malformed details`() {
        listOf("anime", "anime/", "anime/0", "anime/-1", "anime/+1", "anime/2147483648",
            "anime/abc", "anime/20/extra", "anime/20/", "anime/20?x=1", "anime/%32%30", "anime-details",
        ).forEach { assertNull(AppDeepLink.resolve("mykytadu://app/$it"), it) }
        assertNull(AppDeepLink.resolve("https://app/anime/20"))
    }

    @Test
    fun `access and main destination classification stay independent of details ID`() {
        assertEquals(RouteAccess.PUBLIC, AppRoute.Splash.access)
        assertEquals(RouteAccess.PUBLIC, AppRoute.Login.access)
        MainDestination.entries.forEach { assertEquals(RouteAccess.PROTECTED, it.route.access) }
        listOf(1, 20, Int.MAX_VALUE).forEach { id ->
            val route = AppRoute.AnimeDetails(AniListAnimeId(id))
            assertEquals(RouteAccess.PROTECTED, route.access)
            assertEquals(false, MainDestination.entries.any { it.route == route })
        }
        assertEquals(RouteAccess.PROTECTED, AppRoute.Settings.access)
    }

    @Test
    fun `restoration retains prefix and differentiates IDs instead of route types`() {
        val first = AppRoute.AnimeDetails(AniListAnimeId(20))
        val second = AppRoute.AnimeDetails(AniListAnimeId(21))
        val removed = mutableListOf<NavKey>()
        val backing = mutableListOf<NavKey>()
        val stack = object : MutableList<NavKey> by backing {
            override fun removeAt(index: Int): NavKey = backing.removeAt(index).also(removed::add)
        }
        stack.addAll(listOf(AppRoute.Search, first))
        stack.restoreRoutes(listOf(AppRoute.Search, second))
        assertEquals(listOf<NavKey>(first), removed)
        assertContentEquals(listOf<NavKey>(AppRoute.Search, second), stack)
        stack.restoreRoutes(listOf(AppRoute.Search, second))
        assertEquals(listOf<NavKey>(first), removed)
        stack.restoreRoutes(listOf(AppRoute.Search))
        assertEquals(listOf<NavKey>(first, second), removed)
        assertContentEquals(listOf<NavKey>(AppRoute.Search), stack)
    }
}
