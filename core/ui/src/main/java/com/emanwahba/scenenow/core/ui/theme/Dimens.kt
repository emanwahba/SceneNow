package com.emanwahba.scenenow.core.ui.theme

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/** Shared spacing scale so margins/padding stay consistent across screens. */
object Spacing {
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val extraLarge = 24.dp
}

object Elevation {
    val card = 3.dp
}

/** Poster sizes keep the standard 2:3 movie-poster ratio. */
object PosterSize {
    val list = DpSize(110.dp, 165.dp)
    val detail = DpSize(140.dp, 210.dp)
}
