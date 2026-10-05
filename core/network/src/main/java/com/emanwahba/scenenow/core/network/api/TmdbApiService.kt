package com.emanwahba.scenenow.core.network.api

import com.emanwahba.scenenow.core.network.dto.GenreListResponseDto
import com.emanwahba.scenenow.core.network.dto.MovieDetailDto
import com.emanwahba.scenenow.core.network.dto.TrendingMoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 1,
    ): TrendingMoviesResponseDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
    ): MovieDetailDto

    @GET("genre/movie/list")
    suspend fun getGenres(): GenreListResponseDto
}
