package com.emanwahba.scenenow.core.data.repository

import com.emanwahba.scenenow.core.data.mapper.toDomain
import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.MovieDetail
import com.emanwahba.scenenow.core.domain.repository.MovieRepository
import com.emanwahba.scenenow.core.domain.util.Result
import com.emanwahba.scenenow.core.network.api.TmdbApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApiService,
) : MovieRepository {

    override fun getTrendingMovies(): Flow<Result<List<Movie>>> = flow {
        val result = runCatching {
            // 5 TMDB pages of 20 results each.
            (1..5).flatMap { page -> api.getTrendingMovies(page = page).results }
                // Trending is a live ranking: if it shifts between page requests, a movie can
                // appear on two pages. Movie ids must be unique for the UI list keys.
                .distinctBy { it.id }
                .map { it.toDomain() }
        }
        emit(result.toDomainResult())
    }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> =
        runCatching { api.getMovieDetail(movieId).toDomain() }.toDomainResult()

    override fun getGenres(): Flow<Result<List<Genre>>> = flow {
        val result = runCatching { api.getGenres().genres.map { it.toDomain() } }
        emit(result.toDomainResult())
    }
}

private fun <T> kotlin.Result<T>.toDomainResult(): Result<T> = fold(
    onSuccess = { Result.Success(it) },
    onFailure = { Result.Error(it.toUserMessage(), it) },
)

private fun Throwable.toUserMessage(): String = when (this) {
    is IOException -> "No internet connection. Please check your network and try again."
    is HttpException -> "Something went wrong talking to the server (code ${code()}). Please try again."
    else -> "Something unexpected happened. Please try again."
}
