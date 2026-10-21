package br.com.mykytadu.features.library.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.library_filter_all
import br.com.mykytadu.composeapp.generated.resources.library_filter_completed
import br.com.mykytadu.composeapp.generated.resources.library_filter_dropped
import br.com.mykytadu.composeapp.generated.resources.library_filter_paused
import br.com.mykytadu.composeapp.generated.resources.library_filter_planning
import br.com.mykytadu.composeapp.generated.resources.library_filter_watching
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.core.theme.AppShapes
import br.com.mykytadu.features.library.LibraryFilter
import br.com.mykytadu.presentation.components.appInteractivePointer
import org.jetbrains.compose.resources.stringResource

@Composable
fun LibraryFilterChips(
    selectedFilter: LibraryFilter,
    onFilterSelected: (LibraryFilter) -> Unit,
    modifier: Modifier = Modifier,
    countsByFilter: Map<LibraryFilter, Int> = emptyMap(),
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = AppDimensions.padding.lg, vertical = AppDimensions.padding.sm),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LibraryFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            val label = when (filter) {
                LibraryFilter.ALL -> stringResource(Res.string.library_filter_all)
                LibraryFilter.WATCHING -> stringResource(Res.string.library_filter_watching)
                LibraryFilter.PLANNING -> stringResource(Res.string.library_filter_planning)
                LibraryFilter.COMPLETED -> stringResource(Res.string.library_filter_completed)
                LibraryFilter.PAUSED -> stringResource(Res.string.library_filter_paused)
                LibraryFilter.DROPPED -> stringResource(Res.string.library_filter_dropped)
            }
            val count = countsByFilter[filter]
            val displayText = if (count != null && count > 0) "$label ($count)" else label

            val backgroundColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }

            val contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Surface(
                shape = RoundedCornerShape(AppShapes.radius.chip),
                color = backgroundColor,
                contentColor = contentColor,
                modifier = Modifier
                    .appInteractivePointer(enabled = true)
                    .clickable { onFilterSelected(filter) },
            ) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(
                        horizontal = AppDimensions.padding.md,
                        vertical = AppDimensions.padding.sm,
                    ),
                )
            }
        }
    }
}
