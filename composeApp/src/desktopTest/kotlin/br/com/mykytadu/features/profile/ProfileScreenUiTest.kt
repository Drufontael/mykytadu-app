package br.com.mykytadu.features.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.domain.model.auth.User
import br.com.mykytadu.domain.model.auth.UserId
import br.com.mykytadu.domain.model.profile.UserStats
import br.com.mykytadu.domain.model.sync.SyncStatus
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ProfileScreenUiTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `renderiza informacoes do usuario com avatar e iniciais`() {
        val user = User(UserId("usr-1"), "edu@mykytadu.com", "Eduardo Estigarribia")
        val state = ProfileUiState(
            user = user,
            stats = UserStats(totalAnimeCount = 5),
        )

        compose.setContent {
            AppTheme {
                ProfileScreenContent(
                    uiState = state,
                    onNavigateToSettings = {},
                    onTriggerSync = {},
                    onLogout = {},
                )
            }
        }

        compose.onNodeWithTag("profile_screen").assertIsDisplayed()
        compose.onNodeWithTag("profile_card").assertIsDisplayed()
        compose.onNodeWithTag("profile_avatar").assertIsDisplayed()
        compose.onNodeWithTag("profile_avatar_initials").assertTextEquals("EE")
        compose.onNodeWithTag("profile_user_name").assertTextEquals("Eduardo Estigarribia")
        compose.onNodeWithTag("profile_user_email").assertTextEquals("edu@mykytadu.com")
    }

    @Test
    fun `renderiza estatisticas e detalhamento por status da biblioteca`() {
        val state = ProfileUiState(
            stats = UserStats(
                totalAnimeCount = 10,
                watchingCount = 2,
                completedCount = 5,
                planningCount = 2,
                pausedCount = 1,
                droppedCount = 0,
                favoriteCount = 3,
                totalEpisodesWatched = 45,
                estimatedHoursWatched = 18.0,
                averageScore = 88.0,
            )
        )

        compose.setContent {
            AppTheme {
                ProfileScreenContent(
                    uiState = state,
                    onNavigateToSettings = {},
                    onTriggerSync = {},
                    onLogout = {},
                )
            }
        }

        compose.onNodeWithTag("profile_stats_card").assertIsDisplayed()
        compose.onNodeWithTag("stat_total_animes").assertIsDisplayed()
        compose.onNodeWithTag("stat_total_episodes").assertIsDisplayed()
        compose.onNodeWithTag("stat_hours_watched").assertIsDisplayed()
        compose.onNodeWithTag("stat_favorites").assertIsDisplayed()
        compose.onNodeWithTag("stat_average_score").assertIsDisplayed()

        compose.onNodeWithTag("profile_status_breakdown").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("status_watching").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("status_completed").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("status_planning").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("status_paused").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("status_dropped").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `renderiza cartao de sincronizacao e clica no botao sincronizar`() {
        var syncTriggered = false
        val state = ProfileUiState(
            syncStatus = SyncStatus.Idle,
            pendingMutationsCount = 2,
        )

        compose.setContent {
            AppTheme {
                ProfileScreenContent(
                    uiState = state,
                    onNavigateToSettings = {},
                    onTriggerSync = { syncTriggered = true },
                    onLogout = {},
                )
            }
        }

        compose.onNodeWithTag("profile_sync_card").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("profile_sync_status_text").assertIsDisplayed()
        compose.onNodeWithTag("profile_sync_pending_text").assertIsDisplayed()

        compose.onNodeWithTag("profile_sync_button").performScrollTo().assertIsDisplayed().performClick()
        assertTrue(syncTriggered)
    }

    @Test
    fun `clicar em configuracoes e em logout dispara os respectivos callbacks`() {
        var settingsClicked = false
        var logoutClicked = false

        compose.setContent {
            AppTheme {
                ProfileScreenContent(
                    uiState = ProfileUiState(),
                    onNavigateToSettings = { settingsClicked = true },
                    onTriggerSync = {},
                    onLogout = { logoutClicked = true },
                )
            }
        }

        compose.onNodeWithTag("profile_settings_button").performScrollTo().assertIsDisplayed().performClick()
        assertTrue(settingsClicked)

        compose.onNodeWithTag("profile_logout_button").performScrollTo().assertIsDisplayed().performClick()
        assertTrue(logoutClicked)
    }
}

