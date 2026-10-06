package com.emanwahba.scenenow.core.domain.usecase

import com.emanwahba.scenenow.core.domain.model.MovieDetail
import com.emanwahba.scenenow.core.domain.repository.MovieRepository
import com.emanwahba.scenenow.core.domain.util.DataResult
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    suspend operator fun invoke(movieId: Int): DataResult<MovieDetail> =
        repository.getMovieDetail(movieId)
}
