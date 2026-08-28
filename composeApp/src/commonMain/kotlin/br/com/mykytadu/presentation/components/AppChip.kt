package br.com.mykytadu.presentation.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.mykytadu.core.theme.AppColors
import br.com.mykytadu.domain.AnimeStatus

private fun semanticColor(status: AnimeStatus) = when (status) {
    AnimeStatus.WATCHING -> AppColors.semantic.watching
    AnimeStatus.COMPLETED -> AppColors.semantic.completed
    AnimeStatus.PAUSED -> AppColors.semantic.paused
    AnimeStatus.DROPPED -> AppColors.semantic.dropped
    AnimeStatus.PLANNED -> AppColors.semantic.planned
}

@Composable
fun AppChip(
    label: String,
    status: AnimeStatus? = null,
    modifier: Modifier = Modifier,
) {
    AssistChip(
        onClick = {},
        label = {
            Text(text = label)
        },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = status?.let { semanticColor(it)} ?: MaterialTheme.colorScheme.surfaceVariant,
            labelColor = if(status != null) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    )
}