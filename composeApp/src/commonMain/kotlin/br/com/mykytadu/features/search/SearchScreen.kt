package br.com.mykytadu.features.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import br.com.mykytadu.domain.model.CatalogAnimeId
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.search_clear
import br.com.mykytadu.composeapp.generated.resources.search_debouncing
import br.com.mykytadu.composeapp.generated.resources.search_empty_message
import br.com.mykytadu.composeapp.generated.resources.search_empty_title
import br.com.mykytadu.composeapp.generated.resources.search_error_generic
import br.com.mykytadu.composeapp.generated.resources.search_error_more_title
import br.com.mykytadu.composeapp.generated.resources.search_error_rate_limited
import br.com.mykytadu.composeapp.generated.resources.search_error_title
import br.com.mykytadu.composeapp.generated.resources.search_error_timeout
import br.com.mykytadu.composeapp.generated.resources.search_error_unavailable
import br.com.mykytadu.composeapp.generated.resources.search_field_placeholder
import br.com.mykytadu.composeapp.generated.resources.search_initial_message
import br.com.mykytadu.composeapp.generated.resources.search_initial_title
import br.com.mykytadu.composeapp.generated.resources.search_loading
import br.com.mykytadu.composeapp.generated.resources.search_loading_more
import br.com.mykytadu.composeapp.generated.resources.search_rate_limit_cooldown
import br.com.mykytadu.composeapp.generated.resources.search_retry
import br.com.mykytadu.composeapp.generated.resources.search_title
import br.com.mykytadu.composeapp.generated.resources.search_title_unavailable
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.core.layout.ResponsiveLayout
import br.com.mykytadu.core.layout.responsiveLayoutFor
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.presentation.components.AppEmptyState
import br.com.mykytadu.presentation.components.AppError
import br.com.mykytadu.presentation.components.AppLoading
import br.com.mykytadu.presentation.components.AppSearchBar
import br.com.mykytadu.presentation.components.icons.AppIcons
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    onAnimeSelected: (CatalogAnimeId) -> Unit,
    isCurrentRoute: Boolean = true,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = key(state.normalizedQuery) { rememberLazyGridState() }
    var returnIndex by rememberSaveable(state.normalizedQuery) { mutableStateOf<Int?>(null) }
    var returnOffset by rememberSaveable(state.normalizedQuery) { mutableStateOf(0) }
    DisposableEffect(isCurrentRoute, gridState) {
        onDispose {
            // Capture before the outgoing entry is measured without the main navigation.
            // Browser forward leaves this screen without invoking the card callback.
            if (isCurrentRoute) {
                returnIndex = gridState.firstVisibleItemIndex
                returnOffset = gridState.firstVisibleItemScrollOffset
            }
        }
    }
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    LaunchedEffect(lifecycleState) {
        // The outgoing transition can resize the list when the main navigation hides.
        // Restore after the entry resumes, when its original viewport is available again.
        if (lifecycleState == Lifecycle.State.RESUMED) {
            returnIndex?.let { index ->
                gridState.scrollToItem(index, returnOffset)
                returnIndex = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = AppDimensions.padding.lg,
                vertical = AppDimensions.padding.md,
            ),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.search_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        AppSearchBar(
            query = state.queryText,
            onQueryChange = viewModel::onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.search_field_placeholder),
            trailingIcon = AppIcons.Actions.Close.takeIf { state.queryText.isNotEmpty() },
            onTrailingIconClick = (viewModel::clearQuery).takeIf { state.queryText.isNotEmpty() },
            trailingIconContentDescription = stringResource(Res.string.search_clear)
                .takeIf { state.queryText.isNotEmpty() },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { viewModel.submit() }),
        )

        SearchFirstPageContent(
            content = state.content,
            onRetry = viewModel::retry,
            onLoadNextPage = viewModel::loadNextPage,
            onRetryNextPage = viewModel::retryNextPage,
            onAnimeSelected = onAnimeSelected,
            gridState = gridState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
    }
}

@Composable
private fun SearchFirstPageContent(
    content: SearchContent,
    onRetry: () -> Unit,
    onLoadNextPage: () -> Unit,
    onRetryNextPage: () -> Unit,
    onAnimeSelected: (CatalogAnimeId) -> Unit,
    gridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when (content) {
            SearchContent.Initial -> AppEmptyState(
                title = stringResource(Res.string.search_initial_title),
                message = stringResource(Res.string.search_initial_message),
                modifier = Modifier.align(Alignment.Center),
            )

            SearchContent.Debouncing -> Text(
                text = stringResource(Res.string.search_debouncing),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center),
            )

            SearchContent.Loading -> AppLoading(
                contentDescription = stringResource(Res.string.search_loading),
                modifier = Modifier.align(Alignment.Center),
            )

            is SearchContent.RateLimitCooldown -> RateLimitCooldown(
                remainingSeconds = content.remainingSeconds,
                modifier = Modifier.align(Alignment.Center),
            )

            is SearchContent.Results -> SearchResults(
                items = content.items,
                currentPage = content.pageInfo.currentPage,
                hasNextPage = content.pageInfo.hasNextPage,
                pagination = content.pagination,
                onLoadNextPage = onLoadNextPage,
                onRetryNextPage = onRetryNextPage,
                onAnimeSelected = onAnimeSelected,
                gridState = gridState,
                modifier = Modifier.fillMaxSize(),
            )

            SearchContent.Empty -> AppEmptyState(
                title = stringResource(Res.string.search_empty_title),
                message = stringResource(Res.string.search_empty_message),
                modifier = Modifier.align(Alignment.Center),
            )

            is SearchContent.Failure -> AppError(
                title = stringResource(Res.string.search_error_title),
                message = failureMessage(content.reason),
                retryText = stringResource(Res.string.search_retry),
                onRetry = onRetry,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun RateLimitCooldown(
    remainingSeconds: Int,
    modifier: Modifier = Modifier,
) {
    val message = stringResource(
        Res.string.search_rate_limit_cooldown,
        remainingSeconds,
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
    ) {
        AppLoading(contentDescription = message)
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SearchResults(
    items: List<AnimeSummary>,
    currentPage: Int,
    hasNextPage: Boolean,
    pagination: SearchPaginationState,
    onLoadNextPage: () -> Unit,
    onRetryNextPage: () -> Unit,
    onAnimeSelected: (CatalogAnimeId) -> Unit,
    gridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    val unavailableTitle = stringResource(Res.string.search_title_unavailable)

    LaunchedEffect(gridState, items.size, currentPage, hasNextPage, pagination) {
        if (!hasNextPage) return@LaunchedEffect

        snapshotFlow {
            gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect { lastVisibleIndex ->
            val prefetchThreshold = (items.lastIndex - NEXT_PAGE_PREFETCH_DISTANCE)
                .coerceAtLeast(0)
            if (
                pagination is SearchPaginationState.Idle &&
                lastVisibleIndex != null &&
                lastVisibleIndex >= prefetchThreshold
            ) {
                onLoadNextPage()
            }
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val columns = when (responsiveLayoutFor(maxWidth)) {
            ResponsiveLayout.COMPACT -> GridCells.Fixed(2)
            ResponsiveLayout.EXPANDED -> GridCells.Fixed(4)
        }

        LazyVerticalGrid(
            columns = columns,
            state = gridState,
            contentPadding = PaddingValues(bottom = AppDimensions.padding.lg),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
        ) {
            items(
                items = items,
                key = { anime -> anime.id.value },
            ) { anime ->
                AnimeSearchResultCard(
                    anime = anime,
                    unavailableTitle = unavailableTitle,
                    onClick = { onAnimeSelected(anime.id) },
                )
            }

            if (hasNextPage && pagination !is SearchPaginationState.Idle) {
                item(
                    key = PAGINATION_FOOTER_KEY,
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    when (pagination) {
                        SearchPaginationState.Idle -> Unit
                        SearchPaginationState.Loading -> Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimensions.padding.md),
                            contentAlignment = Alignment.Center,
                        ) {
                            AppLoading(
                                contentDescription = stringResource(Res.string.search_loading_more),
                            )
                        }

                        is SearchPaginationState.Failure -> AppError(
                            title = stringResource(Res.string.search_error_more_title),
                            message = failureMessage(pagination.reason),
                            retryText = stringResource(Res.string.search_retry),
                            onRetry = onRetryNextPage,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

private const val NEXT_PAGE_PREFETCH_DISTANCE = 4
private const val PAGINATION_FOOTER_KEY = "search-pagination-footer"

@Composable
private fun failureMessage(reason: RepositoryFailure): String =
    when (reason) {
        RepositoryFailure.Unavailable -> stringResource(Res.string.search_error_unavailable)
        RepositoryFailure.Timeout -> stringResource(Res.string.search_error_timeout)
        RepositoryFailure.RateLimited -> stringResource(Res.string.search_error_rate_limited)
        RepositoryFailure.InvalidInput,
        RepositoryFailure.InvalidData,
        RepositoryFailure.RemoteFailure,
        RepositoryFailure.Unknown,
        -> stringResource(Res.string.search_error_generic)
    }
