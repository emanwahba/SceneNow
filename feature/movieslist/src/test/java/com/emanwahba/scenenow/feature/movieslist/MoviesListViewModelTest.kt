package com.emanwahba.scenenow.feature.movieslist

import app.cash.turbine.test
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.SortDirection
import com.emanwahba.scenenow.core.domain.model.SortField
import com.emanwahba.scenenow.core.domain.model.SortOption
import com.emanwahba.scenenow.core.domain.repository.MovieRepository
import com.emanwahba.scenenow.core.domain.usecase.GetGenresUseCase
import com.emanwahba.scenenow.core.domain.usecase.GetTrendingMoviesUseCase
import com.emanwahba.scenenow.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private class FakeMovieRepository(
    private val trending: Result<List<Movie>> = Result.Success(emptyList()),
) : MovieRepository {
    override fun getTrendingMovies(): Flow<Result<List<Movie>>> = flowOf(trending)
    override suspend fun getMovieDetail(movieId: Int) = throw NotImplementedError()
    override fun getGenres(): Flow<Result<List<com.emanwahba.scenenow.core.domain.model.Genre>>> =
        flowOf(Result.Success(emptyList()))
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
        val repository = FakeMovieRepository(trending = Result.Success(listOf(comedy, drama)))
        val viewModel = viewModel(repository)

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isLoading) state = awaitItem()
            assertEquals(listOf(drama, comedy), state.visibleMovies)
        }
    }

    @Test
    fun `selecting a genre filters the visible list`() = runTest {
        val repository = FakeMovieRepository(trending = Result.Success(listOf(comedy, drama)))
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
        val repository = FakeMovieRepository(trending = Result.Success(listOf(comedy, drama)))
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
        val repository = FakeMovieRepository(trending = Result.Error("No internet"))
        val viewModel = viewModel(repository)

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isLoading) state = awaitItem()
            assertEquals("No internet", state.errorMessage)
        }
    }
}
