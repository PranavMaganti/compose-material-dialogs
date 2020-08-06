package com.vanpra.composematerialdialogsdemos.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = blue500,
    primaryVariant = blue700,
    onPrimary = Color.White,
    secondary = teal200
)

private val LightColorPalette = lightColors(
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
