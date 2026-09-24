package br.com.mykytadu.web.navigation

import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.core.navigation.AppRoute
import br.com.mykytadu.core.navigation.NavigationMutation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebNavigationHistoryTest {

    @Test
    fun `codec serializa todas as rotas sem introduzir parametros`() {
        assertEquals("#/splash", WebRouteCodec.encode(AppRoute.Splash))
        assertEquals("#/login", WebRouteCodec.encode(AppRoute.Login))
        assertEquals("#/home", WebRouteCodec.encode(AppRoute.Home))
        assertEquals("#/search", WebRouteCodec.encode(AppRoute.Search))
        assertEquals("#/anime/20", WebRouteCodec.encode(AppRoute.AnimeDetails(CatalogAnimeId("20"))))
        assertEquals("#/library", WebRouteCodec.encode(AppRoute.Library))
        assertEquals("#/profile", WebRouteCodec.encode(AppRoute.Profile))
        assertEquals("#/settings", WebRouteCodec.encode(AppRoute.Settings))
    }

    @Test
    fun `codec reconstrui cada fragmento valido`() {
        assertEquals(listOf(AppRoute.Splash), WebRouteCodec.decode("#/splash").backStack)
        assertEquals(listOf(AppRoute.Login), WebRouteCodec.decode("#/login").backStack)
        assertEquals(listOf(AppRoute.Home), WebRouteCodec.decode("#/home").backStack)
        assertEquals(listOf(AppRoute.Search), WebRouteCodec.decode("#/search").backStack)
        assertEquals(listOf(AppRoute.Search, AppRoute.AnimeDetails(CatalogAnimeId("20"))), WebRouteCodec.decode("#/anime/20").backStack)
        assertEquals(listOf(AppRoute.Library), WebRouteCodec.decode("#/library").backStack)
        assertEquals(listOf(AppRoute.Profile), WebRouteCodec.decode("#/profile").backStack)
        assertEquals(listOf(AppRoute.Profile, AppRoute.Settings), WebRouteCodec.decode("#/settings").backStack)
    }

    @Test
    fun `fragmento vazio desconhecido ou com parametro inexistente usa splash canonico`() {
        listOf("", "#", "#/unknown", "#/anime/invalid", "#/search?query=naruto").forEach { fragment ->
            val resolution = WebRouteCodec.decode(fragment)

            assertEquals(listOf(AppRoute.Splash), resolution.backStack)
            assertEquals("#/splash", resolution.canonicalFragment)
            assertFalse(resolution.isCanonical)
        }
    }

    @Test
    fun `inicializacao canonicaliza hash vazio sem criar entrada`() {
        val port = FakeHistoryPort("")
        val controller = WebNavigationHistoryController(port)

        assertEquals(listOf(AppRoute.Splash), controller.initialBackStack)
        assertEquals(listOf("#/splash"), port.replaced)
        assertTrue(port.pushed.isEmpty())
    }

    @Test
    fun `navegacao interna cria uma entrada e destino repetido nao polui historico`() {
        val port = FakeHistoryPort("#/home")
        val controller = WebNavigationHistoryController(port)

        controller.onAppNavigation(listOf(AppRoute.Search), NavigationMutation.PUSH)
        controller.onAppNavigation(listOf(AppRoute.Search), NavigationMutation.PUSH)

        assertEquals(listOf("#/search"), port.pushed)
        assertTrue(port.replaced.isEmpty())
    }

    @Test
    fun `transicao que limpa pilha substitui entrada atual`() {
        val port = FakeHistoryPort("#/splash")
        val controller = WebNavigationHistoryController(port)

        controller.onAppNavigation(listOf(AppRoute.Login), NavigationMutation.REPLACE)
        controller.onAppNavigation(listOf(AppRoute.Home), NavigationMutation.REPLACE)

        assertEquals(listOf("#/login", "#/home"), port.replaced)
        assertTrue(port.pushed.isEmpty())
    }

    @Test
    fun `popstate restaura detalhe e nao cria novo push`() {
        val port = FakeHistoryPort("#/home")
        val controller = WebNavigationHistoryController(port)
        val restored = mutableListOf<List<AppRoute>>()
        val dispose = controller.bind(restored::add)

        controller.onAppNavigation(listOf(AppRoute.Search), NavigationMutation.PUSH)
        controller.onAppNavigation(listOf(AppRoute.Search, AppRoute.AnimeDetails(CatalogAnimeId("20"))), NavigationMutation.PUSH)
        port.emit("#/search")
        port.emit("#/search")

        assertEquals(listOf(AppRoute.Search), restored.single())
        assertEquals(listOf("#/search", "#/anime/20"), port.pushed)
        assertTrue(port.replaced.isEmpty())
        dispose()
    }

    @Test
    fun `back e forward do navegador restauram rotas sucessivas sem ciclo`() {
        val port = FakeHistoryPort("#/home")
        val controller = WebNavigationHistoryController(port)
        val restored = mutableListOf<List<AppRoute>>()
        controller.bind(restored::add)

        controller.onAppNavigation(listOf(AppRoute.Search), NavigationMutation.PUSH)
        controller.onAppNavigation(listOf(AppRoute.Search, AppRoute.AnimeDetails(CatalogAnimeId("20"))), NavigationMutation.PUSH)
        port.emit("#/search")
        port.emit("#/anime/20")

        val expected: List<List<AppRoute>> = listOf(
                listOf(AppRoute.Search),
                listOf(AppRoute.Search, AppRoute.AnimeDetails(CatalogAnimeId("20"))),
            )
        assertEquals(expected, restored)
        assertEquals(2, port.pushed.size)
    }

    @Test
    fun `restauracao escolhe a entrada adjacente quando a mesma rota se repete`() {
        val port = FakeHistoryPort("#/home")
        val controller = WebNavigationHistoryController(port)
        val restored = mutableListOf<List<AppRoute>>()
        controller.bind(restored::add)

        controller.onAppNavigation(listOf(AppRoute.Search), NavigationMutation.PUSH)
        controller.onAppNavigation(listOf(AppRoute.Home), NavigationMutation.PUSH)
        controller.onAppNavigation(listOf(AppRoute.Search), NavigationMutation.PUSH)
        port.emit("#/home")
        port.emit("#/search")
        port.emit("#/home")
        port.emit("#/search")

        val expected: List<List<AppRoute>> = listOf(
                listOf(AppRoute.Home),
                listOf(AppRoute.Search),
                listOf(AppRoute.Home),
                listOf(AppRoute.Search),
            )
        assertEquals(expected, restored)
    }

    @Test
    fun `voltar pelo aplicativo delega ao browser quando existe entrada gerenciada`() {
        val port = FakeHistoryPort("#/home")
        val controller = WebNavigationHistoryController(port)
        controller.bind { }
        controller.onAppNavigation(listOf(AppRoute.Search), NavigationMutation.PUSH)

        assertTrue(controller.requestBrowserBack())
        assertEquals(1, port.backRequests)

        port.emit("#/home")
        assertFalse(controller.requestBrowserBack())
    }

    @Test
    fun `hash desconhecido durante restauracao e substituido pelo fallback`() {
        val port = FakeHistoryPort("#/home")
        val controller = WebNavigationHistoryController(port)
        val restored = mutableListOf<List<AppRoute>>()
        controller.bind(restored::add)

        port.emit("#/not-found")

        assertEquals(listOf("#/splash"), port.replaced)
        assertEquals(listOf(AppRoute.Splash), restored.single())
    }

    @Test
    fun `query de diagnostico permanece fora do codec de fragmentos`() {
        val resolution = WebRouteCodec.decode("#/search")

        assertTrue(resolution.isCanonical)
        assertEquals(listOf(AppRoute.Search), resolution.backStack)
    }

    @Test
    fun `descarte remove listeners do browser`() {
        val port = FakeHistoryPort("#/home")
        val controller = WebNavigationHistoryController(port)
        val restored = mutableListOf<List<AppRoute>>()
        val dispose = controller.bind(restored::add)

        dispose()
        port.emit("#/search")

        assertTrue(restored.isEmpty())
        assertEquals(1, port.removedListeners)
    }

    @Test
    fun `details IDs canonicalize and invalid IDs keep general fallback`() {
        listOf("1" to 1, "00020" to 20, "2147483647" to Int.MAX_VALUE).forEach { (text, id) ->
            val result = WebRouteCodec.decode("#/anime/$text")
            assertEquals(listOf(AppRoute.Search, AppRoute.AnimeDetails(CatalogAnimeId(id.toString()))), result.backStack)
            assertEquals("#/anime/$id", result.canonicalFragment)
            assertEquals(text == id.toString(), result.isCanonical)
        }
        listOf("#/anime", "#/anime/", "#/anime/0", "#/anime/-1", "#/anime/+1",
            "#/anime/2147483648", "#/anime/no", "#/anime/20/extra", "#/anime/20/",
            "#/anime/20?x=1", "#/anime/%32%30", "#/anime/ 20", "/anime/20",
        ).forEach {
            assertEquals(listOf(AppRoute.Splash), WebRouteCodec.decode(it).backStack, it)
        }
    }

    @Test
    fun `legacy details URL becomes search without fabricating ID or push`() {
        val port = FakeHistoryPort("#/anime-details")
        val controller = WebNavigationHistoryController(port)
        assertEquals(listOf(AppRoute.Search), controller.initialBackStack)
        assertEquals(listOf("#/search"), port.replaced)
        assertTrue(port.pushed.isEmpty())
    }

    @Test
    fun `two anime IDs survive back forward duplicate events and reload`() {
        val port = FakeHistoryPort("#/search")
        val controller = WebNavigationHistoryController(port)
        val restored = mutableListOf<List<AppRoute>>()
        controller.bind(restored::add)
        val first = listOf(AppRoute.Search, AppRoute.AnimeDetails(CatalogAnimeId("20")))
        val second = listOf(AppRoute.Search, AppRoute.AnimeDetails(CatalogAnimeId("21")))
        controller.onAppNavigation(first, NavigationMutation.PUSH)
        controller.onAppNavigation(first, NavigationMutation.PUSH)
        port.emit("#/search")
        controller.onAppNavigation(second, NavigationMutation.PUSH)
        controller.onAppNavigation(second, NavigationMutation.PUSH)
        port.emit("#/search")
        port.emit("#/anime/21")
        port.emit("#/anime/21")
        assertEquals(listOf(listOf(AppRoute.Search), listOf(AppRoute.Search), second), restored)
        assertEquals(listOf("#/anime/20", "#/anime/21"), port.pushed)
        assertTrue(port.replaced.isEmpty())
        assertEquals(second, WebNavigationHistoryController(port).initialBackStack)
    }

    @Test
    fun `adjacent different details IDs remain separate history entries`() {
        val port = FakeHistoryPort("#/search")
        val controller = WebNavigationHistoryController(port)
        val restored = mutableListOf<List<AppRoute>>()
        controller.bind(restored::add)
        listOf(20, 21).forEach {
            controller.onAppNavigation(listOf(AppRoute.Search, AppRoute.AnimeDetails(CatalogAnimeId(it.toString()))), NavigationMutation.PUSH)
        }
        port.emit("#/anime/20")
        port.emit("#/anime/21")
        assertEquals(listOf("20", "21"), restored.map { (it.last() as AppRoute.AnimeDetails).id.value })
        assertEquals(2, port.pushed.size)
    }

    @Test
    fun `deep link without managed predecessor returns by replacing with search`() {
        val port = FakeHistoryPort("#/anime/20")
        val controller = WebNavigationHistoryController(port)
        assertFalse(controller.requestBrowserBack())
        controller.onAppNavigation(listOf(AppRoute.Search), NavigationMutation.REPLACE)
        assertEquals(listOf("#/search"), port.replaced)
        assertTrue(port.pushed.isEmpty())
    }

    @Test
    fun `equivalent hash and legacy hash are canonicalized even for current destination`() {
        val port = FakeHistoryPort("#/anime/20")
        val controller = WebNavigationHistoryController(port)
        val restored = mutableListOf<List<AppRoute>>()
        controller.bind(restored::add)
        port.emit("#/anime/00020")
        port.emit("#/anime/20")
        assertEquals(listOf("#/anime/20"), port.replaced)
        assertTrue(restored.isEmpty())
        controller.onAppNavigation(listOf(AppRoute.Search), NavigationMutation.REPLACE)
        port.emit("#/anime-details")
        assertEquals("#/search", port.replaced.last())
        assertTrue(restored.isEmpty())
    }

    private class FakeHistoryPort(
        private var fragment: String,
    ) : WebBrowserHistoryPort {
        val pushed = mutableListOf<String>()
        val replaced = mutableListOf<String>()
        var backRequests = 0
        var removedListeners = 0
        private val listeners = mutableListOf<() -> Unit>()

        override fun currentFragment(): String = fragment

        override fun push(fragment: String) {
            this.fragment = fragment
            pushed += fragment
        }

        override fun replace(fragment: String) {
            this.fragment = fragment
            replaced += fragment
        }

        override fun goBack() {
            backRequests++
        }

        override fun addLocationChangeListener(onLocationChange: () -> Unit): () -> Unit {
            listeners += onLocationChange
            return {
                if (listeners.remove(onLocationChange)) {
                    removedListeners++
                }
            }
        }

        fun emit(fragment: String) {
            this.fragment = fragment
            listeners.toList().forEach { it() }
        }
    }
}
