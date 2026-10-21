package br.com.mykytadu.features.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.library_favorite_indicator
import br.com.mykytadu.composeapp.generated.resources.library_filter_completed
import br.com.mykytadu.composeapp.generated.resources.library_filter_dropped
import br.com.mykytadu.composeapp.generated.resources.library_filter_paused
import br.com.mykytadu.composeapp.generated.resources.library_filter_planning
import br.com.mykytadu.composeapp.generated.resources.library_filter_watching
import br.com.mykytadu.composeapp.generated.resources.library_increment_progress
import br.com.mykytadu.core.theme.AppColors
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.core.theme.AppShapes
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.features.library.LibraryItemUiState
import br.com.mykytadu.presentation.components.AppButton
import br.com.mykytadu.presentation.components.AppCard
import br.com.mykytadu.presentation.components.AppProgressBar
import br.com.mykytadu.presentation.components.appInteractivePointer
import br.com.mykytadu.presentation.components.icons.AppIcons
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource

fun LibraryStatus.semanticColor(): Color = when (this) {
    LibraryStatus.WATCHING -> AppColors.semantic.watching
    LibraryStatus.COMPLETED -> AppColors.semantic.completed
    LibraryStatus.PAUSED -> AppColors.semantic.paused
    LibraryStatus.DROPPED -> AppColors.semantic.dropped
    LibraryStatus.PLANNING -> AppColors.semantic.planned
}

@Composable
fun LibraryStatus.label(): String = when (this) {
    LibraryStatus.WATCHING -> stringResource(Res.string.library_filter_watching)
    LibraryStatus.PLANNING -> stringResource(Res.string.library_filter_planning)
    LibraryStatus.COMPLETED -> stringResource(Res.string.library_filter_completed)
    LibraryStatus.PAUSED -> stringResource(Res.string.library_filter_paused)
    LibraryStatus.DROPPED -> stringResource(Res.string.library_filter_dropped)
}

@Composable
fun LibraryAnimeCard(
    item: LibraryItemUiState,
    onClick: () -> Unit,
    onIncrementProgress: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(AppDimensions.radius.md))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                item.coverUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.xs),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (item.isFavorite) {
                        Icon(
                            imageVector = AppIcons.Anime.Favorite,
                            contentDescription = stringResource(Res.string.library_favorite_indicator),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(start = AppDimensions.spacing.xs)
                                .size(AppDimensions.icon.sm),
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(AppShapes.radius.chip),
                    color = item.status.semanticColor(),
                ) {
                    Text(
                        text = item.status.label(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = AppDimensions.padding.sm, vertical = 2.dp),
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = AppDimensions.spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Episódios: ${item.progressLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (item.rating != null) {
                            Text(
                                text = "★ ${item.rating}/100",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    item.progressRatio?.let { ratio ->
                        AppProgressBar(
                            progress = ratio,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = AppDimensions.spacing.xs),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = onEditClick,
                        modifier = Modifier.appInteractivePointer(enabled = true),
                    ) {
                        Text(
                            text = "Editar",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                    Spacer(modifier = Modifier.width(AppDimensions.spacing.xs))

                    val canIncrement = item.progressTotal == null || item.progressCurrent < item.progressTotal
                    AppButton(
                        text = "+1",
                        onClick = onIncrementProgress,
                        enabled = canIncrement,
                    )
                }
            }
        }
    }
}
