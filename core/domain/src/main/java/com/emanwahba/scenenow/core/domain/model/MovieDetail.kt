package com.emanwahba.scenenow.core.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val tagline: String?,
    val posterUrl: String?,
    val genres: List<Genre>,
    val description: String,
    val voteAverage: Double,
    val voteCount: Int,
    val budget: Long,
    val revenue: Long,
    val status: String,
    val imdbUrl: String?,
    val runtimeMinutes: Int?,
    val releaseDate: String?,
)
