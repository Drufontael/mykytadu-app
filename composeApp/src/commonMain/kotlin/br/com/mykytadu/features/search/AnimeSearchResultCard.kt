package br.com.mykytadu.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.presentation.components.AppCard
import coil3.compose.AsyncImage

@Composable
internal fun AnimeSearchResultCard(
    anime: AnimeSummary,
    unavailableTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
        ) {
            AnimeCover(
                url = anime.searchCoverUrl(),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f),
            )
            Text(
                text = anime.displayTitle(unavailableTitle),
                modifier = Modifier.height(AppDimensions.spacing.section),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

internal fun AnimeSummary.searchCoverUrl(): String? =
    sequenceOf(images.coverExtraLarge, images.coverLarge)
        .mapNotNull { url -> url?.trim()?.takeIf(String::isNotEmpty) }
        .firstOrNull()

@Composable
private fun AnimeCover(
    url: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AppDimensions.radius.md))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        url?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}
