package br.com.mykytadu.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.auth_logout_button
import br.com.mykytadu.composeapp.generated.resources.profile_section_stats
import br.com.mykytadu.composeapp.generated.resources.profile_section_sync
import br.com.mykytadu.composeapp.generated.resources.profile_stat_average_score
import br.com.mykytadu.composeapp.generated.resources.profile_stat_completed
import br.com.mykytadu.composeapp.generated.resources.profile_stat_dropped
import br.com.mykytadu.composeapp.generated.resources.profile_stat_episodes
import br.com.mykytadu.composeapp.generated.resources.profile_stat_favorites
import br.com.mykytadu.composeapp.generated.resources.profile_stat_hours
import br.com.mykytadu.composeapp.generated.resources.profile_stat_paused
import br.com.mykytadu.composeapp.generated.resources.profile_stat_planning
import br.com.mykytadu.composeapp.generated.resources.profile_stat_total_animes
import br.com.mykytadu.composeapp.generated.resources.profile_stat_watching
import br.com.mykytadu.composeapp.generated.resources.profile_sync_button
import br.com.mykytadu.composeapp.generated.resources.profile_sync_pending_count
import br.com.mykytadu.composeapp.generated.resources.profile_sync_status_error
import br.com.mykytadu.composeapp.generated.resources.profile_sync_status_idle
import br.com.mykytadu.composeapp.generated.resources.profile_sync_status_success
import br.com.mykytadu.composeapp.generated.resources.profile_sync_status_syncing
import br.com.mykytadu.composeapp.generated.resources.profile_sync_up_to_date
import br.com.mykytadu.composeapp.generated.resources.settings_title
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.domain.model.auth.AuthState
import br.com.mykytadu.domain.model.sync.SyncStatus
import br.com.mykytadu.domain.repository.AuthRepository
import br.com.mykytadu.presentation.components.AppButton
import br.com.mykytadu.presentation.components.AppCard
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatformTools

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel? = null,
    authRepository: AuthRepository? = null,
) {
    val activeAuthRepository = authRepository ?: KoinPlatformTools.defaultContext().getOrNull()?.getOrNull<AuthRepository>()
    val activeViewModel = viewModel ?: KoinPlatformTools.defaultContext().getOrNull()?.getOrNull<ProfileViewModel>()
    val scope = rememberCoroutineScope()

    val vmState = activeViewModel?.uiState?.collectAsStateWithLifecycle()?.value
    val fallbackAuthState = activeAuthRepository?.authState?.collectAsStateWithLifecycle()?.value
    val fallbackUser = (fallbackAuthState as? AuthState.Authenticated)?.user

    val effectiveState = vmState?.let { state ->
        if (state.user == null && fallbackUser != null) {
            state.copy(user = fallbackUser)
        } else {
            state
        }
    } ?: ProfileUiState(user = fallbackUser)

    ProfileScreenContent(
        uiState = effectiveState,
        onNavigateToSettings = onNavigateToSettings,
        onTriggerSync = { activeViewModel?.triggerSync() },
        onLogout = {
            if (activeViewModel != null) {
                activeViewModel.logout()
            } else {
                activeAuthRepository?.let { repo ->
                    scope.launch { repo.logout() }
                }
            }
        },
        modifier = modifier,
        showLogoutButton = activeViewModel != null || activeAuthRepository != null,
    )
}

@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    onNavigateToSettings: () -> Unit,
    onTriggerSync: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    showLogoutButton: Boolean = true,
) {
    val userName = uiState.user?.name ?: "Usuário"
    val userEmail = uiState.user?.email ?: ""
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("profile_screen"),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(AppDimensions.padding.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.lg),
            ) {
                // Cartão de Informações do Usuário com Avatar
                AppCard(
                    modifier = Modifier.fillMaxWidth().testTag("profile_card"),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimensions.padding.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                    ) {
                        // Avatar Circular com Iniciais
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .testTag("profile_avatar"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = uiState.userInitials,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.testTag("profile_avatar_initials"),
                            )
                        }

                        Text(
                            text = userName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("profile_user_name"),
                        )

                        if (userEmail.isNotEmpty()) {
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("profile_user_email"),
                            )
                        }
                    }
                }

                // Cartão de Sincronização em Nuvem
                AppCard(
                    modifier = Modifier.fillMaxWidth().testTag("profile_sync_card"),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimensions.padding.lg),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                    ) {
                        Text(
                            text = stringResource(Res.string.profile_section_sync),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("profile_sync_section_title"),
                        )

                        val syncStatusText = when (uiState.syncStatus) {
                            is SyncStatus.Syncing -> stringResource(Res.string.profile_sync_status_syncing)
                            is SyncStatus.Success -> stringResource(Res.string.profile_sync_status_success)
                            is SyncStatus.Error -> stringResource(Res.string.profile_sync_status_error)
                            is SyncStatus.Idle -> stringResource(Res.string.profile_sync_status_idle)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                        ) {
                            if (uiState.isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp).testTag("profile_sync_progress"),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            Text(
                                text = syncStatusText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (uiState.syncStatus is SyncStatus.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("profile_sync_status_text"),
                            )
                        }

                        val pendingText = if (uiState.pendingMutationsCount > 0) {
                            stringResource(Res.string.profile_sync_pending_count, uiState.pendingMutationsCount)
                        } else {
                            stringResource(Res.string.profile_sync_up_to_date)
                        }

                        Text(
                            text = pendingText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("profile_sync_pending_text"),
                        )

                        if (uiState.syncErrorMessage != null) {
                            Text(
                                text = uiState.syncErrorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.testTag("profile_sync_error_text"),
                            )
                        }

                        Spacer(modifier = Modifier.height(AppDimensions.spacing.xs))

                        AppButton(
                            text = stringResource(Res.string.profile_sync_button),
                            onClick = onTriggerSync,
                            enabled = !uiState.isSyncing,
                            modifier = Modifier.fillMaxWidth().testTag("profile_sync_button"),
                        )
                    }
                }

                // Cartão de Estatísticas da Biblioteca
                AppCard(
                    modifier = Modifier.fillMaxWidth().testTag("profile_stats_card"),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimensions.padding.lg),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                    ) {
                        Text(
                            text = stringResource(Res.string.profile_section_stats),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("profile_stats_section_title"),
                        )

                        // Linha 1 de métricas principais: Total Animes e Episódios
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                        ) {
                            StatBox(
                                label = stringResource(Res.string.profile_stat_total_animes),
                                value = uiState.stats.totalAnimeCount.toString(),
                                modifier = Modifier.weight(1f).testTag("stat_total_animes"),
                            )
                            StatBox(
                                label = stringResource(Res.string.profile_stat_episodes),
                                value = uiState.stats.totalEpisodesWatched.toString(),
                                modifier = Modifier.weight(1f).testTag("stat_total_episodes"),
                            )
                        }

                        // Linha 2 de métricas: Horas Assistidas e Favoritos
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                        ) {
                            val hoursInt = uiState.stats.estimatedHoursWatched.toInt()
                            val hoursDecimal = ((uiState.stats.estimatedHoursWatched - hoursInt) * 10).toInt()
                            val formattedHours = "$hoursInt.${hoursDecimal}h"

                            StatBox(
                                label = stringResource(Res.string.profile_stat_hours),
                                value = formattedHours,
                                modifier = Modifier.weight(1f).testTag("stat_hours_watched"),
                            )
                            StatBox(
                                label = stringResource(Res.string.profile_stat_favorites),
                                value = uiState.stats.favoriteCount.toString(),
                                modifier = Modifier.weight(1f).testTag("stat_favorites"),
                            )
                        }

                        // Linha 3 de métricas: Nota Média
                        val scoreText = uiState.stats.averageScore?.let { "${it.toInt()}/100" } ?: "-"
                        StatBox(
                            label = stringResource(Res.string.profile_stat_average_score),
                            value = scoreText,
                            modifier = Modifier.fillMaxWidth().testTag("stat_average_score"),
                        )

                        // Detalhamento por Status
                        Column(
                            modifier = Modifier.fillMaxWidth().testTag("profile_status_breakdown"),
                            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.xs),
                        ) {
                            StatusRow(stringResource(Res.string.profile_stat_watching), uiState.stats.watchingCount, "status_watching")
                            StatusRow(stringResource(Res.string.profile_stat_completed), uiState.stats.completedCount, "status_completed")
                            StatusRow(stringResource(Res.string.profile_stat_planning), uiState.stats.planningCount, "status_planning")
                            StatusRow(stringResource(Res.string.profile_stat_paused), uiState.stats.pausedCount, "status_paused")
                            StatusRow(stringResource(Res.string.profile_stat_dropped), uiState.stats.droppedCount, "status_dropped")
                        }
                    }
                }

                // Ações: Configurações e Logout
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                ) {
                    AppButton(
                        text = stringResource(Res.string.settings_title),
                        onClick = onNavigateToSettings,
                        modifier = Modifier.fillMaxWidth().testTag("profile_settings_button"),
                    )

                    if (showLogoutButton) {
                        TextButton(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth().testTag("profile_logout_button"),
                        ) {
                            Text(
                                text = stringResource(Res.string.auth_logout_button),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(AppDimensions.padding.md),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.xs),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatusRow(
    label: String,
    count: Int,
    tag: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().testTag(tag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
