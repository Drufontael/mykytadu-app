package br.com.mykytadu.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.home_retry_section
import br.com.mykytadu.composeapp.generated.resources.home_section_continue_watching
import br.com.mykytadu.composeapp.generated.resources.home_section_recent
import br.com.mykytadu.composeapp.generated.resources.home_section_trending
import br.com.mykytadu.composeapp.generated.resources.home_section_trending_empty
import br.com.mykytadu.composeapp.generated.resources.home_section_trending_error
import br.com.mykytadu.composeapp.generated.resources.home_title
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.features.home.components.HomeContinueWatchingCard
import br.com.mykytadu.features.home.components.HomeEmptyLibraryBanner
import br.com.mykytadu.features.home.components.HomeRecentCard
import br.com.mykytadu.features.home.components.HomeSectionHeader
import br.com.mykytadu.features.home.components.HomeTrendingCard
import br.com.mykytadu.presentation.components.AppButton
import br.com.mykytadu.presentation.components.AppError
import br.com.mykytadu.presentation.components.AppLoading
import br.com.mykytadu.presentation.components.AppTopBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onAnimeSelected: (CatalogAnimeId) -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
    ) {
        AppTopBar(
            title = stringResource(Res.string.home_title),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = AppDimensions.padding.md),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.lg),
        ) {
            // Se a biblioteca estiver vazia, exibe banner com CTA para pesquisa/catálogo
            if (uiState.isLibraryEmpty) {
                item(key = "home_empty_banner") {
                    HomeEmptyLibraryBanner(
                        onNavigateToSearch = onNavigateToSearch,
                    )
                }
            }

            // Seção 1: Continuar assistindo (prioridade visual)
            when (val continueWatching = uiState.continueWatchingSection) {
                is HomeSectionState.Success -> {
                    item(key = "home_continue_watching_header") {
                        HomeSectionHeader(
                            title = stringResource(Res.string.home_section_continue_watching),
                        )
                    }
                    item(key = "home_continue_watching_row") {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = AppDimensions.padding.lg),
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                            modifier = Modifier.testTag("home_continue_watching_row"),
                        ) {
                            items(
                                items = continueWatching.data,
                                key = { it.animeId.value },
                            ) { item ->
                                HomeContinueWatchingCard(
                                    item = item,
                                    onClick = { onAnimeSelected(item.animeId) },
                                    onIncrementProgress = { viewModel.incrementProgress(item.animeId) },
                                )
                            }
                        }
                    }
                }
                is HomeSectionState.Loading -> {
                    item(key = "home_continue_watching_loading") {
                        Column {
                            HomeSectionHeader(title = stringResource(Res.string.home_section_continue_watching))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                AppLoading()
                            }
                        }
                    }
                }
                is HomeSectionState.Failure -> {
                    item(key = "home_continue_watching_error") {
                        AppError(
                            title = stringResource(Res.string.home_section_continue_watching),
                            message = continueWatching.message ?: "",
                        )
                    }
                }
                is HomeSectionState.Empty -> {
                    // Oculta graciosamente quando não há animes com status WATCHING
                }
            }

            // Seção 2: Atualizados recentemente
            when (val recent = uiState.recentSection) {
                is HomeSectionState.Success -> {
                    item(key = "home_recent_header") {
                        HomeSectionHeader(
                            title = stringResource(Res.string.home_section_recent),
                        )
                    }
                    item(key = "home_recent_row") {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = AppDimensions.padding.lg),
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                            modifier = Modifier.testTag("home_recent_row"),
                        ) {
                            items(
                                items = recent.data,
                                key = { it.animeId.value },
                            ) { item ->
                                HomeRecentCard(
                                    item = item,
                                    onClick = { onAnimeSelected(item.animeId) },
                                )
                            }
                        }
                    }
                }
                is HomeSectionState.Loading -> {
                    item(key = "home_recent_loading") {
                        Column {
                            HomeSectionHeader(title = stringResource(Res.string.home_section_recent))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                AppLoading()
                            }
                        }
                    }
                }
                is HomeSectionState.Failure -> {
                    item(key = "home_recent_error") {
                        AppError(
                            title = stringResource(Res.string.home_section_recent),
                            message = recent.message ?: "",
                        )
                    }
                }
                is HomeSectionState.Empty -> {
                    // Oculta graciosamente se vazia
                }
            }

            // Seção 3: Em alta na AniList (Descoberta)
            item(key = "home_trending_header") {
                HomeSectionHeader(
                    title = stringResource(Res.string.home_section_trending),
                )
            }

            when (val trending = uiState.trendingSection) {
                is HomeSectionState.Success -> {
                    item(key = "home_trending_row") {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = AppDimensions.padding.lg),
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                            modifier = Modifier.testTag("home_trending_row"),
                        ) {
                            items(
                                items = trending.data,
                                key = { it.animeId.value },
                            ) { item ->
                                HomeTrendingCard(
                                    item = item,
                                    onClick = { onAnimeSelected(item.animeId) },
                                )
                            }
                        }
                    }
                }
                is HomeSectionState.Loading -> {
                    item(key = "home_trending_loading") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            AppLoading()
                        }
                    }
                }
                is HomeSectionState.Failure -> {
                    item(key = "home_trending_error") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimensions.padding.lg)
                                .testTag("home_trending_error_container"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                        ) {
                            Text(
                                text = stringResource(Res.string.home_section_trending_error),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                            AppButton(
                                text = stringResource(Res.string.home_retry_section),
                                onClick = { viewModel.retryTrending() },
                                modifier = Modifier.testTag("home_trending_retry_button"),
                            )
                        }
                    }
                }
                is HomeSectionState.Empty -> {
                    item(key = "home_trending_empty") {
                        Text(
                            text = stringResource(Res.string.home_section_trending_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = AppDimensions.padding.lg),
                        )
                    }
                }
            }
        }
    }
}
