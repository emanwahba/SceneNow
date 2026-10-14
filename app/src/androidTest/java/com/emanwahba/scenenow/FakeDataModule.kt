package com.emanwahba.scenenow

import com.emanwahba.scenenow.core.data.di.DataModule
import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.MovieDetail
import com.emanwahba.scenenow.core.domain.repository.MovieRepository
import com.emanwahba.scenenow.core.domain.util.DataResult
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Singleton

object FakeMovieData {
    val movies = listOf(
        Movie(1, "Galactic Heist", null, listOf(28), 90.0, "2024-03-01", "A crew plans one last job."),
        Movie(2, "Quiet Harbor", null, listOf(18), 40.0, "2023-09-12", "A fisherman returns home."),
    )
    val genres = listOf(Genre(28, "Action"), Genre(18, "Drama"))

    fun detail(id: Int) = MovieDetail(
        id = id,
        title = movies.first { it.id == id }.title,
        tagline = "Fake tagline",
        posterUrl = null,
        genres = genres,
        description = "Detailed overview for movie $id.",
        voteAverage = 7.5,
        voteCount = 100,
        budget = 1_000_000,
        revenue = 2_000_000,
        status = "Released",
        imdbUrl = null,
        runtimeMinutes = 120,
        releaseDate = "2024-03-01",
    )
}

private class FakeMovieRepository : MovieRepository {
    override fun getTrendingMovies(): Flow<DataResult<List<Movie>>> =
        flowOf(DataResult.Success(FakeMovieData.movies))

    override suspend fun getMovieDetail(movieId: Int): DataResult<MovieDetail> =
        DataResult.Success(FakeMovieData.detail(movieId))

    override fun getGenres(): Flow<DataResult<List<Genre>>> =
        flowOf(DataResult.Success(FakeMovieData.genres))
}

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DataModule::class])
object FakeDataModule {
    @Provides
    @Singleton
    fun provideMovieRepository(): MovieRepository = FakeMovieRepository()
}
