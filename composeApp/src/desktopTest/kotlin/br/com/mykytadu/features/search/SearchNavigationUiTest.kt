package br.com.mykytadu.features.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import br.com.mykytadu.App
import br.com.mykytadu.core.navigation.AppRoute
import br.com.mykytadu.core.navigation.NavigationHistoryBridge
import br.com.mykytadu.core.navigation.NavigationMutation
import br.com.mykytadu.domain.model.*
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryResult
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalTestApi::class)
class SearchNavigationUiTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `selection preserves ID viewmodel results and scroll across details and browser restoration`() {
        val repository = FakeRepository()
        val created = mutableListOf<SearchViewModel>()
        var cleared = 0
        val bridge = TestHistoryBridge()
        startKoin {
            modules(module {
                viewModel {
                    SearchViewModel(repository).also {
                        created += it
                        it.addCloseable { cleared++ }
                    }
                }
            })
        }
        try {
            compose.setContent { Box(Modifier.size(500.dp, 700.dp)) { App(bridge) } }
            compose.onNode(hasSetTextAction()).performTextInput("Example")
            compose.onNode(hasSetTextAction()).performImeAction()
            compose.waitUntil(5_000) { created.single().uiState.value.content is SearchContent.Results }
            compose.waitForIdle()
            val originalViewModel = created.single()
            val originalResults = originalViewModel.uiState.value.content
            compose.onNode(hasScrollToIndexAction()).performScrollToIndex(15)
            val item = compose.onNodeWithText("Example 15")
            item.assertIsDisplayed().assertHasClickAction()
            val originalBounds = item.fetchSemanticsNode().boundsInRoot
            item.performClick()
            compose.onNodeWithText("Detalhes do anime — ID AniList: 1015").assertIsDisplayed()
            assertEquals(AppRoute.AnimeDetails(AniListAnimeId(1015)), bridge.pushed.last().last())
            compose.onNodeWithText("Voltar à pesquisa").performClick()
            item.assertIsDisplayed()
            assertEquals(originalBounds, item.fetchSemanticsNode().boundsInRoot)
            assertSame(originalViewModel, created.single())
            assertSame(originalResults, created.single().uiState.value.content)
            assertEquals("Example", originalViewModel.uiState.value.queryText)
            assertEquals(1, repository.searches)
            assertEquals(0, cleared)

            // Real NavDisplay + decorators, driven by the same restoration callback as Web.
            compose.runOnIdle { bridge.restore(listOf(AppRoute.Search, AppRoute.AnimeDetails(AniListAnimeId(1016)))) }
            compose.onNodeWithText("Detalhes do anime — ID AniList: 1016").assertIsDisplayed()
            compose.runOnIdle { bridge.restore(listOf(AppRoute.Search)) }
            item.assertIsDisplayed()
            assertEquals(originalBounds, item.fetchSemanticsNode().boundsInRoot)
            assertSame(originalViewModel, created.single())
            assertEquals(1, repository.searches)

            // Keyboard activation exercises the actionable card, not a direct callback.
            item.performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.RequestFocus) { it() }
            item.performKeyInput { pressKey(Key.Enter) }
            compose.onNodeWithText("Detalhes do anime — ID AniList: 1015").assertIsDisplayed()
            compose.onNodeWithText("Voltar à pesquisa").performClick()
            compose.onNodeWithText("Biblioteca").performClick()
            compose.waitUntil(5_000) { cleared == 1 }
            compose.onNodeWithText("Buscar").performClick()
            compose.onNodeWithText("Encontre seu próximo anime").assertIsDisplayed()
            assertEquals(2, created.size)
            assertEquals(1, repository.searches)
        } finally {
            stopKoin()
        }
    }

    private class TestHistoryBridge : NavigationHistoryBridge {
        override val initialBackStack = listOf(AppRoute.Search)
        val pushed = mutableListOf<List<AppRoute>>()
        private var listener: ((List<AppRoute>) -> Unit)? = null
        override fun bind(onBrowserNavigation: (List<AppRoute>) -> Unit): () -> Unit {
            listener = onBrowserNavigation
            return { listener = null }
        }
        override fun onAppNavigation(backStack: List<AppRoute>, mutation: NavigationMutation) {
            if (mutation == NavigationMutation.PUSH) pushed += backStack
        }
        override fun requestBrowserBack() = false
        fun restore(routes: List<AppRoute>) = requireNotNull(listener)(routes)
    }

    private class FakeRepository : AnimeRepository {
        var searches = 0
        override suspend fun searchAnime(query: String, page: Int, perPage: Int): RepositoryResult<PagedResult<AnimeSummary>> {
            searches++
            return RepositoryResult.Success(PagedResult(
                items = (0 until 20).map { AnimeSummary(id = AniListAnimeId(1000 + it), titles = AnimeTitles(english = "Example $it")) },
                pageInfo = PageInfo(currentPage = 1, lastPage = 1, hasNextPage = false, perPage = 20, total = 20),
            ))
        }
        override suspend fun getAnimeDetails(id: AniListAnimeId): RepositoryResult<AnimeDetails> =
            error("The details placeholder must not fetch details.")
    }
}
