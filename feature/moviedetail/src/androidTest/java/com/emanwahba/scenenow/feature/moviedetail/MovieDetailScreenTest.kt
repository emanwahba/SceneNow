package com.emanwahba.scenenow.feature.moviedetail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.model.MovieDetail
import com.emanwahba.scenenow.core.ui.theme.SceneNowTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MovieDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val movie = MovieDetail(
        id = 1,
        title = "Galactic Heist",
        tagline = "One last job",
        posterUrl = null,
        genres = listOf(Genre(28, "Action"), Genre(53, "Thriller")),
        description = "A crew plans one last job.",
        voteAverage = 7.84,
        voteCount = 1234,
        budget = 50_000_000,
        revenue = 150_000_000,
        status = "Released",
        imdbUrl = "https://www.imdb.com/title/tt0000001/",
        runtimeMinutes = 125,
        releaseDate = "2024-03-01",
    )

    private fun setScreen(
        uiState: MovieDetailUiState,
        onBackClick: () -> Unit = {},
        onRetry: () -> Unit = {},
    ) {
        composeRule.setContent {
            SceneNowTheme {
                MovieDetailScreen(uiState = uiState, onBackClick = onBackClick, onRetry = onRetry)
            }
        }
    }

    @Test
    fun showsMovieDetails() {
        setScreen(MovieDetailUiState(isLoading = false, movie = movie))

        composeRule.onNodeWithText("One last job").assertIsDisplayed()
        composeRule.onNodeWithText("Action").assertIsDisplayed()
        composeRule.onNodeWithText("Thriller").assertIsDisplayed()
        composeRule.onNodeWithText("Overview").assertIsDisplayed()
        composeRule.onNodeWithText("A crew plans one last job.").assertIsDisplayed()
        composeRule.onNodeWithText("Released").assertIsDisplayed()
    }

    @Test
    fun backButtonInvokesCallback() {
        var backClicked = false
        setScreen(
            MovieDetailUiState(isLoading = false, movie = movie),
            onBackClick = { backClicked = true },
        )

        composeRule.onNodeWithContentDescription("Back").performClick()

        assertTrue(backClicked)
    }

    @Test
    fun loadingStateHidesDetails() {
        setScreen(MovieDetailUiState(isLoading = true))

        composeRule.onNodeWithText("Overview").assertDoesNotExist()
        composeRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test
    fun errorStateShowsMessageAndRetriesOnClick() {
        var retried = false
        setScreen(
            MovieDetailUiState(isLoading = false, errorMessage = "Something went wrong"),
            onRetry = { retried = true },
        )

        composeRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()

        assertTrue(retried)
    }
}
