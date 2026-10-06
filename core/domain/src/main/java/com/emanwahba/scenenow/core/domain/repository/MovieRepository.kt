package com.emanwahba.scenenow.core.domain.repository

import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.MovieDetail
import com.emanwahba.scenenow.core.domain.util.DataResult
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getTrendingMovies(): Flow<DataResult<List<Movie>>>
    suspend fun getMovieDetail(movieId: Int): DataResult<MovieDetail>
    fun getGenres(): Flow<DataResult<List<Genre>>>
}
