package com.emanwahba.scenenow.feature.movieslist

import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.model.Movie
import com.emanwahba.scenenow.core.domain.model.SortOption

data class MoviesListUiState(
    val isLoading: Boolean = true,
    val allMovies: List<Movie> = emptyList(),
    val visibleMovies: List<Movie> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val selectedGenreId: Int? = null,
    val sortOption: SortOption = SortOption(),
    val errorMessage: String? = null,
)
