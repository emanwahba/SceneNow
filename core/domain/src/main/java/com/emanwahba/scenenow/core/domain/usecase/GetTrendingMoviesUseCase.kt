package com.emanwahba.scenenow.core.domain.usecase

import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.SortDirection
import com.emanwahba.scenenow.core.domain.model.SortField
import com.emanwahba.scenenow.core.domain.model.SortOption
import com.emanwahba.scenenow.core.domain.repository.MovieRepository
import com.emanwahba.scenenow.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrendingMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    operator fun invoke(): Flow<Result<List<Movie>>> = repository.getTrendingMovies()

    fun applyFilterAndSort(
        movies: List<Movie>,
        genreId: Int?,
        sort: SortOption,
    ): List<Movie> = sortMovies(filterByGenre(movies, genreId), sort)

    private fun filterByGenre(movies: List<Movie>, genreId: Int?): List<Movie> =
        if (genreId == null) movies else movies.filter { genreId in it.genreIds }

    private fun sortMovies(movies: List<Movie>, sort: SortOption): List<Movie> {
        val comparator = when (sort.field) {
            SortField.POPULARITY -> compareBy<Movie> { it.popularity }
            SortField.TITLE -> compareBy { it.title.lowercase() }
            SortField.RELEASE_DATE -> compareBy { it.releaseDate.orEmpty() }
        }
        val sorted = movies.sortedWith(comparator)
        return if (sort.direction == SortDirection.DESCENDING) sorted.reversed() else sorted
    }
}
