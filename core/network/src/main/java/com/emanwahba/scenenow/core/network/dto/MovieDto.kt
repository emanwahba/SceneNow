package com.emanwahba.scenenow.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("genre_ids") val genreIds: List<Int> = emptyList(),
    val popularity: Double = 0.0,
    @SerialName("release_date") val releaseDate: String? = null,
)

@Serializable
data class TrendingMoviesResponseDto(
    val page: Int,
    val results: List<MovieDto> = emptyList(),
    @SerialName("total_results") val totalResults: Int = 0,
)
