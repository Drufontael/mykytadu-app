package br.com.mykytadu.features.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.search_clear
import br.com.mykytadu.composeapp.generated.resources.search_debouncing
import br.com.mykytadu.composeapp.generated.resources.search_empty_message
import br.com.mykytadu.composeapp.generated.resources.search_empty_title
import br.com.mykytadu.composeapp.generated.resources.search_error_generic
import br.com.mykytadu.composeapp.generated.resources.search_error_rate_limited
import br.com.mykytadu.composeapp.generated.resources.search_error_title
import br.com.mykytadu.composeapp.generated.resources.search_error_timeout
import br.com.mykytadu.composeapp.generated.resources.search_error_unavailable
import br.com.mykytadu.composeapp.generated.resources.search_field_placeholder
import br.com.mykytadu.composeapp.generated.resources.search_initial_message
import br.com.mykytadu.composeapp.generated.resources.search_initial_title
import br.com.mykytadu.composeapp.generated.resources.search_loading
import br.com.mykytadu.composeapp.generated.resources.search_rate_limit_cooldown
import br.com.mykytadu.composeapp.generated.resources.search_retry
import br.com.mykytadu.composeapp.generated.resources.search_title
import br.com.mykytadu.composeapp.generated.resources.search_title_unavailable
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.presentation.components.AppCard
import br.com.mykytadu.presentation.components.AppEmptyState
import br.com.mykytadu.presentation.components.AppError
import br.com.mykytadu.presentation.components.AppLoading
import br.com.mykytadu.presentation.components.AppSearchBar
import br.com.mykytadu.presentation.components.icons.AppIcons
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
    modifier: Modifier = Modifier,
) {
    val unavailableTitle = stringResource(Res.string.search_title_unavailable)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = AppDimensions.padding.lg),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
    ) {
        items(
            items = items,
            key = { anime -> anime.id.value },
        ) { anime ->
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = anime.displayTitle(unavailableTitle),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

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
