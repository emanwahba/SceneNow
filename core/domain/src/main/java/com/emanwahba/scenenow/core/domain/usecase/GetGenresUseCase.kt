package com.emanwahba.scenenow.core.domain.usecase

import com.emanwahba.scenenow.core.domain.model.Genre
import com.emanwahba.scenenow.core.domain.repository.MovieRepository
import com.emanwahba.scenenow.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    operator fun invoke(): Flow<Result<List<Genre>>> = repository.getGenres()
}
