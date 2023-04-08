package com.playlab.superpomodoro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White

private val DarkColorPalette = darkColors(
    primary = Blue200,
    primaryVariant = Blue100,
    secondary = Grape200
)

private val LightColorPalette = lightColors(
    primary = Blue200,
    primaryVariant = Blue100,
    secondary = Grape200,

    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun SuperPomodoroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}