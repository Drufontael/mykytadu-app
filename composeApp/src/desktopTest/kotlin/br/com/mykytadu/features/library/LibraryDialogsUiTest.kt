package br.com.mykytadu.features.library

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.features.library.components.LibraryDeleteConfirmationDialog
import br.com.mykytadu.features.library.components.LibraryEditDialog
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class LibraryDialogsUiTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `dialogo de confirmacao de remocao exibe titulo e aciona confirmacao`() {
        var confirmed = false
        var dismissed = false

        compose.setContent {
            AppTheme {
                LibraryDeleteConfirmationDialog(
                    animeTitle = "Neon Genesis Evangelion",
                    onConfirm = { confirmed = true },
                    onDismissRequest = { dismissed = true },
                )
            }
        }

        compose.onNodeWithText("Remover da biblioteca?").assertIsDisplayed()
        compose.onNodeWithText("Remover").performClick()
        assertTrue(confirmed)
    }

    @Test
    fun `dialogo de confirmacao de remocao aciona cancelamento`() {
        var dismissed = false

        compose.setContent {
            AppTheme {
                LibraryDeleteConfirmationDialog(
                    animeTitle = "Neon Genesis Evangelion",
                    onConfirm = {},
                    onDismissRequest = { dismissed = true },
                )
            }
        }

        compose.onNodeWithText("Cancelar").performClick()
        assertTrue(dismissed)
    }

    @Test
    fun `dialogo de edicao permite alterar status e salvar alteracoes`() {
        var savedStatus: LibraryStatus? = null
        var savedProgress: Int? = null
        var deleteRequested = false

        val item = LibraryItemUiState(
            id = LibraryEntryId("lib-42"),
            animeId = CatalogAnimeId("42"),
            title = "Steins;Gate",
            status = LibraryStatus.PLANNING,
            progressCurrent = 0,
            progressTotal = 24,
            progressRatio = 0f,
            isFavorite = false,
            rating = null,
            notes = "Verificar antes do fim do ano",
            updatedAtEpochMillis = 2000L,
        )

        compose.setContent {
            AppTheme {
                LibraryEditDialog(
                    item = item,
                    onSave = { status, progress, _, _, _ ->
                        savedStatus = status
                        savedProgress = progress
                    },
                    onDeleteRequest = { deleteRequested = true },
                    onDismissRequest = {},
                )
            }
        }

        compose.onNodeWithText("Steins;Gate").assertIsDisplayed()
        compose.onNodeWithText("Assistindo").performClick()
        compose.onNodeWithText("+1").performClick()

        compose.onNodeWithText("Salvar alterações").performClick()
        assertEquals(LibraryStatus.WATCHING, savedStatus)
        assertEquals(1, savedProgress)

        compose.onNodeWithText("Remover da biblioteca").performClick()
        assertTrue(deleteRequested)
    }
}
