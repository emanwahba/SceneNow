package com.emanwahba.scenenow.feature.movieslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emanwahba.scenenow.core.domain.model.SortOption
import com.emanwahba.scenenow.core.domain.usecase.GetGenresUseCase
import com.emanwahba.scenenow.core.domain.usecase.GetTrendingMoviesUseCase
import com.emanwahba.scenenow.core.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val getTrendingMovies: GetTrendingMoviesUseCase,
    private val getGenres: GetGenresUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesListUiState())
    val uiState: StateFlow<MoviesListUiState> = _uiState.asStateFlow()

    init {
        loadGenres()
        loadTrendingMovies()
    }

    fun onGenreSelected(genreId: Int?) {
        _uiState.update { it.copy(selectedGenreId = genreId).withVisibleMovies() }
    }

    fun onSortOptionChanged(sortOption: SortOption) {
        _uiState.update { it.copy(sortOption = sortOption).withVisibleMovies() }
    }

    fun onRetry() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadTrendingMovies()
    }

    private fun loadTrendingMovies() {
        viewModelScope.launch {
            getTrendingMovies().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                allMovies = result.data,
                                errorMessage = null,
                            ).withVisibleMovies()
                        }
                    }
                    is Result.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            getGenres().collect { result ->
                // A genre-fetch failure is non-fatal for this screen: the movie list is
                // still useful without filter chips, so we don't surface it as a screen error.
                if (result is Result.Success) {
                    _uiState.update { it.copy(genres = result.data) }
                }
            }
        }
    }

    // Derived in the same update as the change that needs it, so observers never see a state
    // where the filter/sort and the visible list disagree.
    private fun MoviesListUiState.withVisibleMovies(): MoviesListUiState = copy(
        visibleMovies = getTrendingMovies.applyFilterAndSort(
            movies = allMovies,
            genreId = selectedGenreId,
            sort = sortOption,
        ),
    )
}
