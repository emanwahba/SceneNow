package com.emanwahba.scenenow.feature.moviedetail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.emanwahba.scenenow.feature.moviedetail.MovieDetailRoute

private const val MOVIE_DETAIL_ROUTE_BASE = "movie_detail"
const val MOVIE_DETAIL_ROUTE = "$MOVIE_DETAIL_ROUTE_BASE/{movieId}"

fun movieDetailRoute(movieId: Int) = "$MOVIE_DETAIL_ROUTE_BASE/$movieId"

fun NavGraphBuilder.movieDetailScreen(onBackClick: () -> Unit) {
    composable(
        route = MOVIE_DETAIL_ROUTE,
        arguments = listOf(navArgument("movieId") { type = NavType.IntType }),
    ) {
        MovieDetailRoute(onBackClick = onBackClick)
    }
}
