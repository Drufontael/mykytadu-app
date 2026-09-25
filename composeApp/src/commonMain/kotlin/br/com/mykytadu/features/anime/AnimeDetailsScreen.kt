package br.com.mykytadu.features.anime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mykytadu.presentation.components.AppLoading
import br.com.mykytadu.presentation.components.AppError
import br.com.mykytadu.presentation.NavigationPlaceholder
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.anime_details_placeholder
import br.com.mykytadu.composeapp.generated.resources.anime_details_back
import br.com.mykytadu.composeapp.generated.resources.anime_details_error_generic
import br.com.mykytadu.composeapp.generated.resources.anime_details_error_rate_limited
import br.com.mykytadu.composeapp.generated.resources.anime_details_error_timeout
import br.com.mykytadu.composeapp.generated.resources.anime_details_error_title
import br.com.mykytadu.composeapp.generated.resources.anime_details_error_unavailable
import br.com.mykytadu.composeapp.generated.resources.anime_details_loading
import br.com.mykytadu.composeapp.generated.resources.anime_details_retry
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AnimeDetailsScreen(
    id: CatalogAnimeId,
    onBack: () -> Unit,
    viewModel: AnimeDetailsViewModel = koinViewModel(parameters = { parametersOf(id) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val backText = stringResource(Res.string.anime_details_back)

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val current = state) {
            AnimeDetailsUiState.Loading -> AppLoading(
                contentDescription = stringResource(Res.string.anime_details_loading),
            )

            is AnimeDetailsUiState.Content -> {
                val text = stringResource(
                    Res.string.anime_details_placeholder,
                    current.details.id.value,
                )
                // Web resources load asynchronously; do not create an action without its label.
                if (text.isBlank() || backText.isBlank()) {
                    AppLoading()
                } else {
                    NavigationPlaceholder(
                        text = text,
                        onClick = onBack,
                        actionText = backText,
                    )
                }
            }

            is AnimeDetailsUiState.Failure -> AppError(
                title = stringResource(Res.string.anime_details_error_title),
                message = failureMessage(current.reason),
                retryText = stringResource(Res.string.anime_details_retry),
                onRetry = viewModel::retry,
            )
        }
    }
}

@Composable
private fun failureMessage(reason: RepositoryFailure): String =
    when (reason) {
        RepositoryFailure.Unavailable -> stringResource(Res.string.anime_details_error_unavailable)
        RepositoryFailure.Timeout -> stringResource(Res.string.anime_details_error_timeout)
        RepositoryFailure.RateLimited -> stringResource(Res.string.anime_details_error_rate_limited)
        RepositoryFailure.InvalidInput,
        RepositoryFailure.InvalidData,
        RepositoryFailure.RemoteFailure,
        RepositoryFailure.Unknown,
        -> stringResource(Res.string.anime_details_error_generic)
    }
