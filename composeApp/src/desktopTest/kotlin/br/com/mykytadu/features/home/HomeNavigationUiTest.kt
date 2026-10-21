package br.com.mykytadu.features.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.repository.DefaultLibraryRepository
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.domain.model.AnimeImages
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.model.PageInfo
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class HomeNavigationUiTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `clicar em obra de continuar assistindo na HomeScreen navega para detalhes com animeId correto`() {
        val libraryRepo = DefaultLibraryRepository(InMemoryLibraryLocalDataSource())
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("101"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 2, total = 12),
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 2000L,
        )
        runBlocking { libraryRepo.saveEntry(entry) }

        val fakeAnimeRepo = FakeAnimeRepository()
        val viewModel = HomeViewModel(libraryRepo, fakeAnimeRepo)

        var selectedAnimeId: CatalogAnimeId? = null

        compose.setContent {
            AppTheme {
                Box(Modifier.size(800.dp, 600.dp)) {
                    HomeScreen(
                        viewModel = viewModel,
                        onAnimeSelected = { selectedAnimeId = it },
                    )
                }
            }
        }

        compose.waitForIdle()
        compose.onNodeWithTag("home_continue_card_101").assertIsDisplayed()
        compose.onNodeWithTag("home_continue_card_101").performClick()

        assertEquals(CatalogAnimeId("101"), selectedAnimeId)
    }

    @Test
    fun `clicar em obra em alta na HomeScreen navega para detalhes com animeId correto`() {
        val libraryRepo = DefaultLibraryRepository(InMemoryLibraryLocalDataSource())
        val trendingList = listOf(
            AnimeSummary(
                id = CatalogAnimeId("201"),
                titles = AnimeTitles(english = "Frieren"),
                format = AnimeFormat.TV,
                averageScore = 95,
                episodes = 28,
            )
        )
        val fakeAnimeRepo = FakeAnimeRepository(trendingList = trendingList)
        val viewModel = HomeViewModel(libraryRepo, fakeAnimeRepo)

        var selectedAnimeId: CatalogAnimeId? = null

        compose.setContent {
            AppTheme {
                Box(Modifier.size(800.dp, 600.dp)) {
                    HomeScreen(
                        viewModel = viewModel,
                        onAnimeSelected = { selectedAnimeId = it },
                    )
                }
            }
        }

        compose.waitForIdle()
        compose.onNodeWithTag("home_trending_card_201").assertIsDisplayed()
        compose.onNodeWithTag("home_trending_card_201").performClick()

        assertEquals(CatalogAnimeId("201"), selectedAnimeId)
    }

    @Test
    fun `clicar no banner de biblioteca vazia navega para a busca`() {
        val libraryRepo = DefaultLibraryRepository(InMemoryLibraryLocalDataSource())
        val fakeAnimeRepo = FakeAnimeRepository()
        val viewModel = HomeViewModel(libraryRepo, fakeAnimeRepo)

        var searchNavigated = false

        compose.setContent {
            AppTheme {
                Box(Modifier.size(800.dp, 600.dp)) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToSearch = { searchNavigated = true },
                    )
                }
            }
        }

        compose.waitForIdle()
        compose.onNodeWithTag("home_empty_library_banner").assertIsDisplayed()
        compose.onNodeWithTag("home_empty_library_button").performClick()

        assertTrue(searchNavigated, "Deveria ter navegado para a tela de pesquisa")
    }

    @Test
    fun `acionar incremento rapido na HomeScreen avanca progresso sem navegar para detalhes`() {
        val libraryRepo = DefaultLibraryRepository(InMemoryLibraryLocalDataSource())
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("101"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 2, total = 12),
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 2000L,
        )
        runBlocking { libraryRepo.saveEntry(entry) }

        val fakeAnimeRepo = FakeAnimeRepository()
        val viewModel = HomeViewModel(libraryRepo, fakeAnimeRepo)

        var selectedAnimeId: CatalogAnimeId? = null

        compose.setContent {
            AppTheme {
                Box(Modifier.size(800.dp, 600.dp)) {
                    HomeScreen(
                        viewModel = viewModel,
                        onAnimeSelected = { selectedAnimeId = it },
                    )
                }
            }
        }

        compose.waitForIdle()
        compose.onNodeWithTag("home_increment_101").performClick()
        compose.waitForIdle()

        // Garante que o clique no botão rápido não navegou para a tela de detalhes
        assertNull(selectedAnimeId, "Incremento rápido não deve navegar para detalhes")

        // Garante que o progresso foi incrementado
        val updated = runBlocking { libraryRepo.getEntryById(LibraryEntryId("lib-1")) }
        if (updated is RepositoryResult.Success) {
            assertEquals(3, updated.value?.progress?.current)
        }
    }

    private class FakeAnimeRepository(
        trendingList: List<AnimeSummary> = emptyList(),
    ) : AnimeRepository {
        private val trendingPaged = PagedResult(
            items = trendingList,
            pageInfo = PageInfo(
                currentPage = 1,
                lastPage = 1,
                hasNextPage = false,
                perPage = 10,
                total = trendingList.size,
            ),
        )

        override suspend fun getTrendingAnime(
            page: Int,
            perPage: Int
        ): RepositoryResult<PagedResult<AnimeSummary>> =
            RepositoryResult.Success(trendingPaged)

        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int
        ): RepositoryResult<PagedResult<AnimeSummary>> =
            error("Não utilizado neste teste")

        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> =
            RepositoryResult.Success(
                AnimeDetails(
                    id = id,
                    titles = AnimeTitles(english = "Anime #${id.value}"),
                    images = AnimeImages(),
                )
            )
    }
}
