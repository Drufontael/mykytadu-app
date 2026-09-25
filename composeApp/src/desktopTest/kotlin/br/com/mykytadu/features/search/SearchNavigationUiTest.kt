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
import br.com.mykytadu.features.anime.AnimeDetailsViewModel
import br.com.mykytadu.domain.model.*
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlinx.coroutines.CompletableDeferred
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

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
                viewModel { parameters ->
                    AnimeDetailsViewModel(parameters.get(), repository)
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
            val firstResultBounds = compose.onNodeWithText("Example 0").fetchSemanticsNode().boundsInRoot
            val secondResultBounds = compose.onNodeWithText("Example 1").fetchSemanticsNode().boundsInRoot
            assertEquals(firstResultBounds.top, secondResultBounds.top)
            assertTrue(firstResultBounds.left < secondResultBounds.left)
            compose.onNode(hasScrollToIndexAction()).performScrollToIndex(15)
            val item = compose.onNodeWithText("Example 15")
            item.assertIsDisplayed().assertHasClickAction()
            val originalBounds = item.fetchSemanticsNode().boundsInRoot
            item.performClick()
            compose.onNodeWithText("Example 15").assertIsDisplayed()
            assertEquals(AppRoute.AnimeDetails(CatalogAnimeId("1015")), bridge.pushed.last().last())
            compose.onNodeWithContentDescription("Voltar à pesquisa").performClick()
            item.assertIsDisplayed()
            assertEquals(originalBounds, item.fetchSemanticsNode().boundsInRoot)
            assertSame(originalViewModel, created.single())
            assertSame(originalResults, created.single().uiState.value.content)
            assertEquals("Example", originalViewModel.uiState.value.queryText)
            assertEquals(1, repository.searches)
            assertEquals(0, cleared)

            // Real NavDisplay + decorators, driven by the same restoration callback as Web.
            compose.runOnIdle { bridge.restore(listOf(AppRoute.Search, AppRoute.AnimeDetails(CatalogAnimeId("1016")))) }
            compose.onNodeWithText("Example 16").assertIsDisplayed()
            compose.runOnIdle { bridge.restore(listOf(AppRoute.Search)) }
            item.assertIsDisplayed()
            assertEquals(originalBounds, item.fetchSemanticsNode().boundsInRoot)
            assertSame(originalViewModel, created.single())
            assertEquals(1, repository.searches)

            // Keyboard activation exercises the actionable card, not a direct callback.
            item.performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.RequestFocus) { it() }
            item.performKeyInput { pressKey(Key.Enter) }
            compose.onNodeWithText("Example 15").assertIsDisplayed()
            compose.onNodeWithContentDescription("Voltar à pesquisa").performClick()
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

    @Test
    fun `grade carrega pagina seguinte e exibe erro e retry no rodape`() {
        val repository = IncrementalFakeRepository()
        val created = mutableListOf<SearchViewModel>()
        startKoin {
            modules(module {
                viewModel {
                    SearchViewModel(repository).also { created += it }
                }
            })
        }
        try {
            compose.setContent { Box(Modifier.size(500.dp, 700.dp)) { App(TestHistoryBridge()) } }
            compose.onNode(hasSetTextAction()).performTextInput("Example")
            compose.onNode(hasSetTextAction()).performImeAction()

            compose.waitUntil(5_000) {
                created.singleOrNull()?.uiState?.value?.content is SearchContent.Results
            }
            compose.onNode(hasScrollToIndexAction()).performScrollToIndex(18)
            compose.waitUntil(5_000) { repository.calls.count { it.page == 2 } == 1 }
            compose.onNodeWithContentDescription("Carregando mais resultados").assertExists()

            compose.runOnIdle {
                repository.firstNextPage.complete(
                    RepositoryResult.Failure(RepositoryFailure.Timeout),
                )
            }
            compose.waitUntil(5_000) {
                (created.single().uiState.value.content as? SearchContent.Results)?.pagination is
                    SearchPaginationState.Failure
            }
            compose.waitForIdle()
            compose.onNode(hasScrollToIndexAction()).performScrollToIndex(20)
            compose.onNodeWithText("Não foi possível carregar mais resultados")
                .assertExists()
                .assertIsDisplayed()
            compose.onNodeWithText("Tentar novamente")
                .performClick()

            compose.waitUntil(5_000) {
                repository.calls.count { it.page == 2 } == 2 &&
                    (created.single().uiState.value.content as? SearchContent.Results)?.pagination is
                    SearchPaginationState.Loading
            }
            compose.waitForIdle()
            compose.onNodeWithContentDescription("Carregando mais resultados").assertExists()

            compose.runOnIdle {
                repository.retryNextPage.complete(
                    pagedSuccess(2, false, testAnime(21, "Example 21")),
                )
            }
            compose.waitUntil(5_000) {
                val results = created.single().uiState.value.content as? SearchContent.Results
                results?.pageInfo?.currentPage == 2 && results.pagination is SearchPaginationState.Idle
            }
            compose.waitForIdle()
            compose.onNode(hasScrollToIndexAction()).performScrollToIndex(20)
            compose.onNodeWithText("Example 21").assertIsDisplayed()
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
                items = (0 until 20).map { AnimeSummary(id = CatalogAnimeId((1000 + it).toString()), titles = AnimeTitles(english = "Example $it")) },
                pageInfo = PageInfo(currentPage = 1, lastPage = 1, hasNextPage = false, perPage = 20, total = 20),
            ))
        }
        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> =
            RepositoryResult.Success(
                AnimeDetails(
                    id = id,
                    titles = AnimeTitles(
                        english = "Example ${id.value.toInt() - 1000}",
                    ),
                ),
            )
    }

    private class IncrementalFakeRepository : AnimeRepository {
        val calls = mutableListOf<SearchCall>()
        val firstNextPage = CompletableDeferred<RepositoryResult<PagedResult<AnimeSummary>>>()
        val retryNextPage = CompletableDeferred<RepositoryResult<PagedResult<AnimeSummary>>>()
        private var nextPageAttempts = 0

        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int,
        ): RepositoryResult<PagedResult<AnimeSummary>> {
            calls += SearchCall(query, page, perPage)
            return when (page) {
                1 -> pagedSuccess(
                    1,
                    true,
                    *((1..20).map { testAnime(it, "Example $it") }).toTypedArray(),
                )

                2 -> if (nextPageAttempts++ == 0) firstNextPage.await() else retryNextPage.await()
                else -> error("Página inesperada: $page")
            }
        }

        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> =
            RepositoryResult.Success(AnimeDetails(id = id))
    }
}

private data class SearchCall(
    val query: String,
    val page: Int,
    val perPage: Int,
)

private fun testAnime(id: Int, title: String): AnimeSummary =
    AnimeSummary(
        id = CatalogAnimeId(id.toString()),
        titles = AnimeTitles(english = title),
    )

private fun pagedSuccess(
    page: Int,
    hasNextPage: Boolean,
    vararg items: AnimeSummary,
): RepositoryResult<PagedResult<AnimeSummary>> =
    RepositoryResult.Success(
        PagedResult(
            items = items.toList(),
            pageInfo = PageInfo(
                currentPage = page,
                lastPage = if (hasNextPage) page + 1 else page,
                hasNextPage = hasNextPage,
                perPage = 20,
                total = items.size,
            ),
        ),
    )
