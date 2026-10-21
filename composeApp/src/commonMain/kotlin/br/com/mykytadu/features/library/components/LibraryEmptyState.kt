package br.com.mykytadu.features.library.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.library_empty_action
import br.com.mykytadu.composeapp.generated.resources.library_empty_message
import br.com.mykytadu.composeapp.generated.resources.library_empty_title
import br.com.mykytadu.composeapp.generated.resources.library_filter_all
import br.com.mykytadu.composeapp.generated.resources.library_filter_empty_message
import br.com.mykytadu.composeapp.generated.resources.library_filter_empty_title
import br.com.mykytadu.presentation.components.AppEmptyState
import br.com.mykytadu.presentation.components.icons.AppIcons
import org.jetbrains.compose.resources.stringResource

@Composable
fun LibraryEmptyState(
    isFilterEmpty: Boolean,
    onNavigateToSearch: (() -> Unit)? = null,
    onResetFilter: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (isFilterEmpty) {
        AppEmptyState(
            title = stringResource(Res.string.library_filter_empty_title),
            message = stringResource(Res.string.library_filter_empty_message),
            icon = AppIcons.Actions.Search,
            actionText = stringResource(Res.string.library_filter_all),
            onAction = onResetFilter,
            modifier = modifier,
        )
    } else {
        AppEmptyState(
            title = stringResource(Res.string.library_empty_title),
            message = stringResource(Res.string.library_empty_message),
            icon = AppIcons.Anime.Favorite,
            actionText = stringResource(Res.string.library_empty_action),
            onAction = onNavigateToSearch,
            modifier = modifier,
        )
    }
}
