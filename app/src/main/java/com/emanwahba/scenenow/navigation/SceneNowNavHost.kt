package com.emanwahba.scenenow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.emanwahba.scenenow.feature.moviedetail.navigation.movieDetailRoute
import com.emanwahba.scenenow.feature.moviedetail.navigation.movieDetailScreen
import com.emanwahba.scenenow.feature.movieslist.navigation.MOVIES_LIST_ROUTE
import com.emanwahba.scenenow.feature.movieslist.navigation.moviesListScreen

@Composable
fun SceneNowNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = MOVIES_LIST_ROUTE) {
        moviesListScreen(
            onMovieClick = { movieId -> navController.navigate(movieDetailRoute(movieId)) },
        )
        movieDetailScreen(
            onBackClick = { navController.popBackStack() },
        )
    }
}
