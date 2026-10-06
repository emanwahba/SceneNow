package com.emanwahba.scenenow.feature.movieslist

import app.cash.turbine.test
import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.SortDirection
import com.emanwahba.scenenow.core.domain.model.SortField
import com.emanwahba.scenenow.core.domain.model.SortOption
import com.emanwahba.scenenow.core.domain.repository.MovieRepository
import com.emanwahba.scenenow.core.domain.usecase.GetGenresUseCase
import com.emanwahba.scenenow.core.domain.usecase.GetTrendingMoviesUseCase
import com.emanwahba.scenenow.core.domain.util.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

private class FakeMovieRepository(
    var trending: DataResult<List<Movie>> = DataResult.Success(emptyList()),
    var genres: DataResult<List<Genre>> = DataResult.Success(emptyList()),
) : MovieRepository {
    override fun getTrendingMovies(): Flow<DataResult<List<Movie>>> = flowOf(trending)
    override suspend fun getMovieDetail(movieId: Int) = throw NotImplementedError()
    override fun getGenres(): Flow<DataResult<List<Genre>>> = flowOf(genres)
}

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesListViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val comedy =
        Movie(1, "Comedy", null, listOf(35), popularity = 5.0, releaseDate = "2024-01-01")
    private val drama =
        Movie(2, "Drama", null, listOf(18), popularity = 50.0, releaseDate = "2023-06-15")

    private fun viewModel(repository: MovieRepository) = MoviesListViewModel(
        getTrendingMovies = GetTrendingMoviesUseCase(repository),
        getGenres = GetGenresUseCase(repository),
    )

    @Test
    fun `loads movies and exposes them sorted by popularity by default`() = runTest {
        val repository = FakeMovieRepository(trending = DataResult.Success(listOf(comedy, drama)))
        val viewModel = viewModel(repository)

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isLoading) state = awaitItem()
            assertEquals(listOf(drama, comedy), state.visibleMovies)
        }
    }

    @Test
    fun `selecting a genre filters the visible list`() = runTest {
        val repository = FakeMovieRepository(trending = DataResult.Success(listOf(comedy, drama)))
        val viewModel = viewModel(repository)

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isLoading) state = awaitItem()

            viewModel.onGenreSelected(35)
            state = awaitItem()
            assertEquals(listOf(comedy), state.visibleMovies)
        }
    }

    @Test
    fun `changing sort option re-sorts the visible list`() = runTest {
        val repository = FakeMovieRepository(trending = DataResult.Success(listOf(comedy, drama)))
        val viewModel = viewModel(repository)

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isLoading) state = awaitItem()

            viewModel.onSortOptionChanged(SortOption(SortField.TITLE, SortDirection.ASCENDING))
            state = awaitItem()
            assertEquals(listOf(comedy, drama), state.visibleMovies) // "Comedy" < "Drama"
        }
    }

    @Test
    fun `repository error surfaces as an error message`() = runTest {
        val repository = FakeMovieRepository(trending = DataResult.Error("No internet"))
        val viewModel = viewModel(repository)

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isLoading) state = awaitItem()
            assertEquals("No internet", state.errorMessage)
        }
    }

    @Test
    fun `exposes loaded genres for the filter chips`() = runTest {
        val genres = listOf(Genre(35, "Comedy"), Genre(18, "Drama"))
        val repository = FakeMovieRepository(genres = DataResult.Success(genres))
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        assertEquals(genres, viewModel.uiState.value.genres)
    }

    @Test
    fun `genre failure does not surface as a screen error`() = runTest {
        val repository = FakeMovieRepository(
            trending = DataResult.Success(listOf(comedy, drama)),
            genres = DataResult.Error("boom"),
        )
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        assertTrue(state.genres.isEmpty())
        assertEquals(2, state.visibleMovies.size)
    }

    @Test
    fun `selecting all genres again clears the filter`() = runTest {
        val repository = FakeMovieRepository(trending = DataResult.Success(listOf(comedy, drama)))
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onGenreSelected(35)
        viewModel.onGenreSelected(null)

        assertEquals(2, viewModel.uiState.value.visibleMovies.size)
        assertNull(viewModel.uiState.value.selectedGenreId)
    }

    @Test
    fun `genre filter is kept when the sort option changes`() = runTest {
        val repository = FakeMovieRepository(trending = DataResult.Success(listOf(comedy, drama)))
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onGenreSelected(18)
        viewModel.onSortOptionChanged(SortOption(SortField.TITLE, SortDirection.ASCENDING))

        assertEquals(listOf(drama), viewModel.uiState.value.visibleMovies)
    }

    @Test
    fun `retry after an error loads the movies`() = runTest {
        val repository = FakeMovieRepository(trending = DataResult.Error("No internet"))
        val viewModel = viewModel(repository)
        advanceUntilIdle()
        assertEquals("No internet", viewModel.uiState.value.errorMessage)

        repository.trending = DataResult.Success(listOf(comedy, drama))
        viewModel.onRetry()

        assertTrue(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(listOf(drama, comedy), state.visibleMovies)
    }
}
