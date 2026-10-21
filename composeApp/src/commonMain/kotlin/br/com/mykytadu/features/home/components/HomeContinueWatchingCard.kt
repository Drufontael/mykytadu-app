package br.com.mykytadu.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.home_increment_progress
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.features.home.HomeContinueWatchingItemUiState
import br.com.mykytadu.presentation.components.AppCard
import br.com.mykytadu.presentation.components.AppProgressBar
import br.com.mykytadu.presentation.components.appInteractivePointer
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeContinueWatchingCard(
    item: HomeContinueWatchingItemUiState,
    onClick: () -> Unit,
    onIncrementProgress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier
            .width(260.dp)
            .testTag("home_continue_card_${item.animeId.value}"),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 64.dp, height = 90.dp)
                        .clip(RoundedCornerShape(AppDimensions.radius.sm))
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
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = "Episódio: ${item.progressLabel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item.progressRatio?.let { ratio ->
                AppProgressBar(
                    progress = ratio,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledTonalButton(
                    onClick = onIncrementProgress,
                    enabled = item.canIncrement,
                    modifier = Modifier
                        .height(36.dp)
                        .appInteractivePointer(enabled = item.canIncrement)
                        .testTag("home_increment_${item.animeId.value}"),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                ) {
                    Text(
                        text = stringResource(Res.string.home_increment_progress),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}
