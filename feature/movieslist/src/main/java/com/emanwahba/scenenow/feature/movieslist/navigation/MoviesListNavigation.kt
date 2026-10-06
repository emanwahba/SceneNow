package com.emanwahba.scenenow.feature.movieslist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.emanwahba.scenenow.feature.movieslist.MoviesListRoute

const val MOVIES_LIST_ROUTE = "movies_list"

fun NavGraphBuilder.moviesListScreen(onMovieClick: (Int) -> Unit) {
    composable(MOVIES_LIST_ROUTE) {
        MoviesListRoute(onMovieClick = onMovieClick)
    }
}
