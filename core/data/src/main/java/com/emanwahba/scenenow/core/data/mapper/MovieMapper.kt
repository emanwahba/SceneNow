package com.emanwahba.scenenow.core.data.mapper

import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.MovieDetail
import com.emanwahba.scenenow.core.network.dto.GenreDto
import com.emanwahba.scenenow.core.network.dto.MovieDetailDto
import com.emanwahba.scenenow.core.network.dto.MovieDto

private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"
private const val IMDB_BASE_URL = "https://www.imdb.com/title/"

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    posterUrl = posterPath?.let { "$POSTER_BASE_URL$it" },
    genreIds = genreIds,
    popularity = popularity,
    releaseDate = releaseDate,
    description = overview,
)

fun GenreDto.toDomain(): Genre = Genre(id = id, name = name)

fun MovieDetailDto.toDomain(): MovieDetail = MovieDetail(
    id = id,
    title = title,
    tagline = tagline?.takeIf { it.isNotBlank() },
    posterUrl = posterPath?.let { "$POSTER_BASE_URL$it" },
    genres = genres.map { it.toDomain() },
    description = overview,
    voteAverage = voteAverage,
    voteCount = voteCount,
    budget = budget,
    revenue = revenue,
    status = status,
    imdbUrl = imdbId?.let { "$IMDB_BASE_URL$it" },
    runtimeMinutes = runtime,
    releaseDate = releaseDate,
)
