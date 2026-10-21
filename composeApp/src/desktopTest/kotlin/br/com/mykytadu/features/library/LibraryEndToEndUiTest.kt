package br.com.mykytadu.features.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.repository.DefaultLibraryRepository
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeImages
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class LibraryEndToEndUiTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun bibliotecaVaziaExibeEstadoVazioComBotaoExplorar() {
        val libraryRepo = DefaultLibraryRepository(InMemoryLibraryLocalDataSource())
        val fakeAnimeRepo = FakeAnimeRepository()
        val viewModel = LibraryViewModel(libraryRepo, fakeAnimeRepo)

        var exploreClicked = false

        compose.setContent {
            AppTheme {
                Box(Modifier.size(800.dp, 600.dp)) {
                    LibraryScreen(
                        viewModel = viewModel,
                        onNavigateToSearch = { exploreClicked = true },
                    )
                }
            }
        }

        compose.onNodeWithText("Sua biblioteca está vazia").assertIsDisplayed()
        compose.onNodeWithText("Ir para pesquisa").assertIsDisplayed()
        compose.onNodeWithText("Ir para pesquisa").performClick()
        assertTrue(exploreClicked)
    }

    @Test
    fun fluxoCompletoDeUsuarioListagemFiltrosProgressoEdicaoERemocaoDefensiva() {
        val localDataSource = InMemoryLibraryLocalDataSource()
        val libraryRepo = DefaultLibraryRepository(localDataSource)
        val animeRepo = FakeAnimeRepository(
            titles = mapOf(
                CatalogAnimeId("1") to "Cowboy Bebop",
                CatalogAnimeId("2") to "Steins;Gate",
            ),
        )

        runBlocking {
            libraryRepo.saveEntry(
                LibraryEntry(
                    id = LibraryEntryId("lib-1"),
                    animeId = CatalogAnimeId("1"),
                    status = LibraryStatus.WATCHING,
                    progress = LibraryProgress(current = 10, total = 26),
                    rating = LibraryRating(90),
                    isFavorite = true,
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 1000L,
                )
            )
            libraryRepo.saveEntry(
                LibraryEntry(
                    id = LibraryEntryId("lib-2"),
                    animeId = CatalogAnimeId("2"),
                    status = LibraryStatus.PLANNING,
                    progress = LibraryProgress(current = 0, total = 24),
                    rating = null,
                    isFavorite = false,
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 1000L,
                )
            )
        }

        val viewModel = LibraryViewModel(libraryRepo, animeRepo)
        var selectedAnimeId: CatalogAnimeId? = null

        compose.setContent {
            AppTheme {
                Box(Modifier.size(900.dp, 700.dp)) {
                    LibraryScreen(
                        viewModel = viewModel,
                        onAnimeSelected = { selectedAnimeId = it },
                    )
                }
            }
        }

        // 1. Ambos os animes estão inicialmente presentes
        compose.onNodeWithText("Cowboy Bebop").assertIsDisplayed()
        compose.onNodeWithText("Steins;Gate").assertIsDisplayed()

        // 2. Filtro por status: selecionar "Assistindo (1)" para isolar Cowboy Bebop
        compose.onNodeWithText("Assistindo (1)").performClick()
        compose.onNodeWithText("Cowboy Bebop").assertIsDisplayed()
        compose.onNodeWithText("Steins;Gate").assertDoesNotExist()

        // 3. Incremento rápido de progresso: Cowboy Bebop avança de 10/26 para 11/26
        compose.onNodeWithText("Episódios: 10 / 26").assertIsDisplayed()
        compose.onNodeWithText("+1").performClick()
        compose.onNodeWithText("Episódios: 11 / 26").assertIsDisplayed()

        // 4. Edição via diálogo modal
        compose.onNodeWithText("Editar").performClick()
        compose.onNodeWithText("Editar na biblioteca").assertIsDisplayed()

        // 5. Exclusão com confirmação defensiva
        compose.onNodeWithText("Remover da biblioteca").performClick()
        compose.onNodeWithText("Remover da biblioteca?").assertIsDisplayed()

        // Cancelar exclusão: fecha diálogo e mantém anime
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Cowboy Bebop").assertIsDisplayed()

        // Reabrir diálogo de edição e confirmar exclusão
        compose.onNodeWithText("Editar").performClick()
        compose.onNodeWithText("Remover da biblioteca").performClick()
        compose.onNodeWithText("Remover").performClick()

        // 6. Voltar para "Todos (1)" e verificar que Cowboy Bebop foi removido e Steins;Gate permanece
        compose.onNodeWithText("Todos (1)").performClick()
        compose.onNodeWithText("Cowboy Bebop").assertDoesNotExist()
        compose.onNodeWithText("Steins;Gate").assertIsDisplayed()
    }

    private class FakeAnimeRepository(
        private val titles: Map<CatalogAnimeId, String> = emptyMap(),
    ) : AnimeRepository {
        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int
        ): RepositoryResult<PagedResult<AnimeSummary>> =
            error("Não utilizado neste teste")

        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> {
            val title = titles[id] ?: "Title for ${id.value}"
            return RepositoryResult.Success(
                AnimeDetails(
                    id = id,
                    titles = AnimeTitles(english = title, romaji = title),
                    images = AnimeImages(coverExtraLarge = "https://example.com/cover.jpg"),
                )
            )
        }
    }
}
