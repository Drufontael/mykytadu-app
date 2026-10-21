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
import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.features.home.components.HomeContinueWatchingCard
import br.com.mykytadu.features.home.components.HomeEmptyLibraryBanner
import br.com.mykytadu.features.home.components.HomeRecentCard
import br.com.mykytadu.features.home.components.HomeSectionHeader
import br.com.mykytadu.features.home.components.HomeTrendingCard
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class HomeContentUiTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `HomeContinueWatchingCard exibe titulo progresso e dispara cliques de card e incremento`() {
        var cardClicked = false
        var incrementClicked = false

        val item = HomeContinueWatchingItemUiState(
            animeId = CatalogAnimeId("10"),
            title = "Jujutsu Kaisen",
            progressCurrent = 4,
            progressTotal = 24,
            progressRatio = 4f / 24f,
            updatedAtEpochMillis = 1000L,
        )

        compose.setContent {
            AppTheme {
                Box(Modifier.size(400.dp, 300.dp)) {
                    HomeContinueWatchingCard(
                        item = item,
                        onClick = { cardClicked = true },
                        onIncrementProgress = { incrementClicked = true },
                    )
                }
            }
        }

        compose.onNodeWithText("Jujutsu Kaisen").assertIsDisplayed()
        compose.onNodeWithText("Episódio: 4 / 24").assertIsDisplayed()

        compose.onNodeWithTag("home_increment_10").performClick()
        assertTrue(incrementClicked, "Incremento deveria ter sido acionado")

        compose.onNodeWithTag("home_continue_card_10").performClick()
        assertTrue(cardClicked, "Card de detalhes deveria ter sido acionado")
    }

    @Test
    fun `HomeRecentCard exibe titulo badge de status e dispara clique`() {
        var cardClicked = false

        val item = HomeRecentItemUiState(
            animeId = CatalogAnimeId("20"),
            title = "One Piece",
            status = LibraryStatus.WATCHING,
            progressCurrent = 1080,
            progressTotal = null,
            updatedAtEpochMillis = 2000L,
        )

        compose.setContent {
            AppTheme {
                Box(Modifier.size(300.dp, 300.dp)) {
                    HomeRecentCard(
                        item = item,
                        onClick = { cardClicked = true },
                    )
                }
            }
        }

        compose.onNodeWithText("One Piece").assertIsDisplayed()
        compose.onNodeWithText("Assistindo").assertIsDisplayed()
        compose.onNodeWithText("Episódios: 1080").assertIsDisplayed()

        compose.onNodeWithTag("home_recent_card_20").performClick()
        assertTrue(cardClicked, "Clique no card de recentes deveria ter sido disparado")
    }

    @Test
    fun `HomeTrendingCard exibe titulo nota e dispara clique`() {
        var cardClicked = false

        val item = HomeTrendingItemUiState(
            animeId = CatalogAnimeId("30"),
            title = "Solo Leveling",
            format = AnimeFormat.TV,
            averageScore = 88,
            episodes = 12,
        )

        compose.setContent {
            AppTheme {
                Box(Modifier.size(300.dp, 300.dp)) {
                    HomeTrendingCard(
                        item = item,
                        onClick = { cardClicked = true },
                    )
                }
            }
        }

        compose.onNodeWithText("Solo Leveling").assertIsDisplayed()
        compose.onNodeWithText("★ 88%").assertIsDisplayed()
        compose.onNodeWithText("Série de TV • 12 eps").assertIsDisplayed()

        compose.onNodeWithTag("home_trending_card_30").performClick()
        assertTrue(cardClicked, "Clique no card de trending deveria ter sido disparado")
    }

    @Test
    fun `HomeEmptyLibraryBanner exibe mensagem e dispara onNavigateToSearch`() {
        var searchNavigated = false

        compose.setContent {
            AppTheme {
                Box(Modifier.size(400.dp, 300.dp)) {
                    HomeEmptyLibraryBanner(
                        onNavigateToSearch = { searchNavigated = true },
                    )
                }
            }
        }

        compose.onNodeWithTag("home_empty_library_banner").assertIsDisplayed()
        compose.onNodeWithTag("home_empty_library_button").performClick()
        assertTrue(searchNavigated, "Navegação para busca deveria ter sido disparada pelo banner")
    }

    @Test
    fun `HomeSectionHeader exibe titulo e acao contextual`() {
        var actionClicked = false

        compose.setContent {
            AppTheme {
                Box(Modifier.size(400.dp, 100.dp)) {
                    HomeSectionHeader(
                        title = "Continuar assistindo",
                        actionLabel = "Ver todos",
                        onActionClick = { actionClicked = true },
                    )
                }
            }
        }

        compose.onNodeWithText("Continuar assistindo").assertIsDisplayed()
        compose.onNodeWithText("Ver todos").assertIsDisplayed()
        compose.onNodeWithText("Ver todos").performClick()
        assertTrue(actionClicked, "Ação do header deveria ter sido acionada")
    }
}
