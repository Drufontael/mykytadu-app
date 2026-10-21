package br.com.mykytadu.features.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
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
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalTestApi::class)
class HomeEndToEndUiTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `fluxo completo da home hibrida com animes em andamento atualizados recentemente e tendencias`() {
        val libraryRepo = DefaultLibraryRepository(InMemoryLibraryLocalDataSource())
        val entryWatching = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("101"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 5, total = 12),
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 2000L,
        )
        val entryCompleted = LibraryEntry(
            id = LibraryEntryId("lib-2"),
            animeId = CatalogAnimeId("102"),
            status = LibraryStatus.COMPLETED,
            progress = LibraryProgress(current = 24, total = 24),
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 3000L,
        )
        runBlocking {
            libraryRepo.saveEntry(entryWatching)
            libraryRepo.saveEntry(entryCompleted)
        }

        val trendingList = listOf(
            AnimeSummary(
                id = CatalogAnimeId("201"),
                titles = AnimeTitles(english = "Chainsaw Man"),
                format = AnimeFormat.TV,
                averageScore = 87,
                episodes = 12,
            )
        )
        val detailsMap = mapOf(
            CatalogAnimeId("101") to AnimeDetails(
                id = CatalogAnimeId("101"),
                titles = AnimeTitles(english = "Mob Psycho 100"),
                images = AnimeImages(),
                episodes = 12,
            ),
            CatalogAnimeId("102") to AnimeDetails(
                id = CatalogAnimeId("102"),
                titles = AnimeTitles(english = "Steins;Gate"),
                images = AnimeImages(),
                episodes = 24,
            ),
        )

        val animeRepo = FakeAnimeRepository(
            trendingList = trendingList,
            detailsMap = detailsMap,
        )
        val viewModel = HomeViewModel(libraryRepo, animeRepo)

        compose.setContent {
            AppTheme {
                Box(Modifier.size(1000.dp, 1200.dp)) {
                    HomeScreen(
                        viewModel = viewModel,
                    )
                }
            }
        }

        compose.waitUntil(timeoutMillis = 5000) {
            compose.onAllNodes(hasText("Mob Psycho 100")).fetchSemanticsNodes().isNotEmpty()
        }

        // 1. Valida prioridade visual de continuar assistindo
        compose.onNodeWithText("Continuar assistindo").assertIsDisplayed()
        compose.onNodeWithTag("home_continue_card_101").assertIsDisplayed()
        compose.onAllNodes(hasText("Mob Psycho 100"))[0].assertIsDisplayed()

        // 2. Valida atualizados recentemente
        compose.onNodeWithText("Atualizados recentemente").assertIsDisplayed()
        compose.onNodeWithTag("home_recent_card_102").assertIsDisplayed()
        compose.onNodeWithText("Steins;Gate").assertIsDisplayed()

        // 3. Valida seção de tendências da AniList
        compose.onNodeWithText("Em alta na AniList").assertIsDisplayed()
        compose.onNodeWithTag("home_trending_card_201").assertIsDisplayed()
        compose.onNodeWithText("Chainsaw Man").assertIsDisplayed()

        // 4. Executa atalho rápido de avanço de progresso (+1)
        compose.onNodeWithTag("home_increment_101").performClick()
        compose.waitForIdle()

        // 5. Confirma atualização reativa no card e no repositório
        compose.onAllNodes(hasText("Episódio: 6 / 12"))[0].assertIsDisplayed()

        val saved = runBlocking { libraryRepo.getEntryById(LibraryEntryId("lib-1")) }
        assertNotNull(saved)
        if (saved is RepositoryResult.Success) {
            assertEquals(6, saved.value?.progress?.current)
        }
    }

    @Test
    fun `fluxo de recuperacao defensiva quando a AniList falha e apos retry carrega com sucesso`() {
        val libraryRepo = DefaultLibraryRepository(InMemoryLibraryLocalDataSource())
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("101"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 1, total = 12),
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 1000L,
        )
        runBlocking { libraryRepo.saveEntry(entry) }

        val animeRepo = FakeAnimeRepository(
            trendingResult = RepositoryResult.Failure(RepositoryFailure.Unavailable),
        )
        val viewModel = HomeViewModel(libraryRepo, animeRepo)

        compose.setContent {
            AppTheme {
                Box(Modifier.size(1000.dp, 1200.dp)) {
                    HomeScreen(
                        viewModel = viewModel,
                    )
                }
            }
        }

        compose.waitUntil(timeoutMillis = 5000) {
            compose.onAllNodes(hasTestTag("home_trending_retry_button")).fetchSemanticsNodes().isNotEmpty()
        }

        // As seções da biblioteca local funcionam perfeitamente mesmo com falha externa
        compose.onNodeWithText("Continuar assistindo").assertIsDisplayed()
        compose.onNodeWithTag("home_continue_card_101").assertIsDisplayed()

        // A seção de tendências exibe mensagem de erro e botão de tentar novamente
        compose.onNodeWithTag("home_trending_error_container").assertIsDisplayed()
        compose.onNodeWithTag("home_trending_retry_button").assertIsDisplayed()

        // Simula recuperação do serviço externo da AniList
        animeRepo.trendingResult = RepositoryResult.Success(
            PagedResult(
                items = listOf(
                    AnimeSummary(
                        id = CatalogAnimeId("201"),
                        titles = AnimeTitles(english = "Bleach"),
                    )
                ),
                pageInfo = PageInfo(
                    currentPage = 1,
                    lastPage = 1,
                    hasNextPage = false,
                    perPage = 10,
                    total = 1,
                ),
            )
        )

        // Clica no botão de retry da seção
        compose.onNodeWithTag("home_trending_retry_button").performClick()
        compose.waitForIdle()

        // A seção agora exibe o card de tendências com sucesso
        compose.onNodeWithTag("home_trending_card_201").assertIsDisplayed()
        compose.onNodeWithText("Bleach").assertIsDisplayed()
    }

    private class FakeAnimeRepository(
        var trendingResult: RepositoryResult<PagedResult<AnimeSummary>>? = null,
        trendingList: List<AnimeSummary> = emptyList(),
        private val detailsMap: Map<CatalogAnimeId, AnimeDetails> = emptyMap(),
    ) : AnimeRepository {

        init {
            if (trendingResult == null) {
                trendingResult = RepositoryResult.Success(
                    PagedResult(
                        items = trendingList,
                        pageInfo = PageInfo(
                            currentPage = 1,
                            lastPage = 1,
                            hasNextPage = false,
                            perPage = 10,
                            total = trendingList.size,
                        ),
                    )
                )
            }
        }

        override suspend fun getTrendingAnime(
            page: Int,
            perPage: Int
        ): RepositoryResult<PagedResult<AnimeSummary>> =
            trendingResult ?: error("trendingResult não configurado")

        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int
        ): RepositoryResult<PagedResult<AnimeSummary>> =
            error("Não utilizado neste teste")

        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> {
            val details = detailsMap[id] ?: AnimeDetails(
                id = id,
                titles = AnimeTitles(english = "Anime #${id.value}"),
                images = AnimeImages(),
            )
            return RepositoryResult.Success(details)
        }
    }
}
