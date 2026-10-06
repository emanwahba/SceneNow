package com.emanwahba.scenenow.core.data.repository

import com.emanwahba.scenenow.core.domain.util.DataResult
import com.emanwahba.scenenow.core.network.api.TmdbApiService
import com.emanwahba.scenenow.core.network.dto.GenreDto
import com.emanwahba.scenenow.core.network.dto.GenreListResponseDto
import com.emanwahba.scenenow.core.network.dto.MovieDetailDto
import com.emanwahba.scenenow.core.network.dto.MovieDto
import com.emanwahba.scenenow.core.network.dto.TrendingMoviesResponseDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MovieRepositoryImplTest {

    private val api = FakeApi()
    private val repository = MovieRepositoryImpl(api)

    @Test
    fun `trending requests five pages and maps the results`() = runTest {
        api.trendingPage = { page -> listOf(MovieDto(id = page, title = "Movie $page")) }

        val result = repository.getTrendingMovies().first() as DataResult.Success

        assertEquals(listOf(1, 2, 3, 4, 5), api.requestedPages)
        assertEquals(listOf(1, 2, 3, 4, 5), result.data.map { it.id })
    }

    @Test
    fun `trending removes movies that appear on more than one page`() = runTest {
        api.trendingPage = { page ->
            if (page == 1) listOf(MovieDto(1, "A"), MovieDto(2, "B")) else listOf(MovieDto(2, "B"))
        }

        val result = repository.getTrendingMovies().first() as DataResult.Success

        assertEquals(listOf(1, 2), result.data.map { it.id })
    }

    @Test
    fun `no connection becomes a friendly error`() = runTest {
        val cause = IOException("offline")
        api.failure = cause

        val result = repository.getTrendingMovies().first() as DataResult.Error

        assertTrue(result.message.startsWith("No internet connection"))
        assertSame(cause, result.cause)
    }

    @Test
    fun `http failure mentions the status code`() = runTest {
        api.failure = HttpException(Response.error<Any>(503, "".toResponseBody()))

        val result = repository.getMovieDetail(1) as DataResult.Error

        assertTrue(result.message.contains("503"))
    }

    @Test
    fun `unexpected failure becomes a generic error`() = runTest {
        api.failure = IllegalStateException("bad json")

        val result = repository.getGenres().first() as DataResult.Error

        assertTrue(result.message.startsWith("Something unexpected"))
    }

    @Test
    fun `movie detail success is mapped`() = runTest {
        api.detail = MovieDetailDto(id = 9, title = "Nine", imdbId = "tt9")

        val result = repository.getMovieDetail(9) as DataResult.Success

        assertEquals(9, api.requestedDetailId)
        assertEquals("Nine", result.data.title)
        assertEquals("https://www.imdb.com/title/tt9", result.data.imdbUrl)
    }

    @Test
    fun `genres success is mapped`() = runTest {
        api.genres = listOf(GenreDto(1, "Action"), GenreDto(2, "Drama"))

        val result = repository.getGenres().first() as DataResult.Success

        assertEquals(listOf("Action", "Drama"), result.data.map { it.name })
    }

    private class FakeApi : TmdbApiService {
        var trendingPage: (Int) -> List<MovieDto> = { emptyList() }
        var detail = MovieDetailDto(id = 0, title = "")
        var genres: List<GenreDto> = emptyList()
        var failure: Throwable? = null

        val requestedPages = mutableListOf<Int>()
        var requestedDetailId: Int? = null

        override suspend fun getTrendingMovies(page: Int): TrendingMoviesResponseDto {
            failure?.let { throw it }
            requestedPages += page
            return TrendingMoviesResponseDto(page = page, results = trendingPage(page))
        }

        override suspend fun getMovieDetail(movieId: Int): MovieDetailDto {
            failure?.let { throw it }
            requestedDetailId = movieId
            return detail
        }

        override suspend fun getGenres(): GenreListResponseDto {
            failure?.let { throw it }
            return GenreListResponseDto(genres)
        }
    }
}
