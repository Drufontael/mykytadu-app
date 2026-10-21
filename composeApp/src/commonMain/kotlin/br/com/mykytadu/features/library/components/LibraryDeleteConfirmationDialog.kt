package br.com.mykytadu.features.library.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.library_delete_dialog_cancel
import br.com.mykytadu.composeapp.generated.resources.library_delete_dialog_confirm
import br.com.mykytadu.composeapp.generated.resources.library_delete_dialog_message
import br.com.mykytadu.composeapp.generated.resources.library_delete_dialog_title
import br.com.mykytadu.presentation.components.AppDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun LibraryDeleteConfirmationDialog(
    animeTitle: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppDialog(
        title = stringResource(Res.string.library_delete_dialog_title),
        message = stringResource(Res.string.library_delete_dialog_message, animeTitle),
        confirmText = stringResource(Res.string.library_delete_dialog_confirm),
        dismissText = stringResource(Res.string.library_delete_dialog_cancel),
        onConfirm = onConfirm,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    )
}
