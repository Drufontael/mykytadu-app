package br.com.mykytadu.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.features.home.HomeRecentItemUiState
import br.com.mykytadu.features.library.components.label
import br.com.mykytadu.features.library.components.semanticColor
import br.com.mykytadu.presentation.components.AppCard
import coil3.compose.AsyncImage

@Composable
fun HomeRecentCard(
    item: HomeRecentItemUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier
            .width(140.dp)
            .testTag("home_recent_card_${item.animeId.value}"),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.xs),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
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

                Surface(
                    shape = RoundedCornerShape(AppDimensions.radius.xs),
                    color = item.status.semanticColor().copy(alpha = 0.9f),
                    modifier = Modifier
                        .padding(AppDimensions.padding.xs)
                        .align(Alignment.TopStart),
                ) {
                    Text(
                        text = item.status.label(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = "Episódios: ${item.progressLabel}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
