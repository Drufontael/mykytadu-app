package br.com.mykytadu.features.library.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.library_edit_cancel
import br.com.mykytadu.composeapp.generated.resources.library_edit_delete
import br.com.mykytadu.composeapp.generated.resources.library_edit_dialog_title
import br.com.mykytadu.composeapp.generated.resources.library_edit_favorite_label
import br.com.mykytadu.composeapp.generated.resources.library_edit_notes_label
import br.com.mykytadu.composeapp.generated.resources.library_edit_progress_label
import br.com.mykytadu.composeapp.generated.resources.library_edit_rating_label
import br.com.mykytadu.composeapp.generated.resources.library_edit_save
import br.com.mykytadu.composeapp.generated.resources.library_edit_status_label
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.core.theme.AppShapes
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.features.library.LibraryItemUiState
import br.com.mykytadu.presentation.components.AppButton
import br.com.mykytadu.presentation.components.AppTextField
import br.com.mykytadu.presentation.components.appInteractivePointer
import br.com.mykytadu.presentation.components.icons.AppIcons
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryEditDialog(
    item: LibraryItemUiState,
    onSave: (status: LibraryStatus, progress: Int, rating: Int?, notes: String?, isFavorite: Boolean) -> Unit,
    onDeleteRequest: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedStatus by remember { mutableStateOf(item.status) }
    var progressText by remember { mutableStateOf(item.progressCurrent.toString()) }
    var ratingText by remember { mutableStateOf(item.rating?.toString() ?: "") }
    var isFavorite by remember { mutableStateOf(item.isFavorite) }
    var notesText by remember { mutableStateOf(item.notes ?: "") }

    val scrollState = rememberScrollState()

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        Surface(
            shape = RoundedCornerShape(AppShapes.radius.dialog),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.padding.md),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimensions.padding.lg),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
            ) {
                // Título do modal e da obra
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(Res.string.library_edit_dialog_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Conteúdo editável com rolagem se necessário
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                ) {
                    // Seletor de Status
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.xs)) {
                        Text(
                            text = stringResource(Res.string.library_edit_status_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.xs),
                        ) {
                            LibraryStatus.entries.forEach { status ->
                                val isSelected = status == selectedStatus
                                Surface(
                                    shape = RoundedCornerShape(AppShapes.radius.chip),
                                    color = if (isSelected) status.semanticColor() else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .appInteractivePointer(enabled = true)
                                        .clickable { selectedStatus = status },
                                ) {
                                    Text(
                                        text = status.label(),
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    )
                                }
                            }
                        }
                    }

                    // Progresso em episódios
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.xs)) {
                        val maxLabel = item.progressTotal?.let { " (total: $it)" } ?: ""
                        Text(
                            text = "${stringResource(Res.string.library_edit_progress_label)}$maxLabel",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                        ) {
                            TextButton(
                                onClick = {
                                    val current = progressText.toIntOrNull() ?: 0
                                    if (current > 0) progressText = (current - 1).toString()
                                },
                                modifier = Modifier.appInteractivePointer(enabled = true),
                            ) {
                                Text("-1")
                            }

                            AppTextField(
                                value = progressText,
                                onValueChange = { input ->
                                    val filtered = input.filter { it.isDigit() }
                                    progressText = filtered
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(100.dp),
                            )

                            TextButton(
                                onClick = {
                                    val current = progressText.toIntOrNull() ?: 0
                                    val total = item.progressTotal
                                    if (total == null || current < total) {
                                        progressText = (current + 1).toString()
                                    }
                                },
                                modifier = Modifier.appInteractivePointer(enabled = true),
                            ) {
                                Text("+1")
                            }
                        }
                    }

                    // Nota pessoal (0 a 100)
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.xs)) {
                        Text(
                            text = stringResource(Res.string.library_edit_rating_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        AppTextField(
                            value = ratingText,
                            onValueChange = { input ->
                                val filtered = input.filter { it.isDigit() }
                                val number = filtered.toIntOrNull()
                                if (filtered.isEmpty() || (number != null && number in 0..100)) {
                                    ratingText = filtered
                                }
                            },
                            singleLine = true,
                            placeholder = "0 a 100",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    // Favorito Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                        modifier = Modifier
                            .fillMaxWidth()
                            .appInteractivePointer(enabled = true)
                            .clickable { isFavorite = !isFavorite }
                            .padding(vertical = AppDimensions.padding.xs),
                    ) {
                        Icon(
                            imageVector = if (isFavorite) AppIcons.Anime.Favorite else AppIcons.Anime.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(Res.string.library_edit_favorite_label),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    // Anotações pessoais
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.xs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = stringResource(Res.string.library_edit_notes_label),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "${notesText.length}/5000",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        AppTextField(
                            value = notesText,
                            onValueChange = { input ->
                                if (input.length <= 5000) notesText = input
                            },
                            singleLine = false,
                            placeholder = "Escreva seus pensamentos sobre a obra...",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // Ações do rodapé
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = onDeleteRequest,
                        modifier = Modifier.appInteractivePointer(enabled = true),
                    ) {
                        Text(
                            text = stringResource(Res.string.library_edit_delete),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.xs)) {
                        TextButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.appInteractivePointer(enabled = true),
                        ) {
                            Text(stringResource(Res.string.library_edit_cancel))
                        }

                        AppButton(
                            text = stringResource(Res.string.library_edit_save),
                            onClick = {
                                val parsedProgress = progressText.toIntOrNull() ?: 0
                                val parsedRating = ratingText.toIntOrNull()
                                val cleanNotes = notesText.trim().takeIf(String::isNotEmpty)
                                onSave(selectedStatus, parsedProgress, parsedRating, cleanNotes, isFavorite)
                            },
                        )
                    }
                }
            }
        }
    }
}
