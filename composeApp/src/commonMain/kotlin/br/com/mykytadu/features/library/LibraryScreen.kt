package br.com.mykytadu.features.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.library_error_generic
import br.com.mykytadu.composeapp.generated.resources.library_error_title
import br.com.mykytadu.composeapp.generated.resources.library_sort_progress
import br.com.mykytadu.composeapp.generated.resources.library_sort_rating
import br.com.mykytadu.composeapp.generated.resources.library_sort_recent
import br.com.mykytadu.composeapp.generated.resources.library_sort_title
import br.com.mykytadu.composeapp.generated.resources.library_title
import br.com.mykytadu.core.layout.ResponsiveLayout
import br.com.mykytadu.core.layout.responsiveLayoutFor
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.features.library.components.LibraryAnimeCard
import br.com.mykytadu.features.library.components.LibraryDeleteConfirmationDialog
import br.com.mykytadu.features.library.components.LibraryEditDialog
import br.com.mykytadu.features.library.components.LibraryEmptyState
import br.com.mykytadu.features.library.components.LibraryFilterChips
import br.com.mykytadu.presentation.components.AppError
import br.com.mykytadu.presentation.components.AppLoading
import br.com.mykytadu.presentation.components.AppTopBar
import br.com.mykytadu.presentation.components.appInteractivePointer
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryScreen(
    onAnimeSelected: (CatalogAnimeId) -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    viewModel: LibraryViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var editingItem by remember { mutableStateOf<LibraryItemUiState?>(null) }
    var deletingItem by remember { mutableStateOf<LibraryItemUiState?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is LibraryUiState.Loading -> {
                AppTopBar(title = stringResource(Res.string.library_title))
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    AppLoading()
                }
            }

            is LibraryUiState.Failure -> {
                AppTopBar(title = stringResource(Res.string.library_title))
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    AppError(
                        title = stringResource(Res.string.library_error_title),
                        message = state.message ?: stringResource(Res.string.library_error_generic),
                        onRetry = viewModel::retry,
                    )
                }
            }

            is LibraryUiState.Content -> {
                var sortMenuExpanded by remember { mutableStateOf(false) }

                AppTopBar(
                    title = stringResource(Res.string.library_title),
                    actions = {
                        Box {
                            TextButton(
                                onClick = { sortMenuExpanded = true },
                                modifier = Modifier.appInteractivePointer(enabled = true),
                            ) {
                                Text(
                                    text = when (state.sort) {
                                        LibrarySort.RECENT_UPDATED -> stringResource(Res.string.library_sort_recent)
                                        LibrarySort.TITLE -> stringResource(Res.string.library_sort_title)
                                        LibrarySort.RATING -> stringResource(Res.string.library_sort_rating)
                                        LibrarySort.PROGRESS -> stringResource(Res.string.library_sort_progress)
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }

                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.library_sort_recent)) },
                                    onClick = {
                                        viewModel.onSortSelected(LibrarySort.RECENT_UPDATED)
                                        sortMenuExpanded = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.library_sort_title)) },
                                    onClick = {
                                        viewModel.onSortSelected(LibrarySort.TITLE)
                                        sortMenuExpanded = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.library_sort_rating)) },
                                    onClick = {
                                        viewModel.onSortSelected(LibrarySort.RATING)
                                        sortMenuExpanded = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.library_sort_progress)) },
                                    onClick = {
                                        viewModel.onSortSelected(LibrarySort.PROGRESS)
                                        sortMenuExpanded = false
                                    },
                                )
                            }
                        }
                    },
                )

                LibraryFilterChips(
                    selectedFilter = state.filter,
                    onFilterSelected = viewModel::onFilterSelected,
                    countsByFilter = state.statusCounts,
                )

                if (state.isLibraryEmpty || state.isFilterEmpty) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        LibraryEmptyState(
                            isFilterEmpty = state.isFilterEmpty,
                            onNavigateToSearch = onNavigateToSearch,
                            onResetFilter = { viewModel.onFilterSelected(LibraryFilter.ALL) },
                        )
                    }
                } else {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val layout = responsiveLayoutFor(maxWidth)
                        val columns = if (layout == ResponsiveLayout.EXPANDED) 2 else 1
                        val gridState = rememberLazyGridState()

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            state = gridState,
                            contentPadding = PaddingValues(AppDimensions.padding.lg),
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(
                                items = state.items,
                                key = { it.id.value },
                            ) { item ->
                                LibraryAnimeCard(
                                    item = item,
                                    onClick = { onAnimeSelected(item.animeId) },
                                    onIncrementProgress = { viewModel.incrementProgress(item.animeId) },
                                    onEditClick = { editingItem = item },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    editingItem?.let { item ->
        LibraryEditDialog(
            item = item,
            onSave = { status, progress, rating, notes, isFavorite ->
                viewModel.updateEntryDetails(
                    entryId = item.id,
                    newStatus = status,
                    newProgress = progress,
                    newRating = rating,
                    newNotes = notes,
                    isFavorite = isFavorite,
                )
                editingItem = null
            },
            onDeleteRequest = {
                deletingItem = item
                editingItem = null
            },
            onDismissRequest = { editingItem = null },
        )
    }

    deletingItem?.let { item ->
        LibraryDeleteConfirmationDialog(
            animeTitle = item.title,
            onConfirm = {
                viewModel.deleteEntry(item.id)
                deletingItem = null
            },
            onDismissRequest = { deletingItem = null },
        )
    }
}
