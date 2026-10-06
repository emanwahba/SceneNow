package com.emanwahba.scenenow.feature.moviedetail

import androidx.lifecycle.SavedStateHandle
import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.MovieDetail
import com.emanwahba.scenenow.core.domain.repository.MovieRepository
import com.emanwahba.scenenow.core.domain.usecase.GetMovieDetailUseCase
import com.emanwahba.scenenow.core.domain.util.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val detail = MovieDetail(
        id = 7,
        title = "Seven",
        tagline = null,
        posterUrl = null,
        genres = listOf(Genre(1, "Action")),
        description = "Story",
        voteAverage = 8.0,
        voteCount = 10,
        budget = 0,
        revenue = 0,
        status = "Released",
        imdbUrl = null,
        runtimeMinutes = 100,
        releaseDate = "2020-01-01",
    )

    private fun viewModel(repository: FakeRepository, movieId: Int? = 7) = MovieDetailViewModel(
        getMovieDetail = GetMovieDetailUseCase(repository),
        savedStateHandle = SavedStateHandle(
            if (movieId == null) emptyMap() else mapOf("movieId" to movieId),
        ),
    )

    @Test
    fun `starts in loading state`() = runTest {
        val viewModel = viewModel(FakeRepository(DataResult.Success(detail)))

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loads the movie id from the navigation arguments`() = runTest {
        val repository = FakeRepository(DataResult.Success(detail))
        viewModel(repository, movieId = 7)

        advanceUntilIdle()

        assertEquals(listOf(7), repository.requestedIds)
    }

    @Test
    fun `success exposes the movie`() = runTest {
        val viewModel = viewModel(FakeRepository(DataResult.Success(detail)))

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(detail, state.movie)
        assertNull(state.errorMessage)
    }

    @Test
    fun `error exposes the message and no movie`() = runTest {
        val viewModel = viewModel(FakeRepository(DataResult.Error("No internet")))

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.movie)
        assertEquals("No internet", state.errorMessage)
    }

    @Test
    fun `retry reloads and clears the error`() = runTest {
        val repository = FakeRepository(DataResult.Error("No internet"))
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        repository.result = DataResult.Success(detail)
        viewModel.onRetry()

        assertTrue(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
        advanceUntilIdle()

        assertEquals(detail, viewModel.uiState.value.movie)
        assertEquals(2, repository.requestedIds.size)
    }

    @Test(expected = IllegalStateException::class)
    fun `missing movie id argument fails fast`() {
        viewModel(FakeRepository(DataResult.Success(detail)), movieId = null)
    }

    private class FakeRepository(var result: DataResult<MovieDetail>) : MovieRepository {
        val requestedIds = mutableListOf<Int>()

        override suspend fun getMovieDetail(movieId: Int): DataResult<MovieDetail> {
            requestedIds += movieId
            return result
        }

        override fun getTrendingMovies(): Flow<DataResult<List<Movie>>> = emptyFlow()
        override fun getGenres(): Flow<DataResult<List<Genre>>> = emptyFlow()
    }
}
