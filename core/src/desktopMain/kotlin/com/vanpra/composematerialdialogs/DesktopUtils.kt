package com.vanpra.composematerialdialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition

@Composable
internal actual fun rememberScreenConfiguration(): ScreenConfiguration {
    return remember {
        ScreenConfiguration(
            screenWidthDp = 600,
            screenHeightDp = 400
        )
    }
}

@Composable
internal actual fun isSmallDevice(): Boolean {
    return false
}

@Composable
internal actual fun isLargeDevice(): Boolean {
    return true
}

// Dialog

private fun DesktopWindowPosition.toWindowPosition(): WindowPosition {
    return when (this) {
        is DesktopWindowPosition.PlatformDefault -> WindowPosition.PlatformDefault
        is DesktopWindowPosition.Absolute -> WindowPosition(x = x, y = y)
        is DesktopWindowPosition.Aligned -> WindowPosition(alignment)
    }
}

@Composable
internal actual fun DialogBox(
    onDismissRequest: () -> Unit,
    properties: MaterialDialogProperties,
    content: @Composable () -> Unit
) = Dialog(
    onCloseRequest = onDismissRequest,
    state = DialogState(position = properties.position.toWindowPosition(), size = properties.size)
) {
    content()
}