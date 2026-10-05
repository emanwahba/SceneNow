package com.emanwahba.scenenow.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailDto(
    val id: Int,
    val title: String,
    val tagline: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    val genres: List<GenreDto> = emptyList(),
    val overview: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    val budget: Long = 0,
    val revenue: Long = 0,
    val status: String = "",
    @SerialName("imdb_id") val imdbId: String? = null,
    val runtime: Int? = null,
    @SerialName("release_date") val releaseDate: String? = null,
)
