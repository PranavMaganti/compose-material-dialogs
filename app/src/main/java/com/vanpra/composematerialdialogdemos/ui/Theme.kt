package com.vanpra.composematerialdialogdemos.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.material.color.DynamicColors

@Composable
internal fun ComposeMaterialDialogsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        if (DynamicColors.isDynamicColorAvailable()) {
            dynamicDarkColorScheme(LocalContext.current)
        } else {
            darkColorScheme()
        }
    } else {
        if (DynamicColors.isDynamicColorAvailable()) {
            dynamicLightColorScheme(LocalContext.current)
        } else {
            lightColorScheme()
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content
    )
}
