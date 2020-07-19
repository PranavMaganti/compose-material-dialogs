package com.vanpra.composematerialdialogs.ui

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette

private val DarkColorPalette = darkColorPalette(
    primary = blue500,
    primaryVariant = blue700,
    onPrimary = Color.White,
    secondary = teal200
)

private val LightColorPalette = lightColorPalette(
    primary = blue500,
    primaryVariant = blue700,
    onPrimary = Color.White,
    secondary = teal200
)

@Composable
internal fun ComposeMaterialDialogsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
