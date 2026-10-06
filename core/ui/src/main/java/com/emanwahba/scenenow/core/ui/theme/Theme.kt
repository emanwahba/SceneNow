package com.emanwahba.scenenow.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColors = darkColorScheme(
    primary = SceneNowBlue80,
    onPrimary = Color(0xFF002D6E),
    primaryContainer = SceneNowBlueContainerDark,
    onPrimaryContainer = SceneNowOnBlueContainerDark,
    secondary = SceneNowAmber80,
    onSecondary = Color(0xFF4A2800),
    secondaryContainer = SceneNowAmberContainerDark,
    onSecondaryContainer = SceneNowOnAmberContainerDark,
    background = SceneNowBackgroundDark,
    onBackground = SceneNowOnSurfaceDark,
    surface = SceneNowSurfaceDark,
    onSurface = SceneNowOnSurfaceDark,
    surfaceVariant = SceneNowSurfaceVariantDark,
    onSurfaceVariant = SceneNowOnSurfaceVariantDark,
    error = SceneNowErrorRed,
)

private val LightColors = lightColorScheme(
    primary = SceneNowBlue40,
    onPrimary = Color.White,
    primaryContainer = SceneNowBlueContainerLight,
    onPrimaryContainer = SceneNowOnBlueContainerLight,
    secondary = SceneNowAmber40,
    onSecondary = Color.White,
    secondaryContainer = SceneNowAmberContainerLight,
    onSecondaryContainer = SceneNowOnAmberContainerLight,
    background = SceneNowBackgroundLight,
    onBackground = SceneNowOnSurfaceLight,
    surface = SceneNowSurfaceLight,
    onSurface = SceneNowOnSurfaceLight,
    surfaceVariant = SceneNowSurfaceVariantLight,
    onSurfaceVariant = SceneNowOnSurfaceVariantLight,
    error = SceneNowErrorRed,
)

private val SceneNowShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
)

@Composable
fun SceneNowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = SceneNowTypography,
        shapes = SceneNowShapes,
        content = content,
    )
}

/** Shared top bar look so every screen's app bar matches. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun sceneNowTopAppBarColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.background,
    titleContentColor = MaterialTheme.colorScheme.primary,
    navigationIconContentColor = MaterialTheme.colorScheme.primary,
    actionIconContentColor = MaterialTheme.colorScheme.primary,
)
