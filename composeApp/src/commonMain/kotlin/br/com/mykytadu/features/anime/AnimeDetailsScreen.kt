package br.com.mykytadu.features.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mykytadu.composeapp.generated.resources.*
import br.com.mykytadu.core.layout.ResponsiveLayout
import br.com.mykytadu.core.layout.responsiveLayoutFor
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.domain.model.AnimeRelation
import br.com.mykytadu.domain.model.AnimeRelationType
import br.com.mykytadu.domain.model.AnimeReleaseStatus
import br.com.mykytadu.domain.model.AnimeSeason
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.presentation.components.AppChip
import br.com.mykytadu.presentation.components.AppButton
import br.com.mykytadu.presentation.components.AppCard
import br.com.mykytadu.presentation.components.AppError
import br.com.mykytadu.presentation.components.AppLoading
import br.com.mykytadu.presentation.components.AppTopBar
import br.com.mykytadu.presentation.components.icons.AppIcons
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AnimeDetailsScreen(
    id: CatalogAnimeId,
    onBack: () -> Unit,
    viewModel: AnimeDetailsViewModel = koinViewModel(parameters = { parametersOf(id) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val screenTitle = stringResource(Res.string.anime_details_title)
    val backDescription = stringResource(Res.string.anime_details_back)

    if (screenTitle.isBlank() || backDescription.isBlank()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AppLoading()
        }
        return
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = screenTitle,
                navigationIcon = AppIcons.Navigation.Back,
                onNavigationClick = onBack,
                navigationContentDescription = backDescription,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            when (val current = state) {
                AnimeDetailsUiState.Loading -> AppLoading(
                    contentDescription = stringResource(Res.string.anime_details_loading),
                )
                is AnimeDetailsUiState.Content -> AnimeDetailsContent(
                    details = current.details,
                    onOpenExternal = uriHandler::openUri,
                    modifier = Modifier.fillMaxSize(),
                )
                is AnimeDetailsUiState.Failure -> AppError(
                    title = stringResource(Res.string.anime_details_error_title),
                    message = failureMessage(current.reason),
                    retryText = stringResource(Res.string.anime_details_retry),
                    onRetry = viewModel::retry,
                )
            }
        }
    }
}

@Composable
internal fun AnimeDetailsContent(
    details: AnimeDetails,
    modifier: Modifier = Modifier,
    onOpenExternal: (String) -> Unit = {},
) {
    val unavailableTitle = stringResource(Res.string.anime_details_title_unavailable)
    val title = details.displayTitle(unavailableTitle)
    val description = details.displayDescription()

    LazyColumn(modifier = modifier) {
        item { DetailsBanner(details.bannerUrl()) }
        item {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth().padding(AppDimensions.padding.lg),
            ) {
                when (responsiveLayoutFor(maxWidth)) {
                    ResponsiveLayout.COMPACT -> Column(
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.lg),
                    ) {
                        DetailsCover(
                            url = details.coverUrl(),
                            title = title,
                            modifier = Modifier
                                .widthIn(max = 220.dp)
                                .align(Alignment.CenterHorizontally),
                        )
                        DetailsText(details, title, description, onOpenExternal = onOpenExternal)
                    }
                    ResponsiveLayout.EXPANDED -> Row(
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.xxl),
                        verticalAlignment = Alignment.Top,
                    ) {
                        DetailsCover(
                            url = details.coverUrl(),
                            title = title,
                            modifier = Modifier.width(240.dp),
                        )
                        DetailsText(
                            details = details,
                            title = title,
                            description = description,
                            onOpenExternal = onOpenExternal,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsBanner(url: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
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

@Composable
private fun DetailsCover(
    url: String?,
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(AppDimensions.radius.lg))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        url?.let {
            AsyncImage(
                model = it,
                contentDescription = stringResource(Res.string.anime_details_cover, title),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun DetailsText(
    details: AnimeDetails,
    title: String,
    description: String?,
    modifier: Modifier = Modifier,
    onOpenExternal: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.lg),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
        )
        DetailsMetadata(details)
        description?.let {
            Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm)) {
                Text(
                    text = stringResource(Res.string.anime_details_description_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        DetailsComplementaryContent(details, onOpenExternal)
        Spacer(Modifier.height(AppDimensions.spacing.xxl))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailsComplementaryContent(
    details: AnimeDetails,
    onOpenExternal: (String) -> Unit,
) {
    val genres = details.displayGenres()
    if (genres.isNotEmpty()) {
        DetailsSection(stringResource(Res.string.anime_details_genres_title)) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
            ) {
                genres.forEach { AppChip(label = it) }
            }
        }
    }

    val startDate = details.startDate?.displayValue()
    val endDate = details.endDate?.displayValue()
    if (startDate != null || endDate != null) {
        DetailsSection(stringResource(Res.string.anime_details_dates_title)) {
            startDate?.let { Text(stringResource(Res.string.anime_details_start_date, it)) }
            endDate?.let { Text(stringResource(Res.string.anime_details_end_date, it)) }
        }
    }

    val studios = details.displayStudios()
    if (studios.isNotEmpty()) {
        DetailsSection(stringResource(Res.string.anime_details_studios_title)) {
            Text(
                text = studios.joinToString(", "),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    details.trailer?.externalUrl()?.let { url ->
        DetailsSection(stringResource(Res.string.anime_details_trailer_title)) {
            AppButton(
                text = stringResource(Res.string.anime_details_watch_trailer),
                onClick = { onOpenExternal(url) },
            )
        }
    }

    if (details.relations.isNotEmpty()) {
        DetailsRelations(details.relations)
    }
}

@Composable
private fun DetailsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        content()
    }
}

@Composable
private fun DetailsRelations(relations: List<AnimeRelation>) {
    val unavailableTitle = stringResource(Res.string.anime_details_title_unavailable)
    DetailsSection(stringResource(Res.string.anime_details_relations_title)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md)) {
            items(relations, key = { it.id.value }) { relation ->
                val title = relation.displayTitle(unavailableTitle)
                AppCard(modifier = Modifier.width(180.dp)) {
                    relation.coverUrl()?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = stringResource(Res.string.anime_details_relation_cover, title),
                            modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f).clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.height(AppDimensions.spacing.sm))
                    }
                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    relation.relationType?.relationLabel()?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimeRelationType.relationLabel(): String? = when (this) {
    AnimeRelationType.ADAPTATION -> stringResource(Res.string.anime_details_relation_adaptation)
    AnimeRelationType.PREQUEL -> stringResource(Res.string.anime_details_relation_prequel)
    AnimeRelationType.SEQUEL -> stringResource(Res.string.anime_details_relation_sequel)
    AnimeRelationType.PARENT -> stringResource(Res.string.anime_details_relation_parent)
    AnimeRelationType.SIDE_STORY -> stringResource(Res.string.anime_details_relation_side_story)
    AnimeRelationType.CHARACTER -> stringResource(Res.string.anime_details_relation_character)
    AnimeRelationType.SUMMARY -> stringResource(Res.string.anime_details_relation_summary)
    AnimeRelationType.ALTERNATIVE -> stringResource(Res.string.anime_details_relation_alternative)
    AnimeRelationType.SPIN_OFF -> stringResource(Res.string.anime_details_relation_spin_off)
    AnimeRelationType.OTHER -> stringResource(Res.string.anime_details_relation_other)
    AnimeRelationType.SOURCE -> stringResource(Res.string.anime_details_relation_source)
    AnimeRelationType.COMPILATION -> stringResource(Res.string.anime_details_relation_compilation)
    AnimeRelationType.CONTAINS -> stringResource(Res.string.anime_details_relation_contains)
    AnimeRelationType.UNKNOWN -> null
}

@Composable
private fun DetailsMetadata(details: AnimeDetails) {
    val labels = buildList {
        details.format?.formatLabel()?.let(::add)
        details.status?.statusLabel()?.let(::add)
        details.episodes?.let { add(stringResource(Res.string.anime_details_episodes, it)) }
        details.duration?.let { add(stringResource(Res.string.anime_details_duration, it)) }
        details.season?.seasonLabel()?.let { season ->
            add(details.seasonYear?.let { "$season $it" } ?: season)
        }
        if (details.season == null) details.seasonYear?.let { add(it.toString()) }
        details.averageScore?.let { add(stringResource(Res.string.anime_details_score, it)) }
    }

    if (labels.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
        ) {
            labels.forEach { label -> AppChip(label = label) }
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

@Composable
private fun AnimeReleaseStatus.statusLabel(): String? = when (this) {
    AnimeReleaseStatus.FINISHED -> stringResource(Res.string.anime_details_status_finished)
    AnimeReleaseStatus.RELEASING -> stringResource(Res.string.anime_details_status_releasing)
    AnimeReleaseStatus.NOT_YET_RELEASED -> stringResource(Res.string.anime_details_status_not_yet_released)
    AnimeReleaseStatus.CANCELLED -> stringResource(Res.string.anime_details_status_cancelled)
    AnimeReleaseStatus.HIATUS -> stringResource(Res.string.anime_details_status_hiatus)
    AnimeReleaseStatus.UNKNOWN -> null
}

@Composable
private fun AnimeSeason.seasonLabel(): String? = when (this) {
    AnimeSeason.WINTER -> stringResource(Res.string.anime_details_season_winter)
    AnimeSeason.SPRING -> stringResource(Res.string.anime_details_season_spring)
    AnimeSeason.SUMMER -> stringResource(Res.string.anime_details_season_summer)
    AnimeSeason.FALL -> stringResource(Res.string.anime_details_season_fall)
    AnimeSeason.UNKNOWN -> null
}

@Composable
private fun failureMessage(reason: RepositoryFailure): String = when (reason) {
    RepositoryFailure.Unavailable -> stringResource(Res.string.anime_details_error_unavailable)
    RepositoryFailure.Timeout -> stringResource(Res.string.anime_details_error_timeout)
    RepositoryFailure.RateLimited -> stringResource(Res.string.anime_details_error_rate_limited)
    RepositoryFailure.InvalidInput,
    RepositoryFailure.InvalidData,
    RepositoryFailure.RemoteFailure,
    RepositoryFailure.Unknown,
    -> stringResource(Res.string.anime_details_error_generic)
}
