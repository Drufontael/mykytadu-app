package br.com.mykytadu.features.anime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.domain.model.AnimeReleaseStatus
import br.com.mykytadu.domain.model.AnimeRelation
import br.com.mykytadu.domain.model.AnimeRelationType
import br.com.mykytadu.domain.model.AnimeSeason
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.MediaType
import br.com.mykytadu.domain.model.PartialDate
import br.com.mykytadu.domain.model.Studio
import br.com.mykytadu.domain.model.Trailer
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AnimeDetailsContentUiTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `conteudo compacto apresenta titulo metadados e sinopse normalizada`() {
        compose.setContent {
            AppTheme {
                Box(Modifier.size(500.dp, 800.dp)) {
                    AnimeDetailsContent(details = completeDetails())
                }
            }
        }

        compose.onNodeWithText("Cowboy Bebop").assertIsDisplayed()
        compose.onNodeWithText("Série de TV").fetchSemanticsNode()
        compose.onNodeWithText("Finalizado").fetchSemanticsNode()
        compose.onNodeWithText("26 episódios").fetchSemanticsNode()
        compose.onNodeWithText("Outono 1998").fetchSemanticsNode()
        compose.onNodeWithText("Nota 86/100").fetchSemanticsNode()
        compose.onNodeWithText("Sinopse").fetchSemanticsNode()
        compose.onNodeWithText("Caçadores de recompensa.\nNo espaço.").fetchSemanticsNode()
    }

    @Test
    fun `conteudo expandido tolera campos opcionais ausentes`() {
        compose.setContent {
            AppTheme {
                Box(Modifier.size(900.dp, 700.dp)) {
                    AnimeDetailsContent(
                        details = AnimeDetails(
                            id = CatalogAnimeId("20"),
                            titles = AnimeTitles(romaji = "Cowboy Bebop"),
                        ),
                    )
                }
            }
        }

        compose.onNodeWithText("Cowboy Bebop").assertIsDisplayed()
        assertEquals(0, compose.onAllNodesWithText("Sinopse").fetchSemanticsNodes().size)
        assertEquals(0, compose.onAllNodesWithText("Gêneros").fetchSemanticsNodes().size)
        assertEquals(0, compose.onAllNodesWithText("Obras relacionadas").fetchSemanticsNodes().size)
    }

    @Test
    fun `conteudo complementar apresenta secoes e abre trailer reconhecido`() {
        var openedUrl: String? = null
        compose.setContent {
            AppTheme {
                Box(Modifier.size(900.dp, 700.dp)) {
                    AnimeDetailsContent(
                        details = completeDetails(),
                        onOpenExternal = { openedUrl = it },
                    )
                }
            }
        }

        compose.onNodeWithText("Gêneros").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Ação").assertIsDisplayed()
        compose.onNodeWithText("Início: 03/04/1998").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Sunrise").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Assistir ao trailer").performScrollTo().performClick()
        assertEquals("https://www.youtube.com/watch?v=abc", openedUrl)
        compose.onNodeWithText("Obras relacionadas").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Cowboy Bebop: O Filme").fetchSemanticsNode()
        compose.onNodeWithText("Sequência").fetchSemanticsNode()
    }

    @Test
    fun `relacao de anime e clicavel e relacao de manga nao possui acao de clique`() {
        var clickedId: CatalogAnimeId? = null
        val animeRelation = AnimeRelation(
            id = CatalogAnimeId("101"),
            relationType = AnimeRelationType.SEQUEL,
            mediaType = MediaType.ANIME,
            titles = AnimeTitles(english = "Anime Sequel"),
        )
        val mangaRelation = AnimeRelation(
            id = CatalogAnimeId("102"),
            relationType = AnimeRelationType.ADAPTATION,
            mediaType = MediaType.MANGA,
            titles = AnimeTitles(english = "Manga Adaptation"),
        )
        val details = completeDetails(
            relations = listOf(animeRelation, mangaRelation),
        )

        compose.setContent {
            AppTheme {
                Box(Modifier.size(900.dp, 700.dp)) {
                    AnimeDetailsContent(
                        details = details,
                        onNavigateToAnime = { clickedId = it },
                    )
                }
            }
        }

        compose.onNode(hasScrollAction() and hasContentDescription("Obras relacionadas")).performScrollTo()
        val animeCard = compose.onNodeWithText("Anime Sequel")
        animeCard.assertIsDisplayed().assertHasClickAction()
        animeCard.performClick()
        assertEquals(CatalogAnimeId("101"), clickedId)

        val mangaCard = compose.onNodeWithText("Manga Adaptation")
        mangaCard.assertIsDisplayed()
        mangaCard.assert(!hasClickAction())
    }

    @Test
    fun `conteudo renderiza com sucesso em tema claro e escuro em larguras compacta e expandida`() {
        // Tema claro em largura compacta (360.dp)
        compose.setContent {
            AppTheme(darkTheme = false) {
                Box(Modifier.size(360.dp, 750.dp)) {
                    AnimeDetailsContent(details = completeDetails())
                }
            }
        }
        compose.onNodeWithText("Cowboy Bebop").assertIsDisplayed()
        compose.onNodeWithText("Série de TV").fetchSemanticsNode()
        compose.onNodeWithText("Sinopse").fetchSemanticsNode()

        // Tema escuro em largura expandida (1024.dp)
        compose.setContent {
            AppTheme(darkTheme = true) {
                Box(Modifier.size(1024.dp, 750.dp)) {
                    AnimeDetailsContent(details = completeDetails())
                }
            }
        }
        compose.onNodeWithText("Cowboy Bebop").assertIsDisplayed()
        compose.onNodeWithText("Série de TV").fetchSemanticsNode()
        compose.onNodeWithText("Sinopse").fetchSemanticsNode()
    }

    private fun completeDetails(
        relations: List<AnimeRelation> = listOf(
            AnimeRelation(
                id = CatalogAnimeId("5"),
                relationType = AnimeRelationType.SEQUEL,
                titles = AnimeTitles(english = "Cowboy Bebop: O Filme"),
            ),
        ),
    ) = AnimeDetails(
        id = CatalogAnimeId("1"),
        titles = AnimeTitles(english = "Cowboy Bebop"),
        description = "<p>Caçadores de recompensa.<br>No espaço.</p>",
        format = AnimeFormat.TV,
        status = AnimeReleaseStatus.FINISHED,
        episodes = 26,
        duration = 24,
        season = AnimeSeason.FALL,
        seasonYear = 1998,
        averageScore = 86,
        startDate = PartialDate(1998, 4, 3),
        endDate = PartialDate(1999, 4, 24),
        genres = listOf("Ação", "Ficção científica"),
        studios = listOf(Studio(1, "Sunrise", true)),
        trailer = Trailer("abc", "youtube"),
        relations = relations,
    )
}
