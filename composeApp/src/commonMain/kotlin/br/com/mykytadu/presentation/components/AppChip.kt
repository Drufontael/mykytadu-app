package br.com.mykytadu.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import br.com.mykytadu.core.theme.AppColors
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.core.theme.AppShapes
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
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AppShapes.radius.chip),
        color = status?.let { semanticColor(it) } ?: MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (status != null) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = AppDimensions.padding.md, vertical = AppDimensions.padding.sm),
        )
    }
}
