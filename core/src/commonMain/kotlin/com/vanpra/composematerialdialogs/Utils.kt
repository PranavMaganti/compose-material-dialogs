package com.vanpra.composematerialdialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

internal fun List<Pair<MaterialDialogButtonTypes, Placeable>>.buttons(type: MaterialDialogButtonTypes) =
    this.filter { it.first == type }.map { it.second }


// Screen Configuration

internal class ScreenConfiguration(val screenWidthDp: Int, val screenHeightDp: Int)

@Composable
internal expect fun rememberScreenConfiguration(): ScreenConfiguration

@Composable
internal expect fun isSmallDevice(): Boolean

@Composable
internal expect fun isLargeDevice(): Boolean

// Dialog

// SecureFlagPolicy For Android Dialogs

enum class SecurePolicy {
    Inherit, //Forces [WindowManager.LayoutParams.FLAG_SECURE]
    SecureOn, // Sets [WindowManager.LayoutParams.FLAG_SECURE] on the window
    SecureOff // No [WindowManager.LayoutParams.FLAG_SECURE] will be set on the window
}

// Desktop Window Position

fun DesktopWindowPosition(x: Dp, y: Dp): DesktopWindowPosition = DesktopWindowPosition.Absolute(x = x, y = y)
fun DesktopWindowPosition(alignment: Alignment): DesktopWindowPosition = DesktopWindowPosition.Aligned(alignment)

sealed class DesktopWindowPosition {
    @Stable
    abstract val x: Dp

    @Stable
    abstract val y: Dp

    @Stable
    abstract val isSpecified: Boolean

    object PlatformDefault : DesktopWindowPosition() {
        override val x: Dp get() = Dp.Unspecified
        override val y: Dp get() = Dp.Unspecified
        override val isSpecified: Boolean get() = false
    }

    class Absolute(override val x: Dp, override val y: Dp) : DesktopWindowPosition() {
        override val isSpecified: Boolean = true
    }

    class Aligned(val alignment: Alignment) : DesktopWindowPosition() {
        override val x: Dp get() = Dp.Unspecified
        override val y: Dp get() = Dp.Unspecified
        override val isSpecified: Boolean get() = false
    }
}

class MaterialDialogProperties(
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
    val securePolicy: SecurePolicy = SecurePolicy.Inherit,
    val usePlatformDefaultWidth : Boolean = false,
    val position: DesktopWindowPosition = DesktopWindowPosition(Alignment.Center),
    val size: DpSize = DpSize(400.dp, 300.dp)
)

@Composable
internal expect fun DialogBox(
    onDismissRequest: () -> Unit,
    properties: MaterialDialogProperties,
    content: @Composable () -> Unit,
)