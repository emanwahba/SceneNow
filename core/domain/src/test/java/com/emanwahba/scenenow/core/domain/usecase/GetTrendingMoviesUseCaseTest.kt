package com.emanwahba.scenenow.core.domain.usecase

import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.MovieDetail
import com.emanwahba.scenenow.core.domain.model.SortDirection
import com.emanwahba.scenenow.core.domain.model.SortField
import com.emanwahba.scenenow.core.domain.model.SortOption
import com.emanwahba.scenenow.core.domain.repository.MovieRepository
import com.emanwahba.scenenow.core.domain.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTrendingMoviesUseCaseTest {

    private val comedy = movie(1, "comedy", listOf(35), popularity = 5.0, releaseDate = "2024-01-01")
    private val drama = movie(2, "Drama", listOf(18), popularity = 50.0, releaseDate = "2023-06-15")
    private val both = movie(3, "Action Comedy", listOf(35, 28), popularity = 20.0, releaseDate = null)
    private val movies = listOf(comedy, drama, both)

    private val useCase = GetTrendingMoviesUseCase(FakeRepository())

    @Test
    fun `no genre keeps every movie`() {
        val result = useCase.applyFilterAndSort(movies, genreId = null, sort = popularityDescending)

        assertEquals(3, result.size)
    }

    @Test
    fun `genre filter keeps only movies that contain the genre`() {
        val result = useCase.applyFilterAndSort(movies, genreId = 35, sort = popularityDescending)

        assertEquals(listOf(both, comedy), result)
    }

    @Test
    fun `genre with no movies gives an empty list`() {
        val result = useCase.applyFilterAndSort(movies, genreId = 999, sort = popularityDescending)

        assertEquals(emptyList<Movie>(), result)
    }

    @Test
    fun `sorts by popularity descending and ascending`() {
        val descending = useCase.applyFilterAndSort(movies, null, popularityDescending)
        val ascending = useCase.applyFilterAndSort(
            movies,
            null,
            SortOption(SortField.POPULARITY, SortDirection.ASCENDING),
        )

        assertEquals(listOf(drama, both, comedy), descending)
        assertEquals(listOf(comedy, both, drama), ascending)
    }

    @Test
    fun `sorts by title ignoring case`() {
        val ascending = useCase.applyFilterAndSort(
            movies,
            null,
            SortOption(SortField.TITLE, SortDirection.ASCENDING),
        )
        val descending = useCase.applyFilterAndSort(
            movies,
            null,
            SortOption(SortField.TITLE, SortDirection.DESCENDING),
        )

        assertEquals(listOf(both, comedy, drama), ascending)
        assertEquals(listOf(drama, comedy, both), descending)
    }

    @Test
    fun `sorts by release date and treats a missing date as oldest`() {
        val ascending = useCase.applyFilterAndSort(
            movies,
            null,
            SortOption(SortField.RELEASE_DATE, SortDirection.ASCENDING),
        )
        val descending = useCase.applyFilterAndSort(
            movies,
            null,
            SortOption(SortField.RELEASE_DATE, SortDirection.DESCENDING),
        )

        assertEquals(listOf(both, drama, comedy), ascending)
        assertEquals(listOf(comedy, drama, both), descending)
    }

    @Test
    fun `filter and sort are applied together`() {
        val result = useCase.applyFilterAndSort(
            movies,
            genreId = 35,
            sort = SortOption(SortField.TITLE, SortDirection.DESCENDING),
        )

        assertEquals(listOf(comedy, both), result)
    }

    @Test
    fun `empty input gives empty output`() {
        assertEquals(emptyList<Movie>(), useCase.applyFilterAndSort(emptyList(), 35, SortOption()))
    }

    @Test
    fun `invoke returns what the repository emits`() = runTest {
        val expected = DataResult.Success(movies)
        val useCase = GetTrendingMoviesUseCase(FakeRepository(trending = expected))

        assertEquals(expected, useCase().first())
    }

    @Test
    fun `genres and detail use cases delegate to the repository`() = runTest {
        val genres = DataResult.Success(listOf(Genre(1, "Action")))
        val error = DataResult.Error("nope")
        val repository = FakeRepository(genres = genres, detail = error)

        assertEquals(genres, GetGenresUseCase(repository)().first())
        assertEquals(error, GetMovieDetailUseCase(repository)(7))
        assertEquals(7, repository.requestedDetailId)
    }

    private val popularityDescending = SortOption(SortField.POPULARITY, SortDirection.DESCENDING)

    private fun movie(
        id: Int,
        title: String,
        genreIds: List<Int>,
        popularity: Double,
        releaseDate: String?,
    ) = Movie(id, title, null, genreIds, popularity, releaseDate)

    private class FakeRepository(
        private val trending: DataResult<List<Movie>> = DataResult.Success(emptyList()),
        private val genres: DataResult<List<Genre>> = DataResult.Success(emptyList()),
        private val detail: DataResult<MovieDetail> = DataResult.Error("unused"),
    ) : MovieRepository {
        var requestedDetailId: Int? = null

        override fun getTrendingMovies(): Flow<DataResult<List<Movie>>> = flowOf(trending)

        override suspend fun getMovieDetail(movieId: Int): DataResult<MovieDetail> {
            requestedDetailId = movieId
            return detail
        }

        override fun getGenres(): Flow<DataResult<List<Genre>>> = flowOf(genres)
    }
}
