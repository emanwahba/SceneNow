package com.emanwahba.scenenow.core.domain.repository

import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.MovieDetail
import com.emanwahba.scenenow.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getTrendingMovies(): Flow<Result<List<Movie>>>
    suspend fun getMovieDetail(movieId: Int): Result<MovieDetail>
    fun getGenres(): Flow<Result<List<Genre>>>
}
