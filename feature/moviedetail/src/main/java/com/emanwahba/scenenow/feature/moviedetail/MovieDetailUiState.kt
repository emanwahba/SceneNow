package com.emanwahba.scenenow.feature.moviedetail

import com.emanwahba.scenenow.core.domain.model.MovieDetail

data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val movie: MovieDetail? = null,
    val errorMessage: String? = null,
)
