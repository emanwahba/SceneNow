package com.emanwahba.scenenow.feature.moviedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.emanwahba.scenenow.core.domain.model.MovieDetail
import com.emanwahba.scenenow.core.ui.components.ErrorView
import com.emanwahba.scenenow.core.ui.components.LoadingView
import com.emanwahba.scenenow.core.ui.components.MoviePoster
import com.emanwahba.scenenow.core.ui.theme.Elevation
import com.emanwahba.scenenow.core.ui.theme.PosterSize
import com.emanwahba.scenenow.core.ui.theme.Spacing
import com.emanwahba.scenenow.core.ui.theme.sceneNowTopAppBarColors
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailRoute(
    onBackClick: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.movie?.title.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.movie_detail_back)
                        )
                    }
                },
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

            uiState.movie != null -> MovieDetailContent(uiState.movie!!, padding)
        }
    }
}

@Composable
private fun MovieDetailContent(movie: MovieDetail, padding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(Spacing.large),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        MovieDetailHeader(movie)

        if (movie.description.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                Text(
                    stringResource(R.string.movie_detail_overview),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(movie.description, style = MaterialTheme.typography.bodyLarge)
            }
        }

        InfoCard(movie)

        movie.imdbUrl?.let { url ->
            val uriHandler = LocalUriHandler.current
            TextButton(onClick = { uriHandler.openUri(url) }) {
                Text(stringResource(R.string.movie_detail_view_on_imdb))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun MovieDetailHeader(movie: MovieDetail) {
    var showFullPoster by remember { mutableStateOf(false) }

    if (showFullPoster) {
        FullPosterDialog(movie = movie, onDismiss = { showFullPoster = false })
    }

    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.large)) {
        MoviePoster(
            posterUrl = movie.posterUrl,
            contentDescription = movie.title,
            modifier = Modifier
                .size(PosterSize.detail)
                .clickable(enabled = movie.posterUrl != null) { showFullPoster = true },
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
            Text(movie.title, style = MaterialTheme.typography.titleLarge)
            movie.tagline?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = " ${"%.1f".format(movie.voteAverage)}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "  " + pluralStringResource(
                        R.plurals.movie_detail_votes,
                        movie.voteCount,
                        movie.voteCount
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            val meta = listOfNotNull(
                movie.releaseDate?.takeIf { it.isNotBlank() },
                movie.runtimeMinutes?.let { "${it / 60}h ${it % 60}m" },
            ).joinToString(" · ")
            if (meta.isNotEmpty()) {
                Text(meta, style = MaterialTheme.typography.bodyMedium)
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                movie.genres.forEach { GenreTag(it.name) }
            }
        }
    }
}

@Composable
private fun FullPosterDialog(movie: MovieDetail, onDismiss: () -> Unit) {
    // Full-screen, opaque viewer: nothing behind it shows through, and a tap anywhere closes it.
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
            contentAlignment = Alignment.Center,
        ) {
            MoviePoster(
                posterUrl = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.large)
                    .aspectRatio(2f / 3f),
            )
        }
    }
}

@Composable
private fun GenreTag(name: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = Spacing.small, vertical = Spacing.extraSmall),
        )
    }
}

@Composable
private fun InfoCard(movie: MovieDetail) {
    val unknown = stringResource(R.string.movie_detail_unknown)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.card),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            InfoRow(stringResource(R.string.movie_detail_status), movie.status)
            InfoRow(
                stringResource(R.string.movie_detail_budget),
                formatMoney(movie.budget, unknown)
            )
            InfoRow(
                stringResource(R.string.movie_detail_revenue),
                formatMoney(movie.revenue, unknown)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

// TMDB reports 0 when a figure is unknown.
private fun formatMoney(amount: Long, unknown: String): String =
    if (amount <= 0L) {
        unknown
    } else {
        NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }
            .format(amount)
    }
