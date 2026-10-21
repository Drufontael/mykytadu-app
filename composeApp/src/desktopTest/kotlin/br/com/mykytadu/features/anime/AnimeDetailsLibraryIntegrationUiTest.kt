package br.com.mykytadu.features.anime

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
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class AnimeDetailsLibraryIntegrationUiTest {
    @get:Rule
    val compose = createComposeRule()

    private fun sampleDetails() = AnimeDetails(
        id = CatalogAnimeId("10"),
        titles = AnimeTitles(english = "Fullmetal Alchemist: Brotherhood"),
        episodes = 64,
    )

    @Test
    fun `obra fora da biblioteca exibe botao de adicionar`() {
        var addClicked = false

        compose.setContent {
            AppTheme {
                Box(Modifier.size(500.dp, 800.dp)) {
                    AnimeDetailsContent(
                        details = sampleDetails(),
                        libraryEntry = null,
                        onAddToLibrary = { addClicked = true },
                    )
                }
            }
        }

        compose.onNodeWithText("+ Adicionar à biblioteca").assertIsDisplayed().performClick()
        assertTrue(addClicked)
    }

    @Test
    fun `obra na biblioteca exibe status progresso nota e aciona atalhos`() {
        var editClicked = false
        var incrementClicked = false

        val entry = LibraryEntry(
            id = LibraryEntryId("lib-10"),
            animeId = CatalogAnimeId("10"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 10, total = 64),
            isFavorite = true,
            rating = LibraryRating(95),
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 1000L,
        )

        compose.setContent {
            AppTheme {
                Box(Modifier.size(500.dp, 800.dp)) {
                    AnimeDetailsContent(
                        details = sampleDetails(),
                        libraryEntry = entry,
                        onEditLibrary = { editClicked = true },
                        onIncrementProgress = { incrementClicked = true },
                    )
                }
            }
        }

        compose.onNodeWithText("Assistindo").assertIsDisplayed()
        compose.onNodeWithText("Episódio 10 / 64").assertIsDisplayed()
        compose.onNodeWithText("★ 95/100").assertIsDisplayed()

        compose.onNodeWithText("+1").performClick()
        assertTrue(incrementClicked)

        compose.onNodeWithText("Editar").performClick()
        assertTrue(editClicked)
    }
}
