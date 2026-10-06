package com.emanwahba.scenenow.feature.movieslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.SortDirection
import com.emanwahba.scenenow.core.domain.model.SortField
import com.emanwahba.scenenow.core.domain.model.SortOption
import com.emanwahba.scenenow.core.ui.components.ErrorView
import com.emanwahba.scenenow.core.ui.components.LoadingView
import com.emanwahba.scenenow.core.ui.components.MoviePoster
import com.emanwahba.scenenow.core.ui.theme.Elevation
import com.emanwahba.scenenow.core.ui.theme.PosterSize
import com.emanwahba.scenenow.core.ui.theme.Spacing
import com.emanwahba.scenenow.core.ui.theme.sceneNowTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesListRoute(
    onMovieClick: (Int) -> Unit,
    viewModel: MoviesListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.movies_list_title)) },
                actions = { SortMenu(uiState.sortOption, viewModel::onSortOptionChanged) },
                colors = sceneNowTopAppBarColors(),
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> LoadingView(modifier = Modifier.padding(padding))
            uiState.errorMessage != null -> ErrorView(
                message = uiState.errorMessage.orEmpty(),
                onRetry = viewModel::onRetry,
                modifier = Modifier.padding(padding),
            )

            else -> MoviesListContent(
                uiState = uiState,
                onGenreSelected = viewModel::onGenreSelected,
                onMovieClick = onMovieClick,
                padding = padding,
            )
        }
    }
}

@Composable
private fun MoviesListContent(
    uiState: MoviesListUiState,
    onGenreSelected: (Int?) -> Unit,
    onMovieClick: (Int) -> Unit,
    padding: PaddingValues,
) {
    Column(modifier = Modifier.padding(padding)) {
        if (uiState.genres.isNotEmpty()) {
            GenreFilterRow(
                genres = uiState.genres,
                selectedGenreId = uiState.selectedGenreId,
                onGenreSelected = onGenreSelected,
            )
        }

        key(uiState.selectedGenreId, uiState.sortOption) {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(Spacing.large),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                items(uiState.visibleMovies, key = { it.id }) { movie ->
                    MovieRow(movie = movie, onClick = { onMovieClick(movie.id) })
                }
            }
        }
    }
}

@Composable
private fun GenreFilterRow(
    genres: List<com.emanwahba.scenenow.core.domain.model.Genre>,
    selectedGenreId: Int?,
    onGenreSelected: (Int?) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Spacing.large, vertical = Spacing.small),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        item {
            FilterChip(
                selected = selectedGenreId == null,
                onClick = { onGenreSelected(null) },
                label = { Text(stringResource(R.string.movies_list_filter_all)) },
            )
        }
        items(genres, key = { it.id }) { genre ->
            FilterChip(
                selected = selectedGenreId == genre.id,
                onClick = { onGenreSelected(genre.id) },
                label = { Text(genre.name) },
            )
        }
    }
}

@Composable
private fun MovieRow(movie: Movie, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.card),
    ) {
        Row(modifier = Modifier.padding(Spacing.medium)) {
            MoviePoster(
                posterUrl = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier.size(PosterSize.list),
            )
            Column(modifier = Modifier.padding(start = Spacing.medium)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = movie.releaseDate.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (movie.description.isNotBlank()) {
                    Text(
                        text = movie.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SortMenu(current: SortOption, onSortOptionChanged: (SortOption) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(
            Icons.AutoMirrored.Filled.Sort,
            contentDescription = stringResource(R.string.movies_list_sort)
        )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        val options = listOf(
            stringResource(R.string.movies_list_sort_most_popular) to SortOption(
                SortField.POPULARITY,
                SortDirection.DESCENDING
            ),
            stringResource(R.string.movies_list_sort_least_popular) to SortOption(
                SortField.POPULARITY,
                SortDirection.ASCENDING
            ),
            stringResource(R.string.movies_list_sort_title_az) to SortOption(
                SortField.TITLE,
                SortDirection.ASCENDING
            ),
            stringResource(R.string.movies_list_sort_title_za) to SortOption(
                SortField.TITLE,
                SortDirection.DESCENDING
            ),
            stringResource(R.string.movies_list_sort_newest) to SortOption(
                SortField.RELEASE_DATE,
                SortDirection.DESCENDING
            ),
            stringResource(R.string.movies_list_sort_oldest) to SortOption(
                SortField.RELEASE_DATE,
                SortDirection.ASCENDING
            ),
        )
        options.forEach { (label, option) ->
            DropdownMenuItem(
                text = { Text(label) },
                trailingIcon = {
                    if (option == current) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(R.string.movies_list_sort_selected)
                        )
                    }
                },
                onClick = { onSortOptionChanged(option); expanded = false },
            )
        }
    }
}
