package com.emanwahba.scenenow.core.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val genreIds: List<Int>,
    val popularity: Double,
    val releaseDate: String?,
    val description: String = "",
)
