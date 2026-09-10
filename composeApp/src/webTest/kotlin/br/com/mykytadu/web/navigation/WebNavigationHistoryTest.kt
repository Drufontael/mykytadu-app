package br.com.mykytadu.web.navigation

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
        assertEquals("#/anime-details", WebRouteCodec.encode(AppRoute.AnimeDetails))
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
        assertEquals(listOf(AppRoute.Search, AppRoute.AnimeDetails), WebRouteCodec.decode("#/anime-details").backStack)
        assertEquals(listOf(AppRoute.Library), WebRouteCodec.decode("#/library").backStack)
        assertEquals(listOf(AppRoute.Profile), WebRouteCodec.decode("#/profile").backStack)
        assertEquals(listOf(AppRoute.Profile, AppRoute.Settings), WebRouteCodec.decode("#/settings").backStack)
    }

    @Test
    fun `fragmento vazio desconhecido ou com parametro inexistente usa splash canonico`() {
        listOf("", "#", "#/unknown", "#/anime/20", "#/search?query=naruto").forEach { fragment ->
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
        controller.onAppNavigation(listOf(AppRoute.Search, AppRoute.AnimeDetails), NavigationMutation.PUSH)
        port.emit("#/search")
        port.emit("#/search")

        assertEquals(listOf(AppRoute.Search), restored.single())
        assertEquals(listOf("#/search", "#/anime-details"), port.pushed)
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
        controller.onAppNavigation(listOf(AppRoute.Search, AppRoute.AnimeDetails), NavigationMutation.PUSH)
        port.emit("#/search")
        port.emit("#/anime-details")

        val expected: List<List<AppRoute>> = listOf(
                listOf(AppRoute.Search),
                listOf(AppRoute.Search, AppRoute.AnimeDetails),
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
