package br.com.mykytadu.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.anime_details_format_movie
import br.com.mykytadu.composeapp.generated.resources.anime_details_format_music
import br.com.mykytadu.composeapp.generated.resources.anime_details_format_ona
import br.com.mykytadu.composeapp.generated.resources.anime_details_format_ova
import br.com.mykytadu.composeapp.generated.resources.anime_details_format_special
import br.com.mykytadu.composeapp.generated.resources.anime_details_format_tv
import br.com.mykytadu.composeapp.generated.resources.anime_details_format_tv_short
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.features.home.HomeTrendingItemUiState
import br.com.mykytadu.presentation.components.AppCard
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeTrendingCard(
    item: HomeTrendingItemUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier
            .width(150.dp)
            .testTag("home_trending_card_${item.animeId.value}"),
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

                item.averageScore?.let { score ->
                    Surface(
                        shape = RoundedCornerShape(AppDimensions.radius.xs),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        modifier = Modifier
                            .padding(AppDimensions.padding.xs)
                            .align(Alignment.TopEnd),
                    ) {
                        Text(
                            text = "★ $score%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            val formatText = item.format?.formatLabel()
            val metaInfo = listOfNotNull(
                formatText,
                item.episodes?.let { "$it eps" },
            ).joinToString(" • ")

            if (metaInfo.isNotEmpty()) {
                Text(
                    text = metaInfo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun AnimeFormat.formatLabel(): String? = when (this) {
    AnimeFormat.TV -> stringResource(Res.string.anime_details_format_tv)
    AnimeFormat.TV_SHORT -> stringResource(Res.string.anime_details_format_tv_short)
    AnimeFormat.MOVIE -> stringResource(Res.string.anime_details_format_movie)
    AnimeFormat.SPECIAL -> stringResource(Res.string.anime_details_format_special)
    AnimeFormat.OVA -> stringResource(Res.string.anime_details_format_ova)
    AnimeFormat.ONA -> stringResource(Res.string.anime_details_format_ona)
    AnimeFormat.MUSIC -> stringResource(Res.string.anime_details_format_music)
    AnimeFormat.MANGA, AnimeFormat.NOVEL, AnimeFormat.ONE_SHOT, AnimeFormat.UNKNOWN -> null
}
