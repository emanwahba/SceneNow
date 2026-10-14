package com.emanwahba.scenenow.feature.movieslist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.SortDirection
import com.emanwahba.scenenow.core.domain.model.SortField
import com.emanwahba.scenenow.core.domain.model.SortOption
import com.emanwahba.scenenow.core.ui.theme.SceneNowTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MoviesListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val action = Genre(28, "Action")
    private val drama = Genre(18, "Drama")
    private val firstMovie = Movie(
        id = 1,
        title = "Galactic Heist",
        posterUrl = null,
        genreIds = listOf(28),
        popularity = 90.0,
        releaseDate = "2024-03-01",
        description = "A crew plans one last job.",
    )
    private val secondMovie = Movie(
        id = 2,
        title = "Quiet Harbor",
        posterUrl = null,
        genreIds = listOf(18),
        popularity = 40.0,
        releaseDate = "2023-09-12",
    )
    private val contentState = MoviesListUiState(
        isLoading = false,
        allMovies = listOf(firstMovie, secondMovie),
        visibleMovies = listOf(firstMovie, secondMovie),
        genres = listOf(action, drama),
    )

    private fun setScreen(
        uiState: MoviesListUiState,
        onSortOptionChanged: (SortOption) -> Unit = {},
        onGenreSelected: (Int?) -> Unit = {},
        onMovieClick: (Int) -> Unit = {},
        onRetry: () -> Unit = {},
    ) {
        composeRule.setContent {
            SceneNowTheme {
                MoviesListScreen(
                    uiState = uiState,
                    onSortOptionChanged = onSortOptionChanged,
                    onGenreSelected = onGenreSelected,
                    onMovieClick = onMovieClick,
                    onRetry = onRetry,
                )
            }
        }
    }

    @Test
    fun showsMoviesAndGenreChips() {
        setScreen(contentState)

        composeRule.onNodeWithText("Galactic Heist").assertIsDisplayed()
        composeRule.onNodeWithText("Quiet Harbor").assertIsDisplayed()
        composeRule.onNodeWithText("All").assertIsDisplayed()
        composeRule.onNodeWithText("Action").assertIsDisplayed()
        composeRule.onNodeWithText("Drama").assertIsDisplayed()
    }

    @Test
    fun clickingMovieReportsItsId() {
        var clickedId: Int? = null
        setScreen(contentState, onMovieClick = { clickedId = it })

        composeRule.onNodeWithText("Quiet Harbor").performClick()

        assertEquals(2, clickedId)
    }

    @Test
    fun clickingGenreChipReportsGenreId() {
        var selected: Int? = -1
        setScreen(contentState, onGenreSelected = { selected = it })

        composeRule.onNodeWithText("Drama").performClick()

        assertEquals(18, selected)
    }

    @Test
    fun clickingAllChipClearsGenreFilter() {
        var selected: Int? = -1
        setScreen(
            contentState.copy(selectedGenreId = 18),
            onGenreSelected = { selected = it },
        )

        composeRule.onNodeWithText("All").performClick()

        assertEquals(null, selected)
    }

    @Test
    fun sortMenuReportsChosenOption() {
        var chosen: SortOption? = null
        setScreen(contentState, onSortOptionChanged = { chosen = it })

        composeRule.onNodeWithContentDescription("Sort").performClick()
        composeRule.onNodeWithText("Title A-Z").performClick()

        assertEquals(SortOption(SortField.TITLE, SortDirection.ASCENDING), chosen)
    }

    @Test
    fun sortMenuMarksCurrentOptionAsSelected() {
        setScreen(contentState)

        composeRule.onNodeWithContentDescription("Sort").performClick()

        composeRule.onNodeWithContentDescription("Selected").assertIsDisplayed()
    }

    @Test
    fun loadingStateHidesMoviesAndError() {
        setScreen(MoviesListUiState(isLoading = true))

        composeRule.onNodeWithText("Trending this week").assertIsDisplayed()
        composeRule.onNodeWithText("Galactic Heist").assertDoesNotExist()
        composeRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test
    fun errorStateShowsMessageAndRetriesOnClick() {
        var retried = false
        setScreen(
            MoviesListUiState(isLoading = false, errorMessage = "No internet connection"),
            onRetry = { retried = true },
        )

        composeRule.onNodeWithText("No internet connection").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()

        assertTrue(retried)
    }
}
