package com.emanwahba.scenenow.core.data.mapper

import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.network.dto.GenreDto
import com.emanwahba.scenenow.core.network.dto.MovieDetailDto
import com.emanwahba.scenenow.core.network.dto.MovieDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieMapperTest {

    @Test
    fun `movie dto maps all fields and builds the poster url`() {
        val movie = MovieDto(
            id = 1,
            title = "Title",
            posterPath = "/poster.jpg",
            genreIds = listOf(1, 2),
            popularity = 9.5,
            releaseDate = "2024-05-01",
            overview = "Overview",
        ).toDomain()

        assertEquals(1, movie.id)
        assertEquals("Title", movie.title)
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", movie.posterUrl)
        assertEquals(listOf(1, 2), movie.genreIds)
        assertEquals(9.5, movie.popularity, 0.0)
        assertEquals("2024-05-01", movie.releaseDate)
        assertEquals("Overview", movie.description)
    }

    @Test
    fun `movie without a poster has no poster url`() {
        assertNull(MovieDto(id = 1, title = "T").toDomain().posterUrl)
    }

    @Test
    fun `genre dto maps to domain genre`() {
        assertEquals(Genre(28, "Action"), GenreDto(28, "Action").toDomain())
    }

    @Test
    fun `movie detail dto maps all fields`() {
        val detail = MovieDetailDto(
            id = 5,
            title = "Detail",
            tagline = "A tagline",
            posterPath = "/p.jpg",
            genres = listOf(GenreDto(1, "Action")),
            overview = "Story",
            voteAverage = 7.8,
            voteCount = 120,
            budget = 1_000,
            revenue = 5_000,
            status = "Released",
            imdbId = "tt1234567",
            runtime = 130,
            releaseDate = "2020-01-01",
        ).toDomain()

        assertEquals("A tagline", detail.tagline)
        assertEquals("https://image.tmdb.org/t/p/w500/p.jpg", detail.posterUrl)
        assertEquals(listOf(Genre(1, "Action")), detail.genres)
        assertEquals("Story", detail.description)
        assertEquals(7.8, detail.voteAverage, 0.0)
        assertEquals(120, detail.voteCount)
        assertEquals(1_000L, detail.budget)
        assertEquals(5_000L, detail.revenue)
        assertEquals("Released", detail.status)
        assertEquals("https://www.imdb.com/title/tt1234567", detail.imdbUrl)
        assertEquals(130, detail.runtimeMinutes)
        assertEquals("2020-01-01", detail.releaseDate)
    }

    @Test
    fun `blank tagline becomes null`() {
        assertNull(MovieDetailDto(id = 1, title = "T", tagline = "  ").toDomain().tagline)
    }

    @Test
    fun `missing imdb id and poster give null urls`() {
        val detail = MovieDetailDto(id = 1, title = "T").toDomain()

        assertNull(detail.imdbUrl)
        assertNull(detail.posterUrl)
    }
}
