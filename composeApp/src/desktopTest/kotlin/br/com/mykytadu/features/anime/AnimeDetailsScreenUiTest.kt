package br.com.mykytadu.features.anime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.domain.model.*
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.CompletableDeferred
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@OptIn(ExperimentalTestApi::class)
class AnimeDetailsScreenUiTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `tela de detalhes transiciona de loading para erro e recupera conteudo no retry`() {
        val fakeRepo = ControllableDetailsRepository()
        startKoin {
            modules(module {
                viewModel { parameters ->
                    AnimeDetailsViewModel(parameters.get(), fakeRepo)
                }
            })
        }
        try {
            compose.setContent {
                AppTheme {
                    Box(Modifier.size(500.dp, 800.dp)) {
                        AnimeDetailsScreen(
                            id = CatalogAnimeId("10"),
                            onBack = {},
                        )
                    }
                }
            }

            compose.onNodeWithContentDescription("Carregando detalhes do anime").assertExists()

            compose.runOnIdle {
                fakeRepo.firstDeferred.complete(RepositoryResult.Failure(RepositoryFailure.Timeout))
            }

            compose.waitUntil(5_000) {
                compose.onAllNodesWithText("Não foi possível carregar os detalhes").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText("Não foi possível carregar os detalhes").assertIsDisplayed()
            compose.onNodeWithText("O carregamento demorou mais que o esperado. Tente novamente.").assertIsDisplayed()
            val retryButton = compose.onNodeWithText("Tentar novamente")
            retryButton.assertIsDisplayed().assertHasClickAction()

            retryButton.performClick()

            compose.waitUntil(5_000) { fakeRepo.retryDeferred.isActive || fakeRepo.calls == 2 }

            compose.runOnIdle {
                fakeRepo.retryDeferred.complete(
                    RepositoryResult.Success(
                        AnimeDetails(
                            id = CatalogAnimeId("10"),
                            titles = AnimeTitles(english = "Monster"),
                            description = "Um neurocirurgião genial...",
                            format = AnimeFormat.TV,
                            status = AnimeReleaseStatus.FINISHED,
                        ),
                    ),
                )
            }

            compose.waitUntil(5_000) {
                compose.onAllNodesWithText("Monster").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText("Monster").assertIsDisplayed()
            compose.onNodeWithText("Série de TV").fetchSemanticsNode()
            compose.onNodeWithText("Finalizado").fetchSemanticsNode()
        } finally {
            stopKoin()
        }
    }

    @Test
    fun `tela completa renderiza corretamente em tema claro e largura compacta`() {
        val details = AnimeDetails(
            id = CatalogAnimeId("1"),
            titles = AnimeTitles(english = "Fullmetal Alchemist"),
            description = "Dois irmãos buscam a pedra filosofal.",
            format = AnimeFormat.TV,
            status = AnimeReleaseStatus.FINISHED,
            genres = listOf("Ação", "Aventura"),
        )
        val fakeRepo = SimpleSuccessRepository(details)
        startKoin {
            modules(module {
                viewModel { parameters ->
                    AnimeDetailsViewModel(parameters.get(), fakeRepo)
                }
            })
        }
        try {
            compose.setContent {
                AppTheme(darkTheme = false) {
                    Box(Modifier.size(360.dp, 700.dp)) {
                        AnimeDetailsScreen(
                            id = CatalogAnimeId("1"),
                            onBack = {},
                        )
                    }
                }
            }
            compose.waitUntil(5_000) {
                compose.onAllNodesWithText("Fullmetal Alchemist").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText("Fullmetal Alchemist").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Sinopse").fetchSemanticsNode()
            compose.onNodeWithText("Dois irmãos buscam a pedra filosofal.").fetchSemanticsNode()
        } finally {
            stopKoin()
        }
    }

    @Test
    fun `tela completa renderiza corretamente em tema escuro e largura expandida`() {
        val details = AnimeDetails(
            id = CatalogAnimeId("1"),
            titles = AnimeTitles(english = "Fullmetal Alchemist"),
            description = "Dois irmãos buscam a pedra filosofal.",
            format = AnimeFormat.TV,
            status = AnimeReleaseStatus.FINISHED,
            genres = listOf("Ação", "Aventura"),
        )
        val fakeRepo = SimpleSuccessRepository(details)
        startKoin {
            modules(module {
                viewModel { parameters ->
                    AnimeDetailsViewModel(parameters.get(), fakeRepo)
                }
            })
        }
        try {
            compose.setContent {
                AppTheme(darkTheme = true) {
                    Box(Modifier.size(1024.dp, 768.dp)) {
                        AnimeDetailsScreen(
                            id = CatalogAnimeId("1"),
                            onBack = {},
                        )
                    }
                }
            }
            compose.waitUntil(5_000) {
                compose.onAllNodesWithText("Fullmetal Alchemist").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText("Fullmetal Alchemist").assertIsDisplayed()
            compose.onNodeWithText("Gêneros").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Ação").assertIsDisplayed()
        } finally {
            stopKoin()
        }
    }

    private class ControllableDetailsRepository : AnimeRepository {
        var calls = 0
        val firstDeferred = CompletableDeferred<RepositoryResult<AnimeDetails>>()
        val retryDeferred = CompletableDeferred<RepositoryResult<AnimeDetails>>()

        override suspend fun searchAnime(query: String, page: Int, perPage: Int): RepositoryResult<PagedResult<AnimeSummary>> =
            RepositoryResult.Failure(RepositoryFailure.Unknown)

        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> {
            calls++
            return if (calls == 1) {
                firstDeferred.await()
            } else {
                retryDeferred.await()
            }
        }
    }

    private class SimpleSuccessRepository(private val details: AnimeDetails) : AnimeRepository {
        override suspend fun searchAnime(query: String, page: Int, perPage: Int): RepositoryResult<PagedResult<AnimeSummary>> =
            RepositoryResult.Failure(RepositoryFailure.Unknown)

        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> =
            RepositoryResult.Success(details)
    }
}
