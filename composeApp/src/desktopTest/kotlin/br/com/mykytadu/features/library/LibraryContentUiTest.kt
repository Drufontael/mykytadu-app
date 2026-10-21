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
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.features.library.components.LibraryAnimeCard
import br.com.mykytadu.features.library.components.LibraryEmptyState
import br.com.mykytadu.features.library.components.LibraryFilterChips
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class LibraryContentUiTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `card de anime exibe titulo status progresso e nota e dispara increment`() {
        var incrementClicked = false
        var cardClicked = false
        var editClicked = false

        val item = LibraryItemUiState(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("1"),
            title = "Cowboy Bebop",
            status = LibraryStatus.WATCHING,
            progressCurrent = 12,
            progressTotal = 26,
            progressRatio = 12f / 26f,
            isFavorite = true,
            rating = 90,
            updatedAtEpochMillis = 1000L,
        )

        compose.setContent {
            AppTheme {
                Box(Modifier.size(400.dp, 300.dp)) {
                    LibraryAnimeCard(
                        item = item,
                        onClick = { cardClicked = true },
                        onIncrementProgress = { incrementClicked = true },
                        onEditClick = { editClicked = true },
                    )
                }
            }
        }

        compose.onNodeWithText("Cowboy Bebop").assertIsDisplayed()
        compose.onNodeWithText("Assistindo").assertIsDisplayed()
        compose.onNodeWithText("Episódios: 12 / 26").assertIsDisplayed()
        compose.onNodeWithText("★ 90/100").assertIsDisplayed()

        compose.onNodeWithText("+1").performClick()
        assertTrue(incrementClicked)

        compose.onNodeWithText("Editar").performClick()
        assertTrue(editClicked)
    }

    @Test
    fun `chips de filtro exibem contagens e disparam selecao`() {
        var selected: LibraryFilter? = null
        val counts = mapOf(
            LibraryFilter.ALL to 10,
            LibraryFilter.WATCHING to 4,
            LibraryFilter.COMPLETED to 6,
        )

        compose.setContent {
            AppTheme {
                LibraryFilterChips(
                    selectedFilter = LibraryFilter.ALL,
                    onFilterSelected = { selected = it },
                    countsByFilter = counts,
                )
            }
        }

        compose.onNodeWithText("Todos (10)").assertIsDisplayed()
        compose.onNodeWithText("Assistindo (4)").assertIsDisplayed()
        compose.onNodeWithText("Completos (6)").assertIsDisplayed()

        compose.onNodeWithText("Assistindo (4)").performClick()
        assertEquals(LibraryFilter.WATCHING, selected)
    }

    @Test
    fun `estado vazio de biblioteca vazia exibe mensagem e aciona busca`() {
        var searchNavigated = false

        compose.setContent {
            AppTheme {
                LibraryEmptyState(
                    isFilterEmpty = false,
                    onNavigateToSearch = { searchNavigated = true },
                )
            }
        }

        compose.onNodeWithText("Sua biblioteca está vazia").assertIsDisplayed()
        compose.onNodeWithText("Ir para pesquisa").performClick()
        assertTrue(searchNavigated)
    }

    @Test
    fun `estado vazio de filtro sem resultados exibe mensagem e reseta filtro`() {
        var resetClicked = false

        compose.setContent {
            AppTheme {
                LibraryEmptyState(
                    isFilterEmpty = true,
                    onResetFilter = { resetClicked = true },
                )
            }
        }

        compose.onNodeWithText("Nenhum anime neste filtro").assertIsDisplayed()
        compose.onNodeWithText("Todos").performClick()
        assertTrue(resetClicked)
    }
}
