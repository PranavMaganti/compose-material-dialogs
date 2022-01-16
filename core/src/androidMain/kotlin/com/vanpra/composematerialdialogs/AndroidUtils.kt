package com.vanpra.composematerialdialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy

// Screen Configuration

@Composable
internal actual fun rememberScreenConfiguration(): ScreenConfiguration {
    val config = LocalConfiguration.current
    return remember(config.screenWidthDp, config.screenHeightDp) {
        ScreenConfiguration(
            screenWidthDp = config.screenWidthDp,
            screenHeightDp = config.screenHeightDp
        )
    }
}

@Composable
internal actual fun isSmallDevice(): Boolean {
    return LocalConfiguration.current.screenWidthDp <= 360
}

@Composable
internal actual fun isLargeDevice(): Boolean {
    return LocalConfiguration.current.screenWidthDp <= 600
}

// Dialog

private fun SecurePolicy.toSecureFlagPolicy(): SecureFlagPolicy {
    return when (this) {
        SecurePolicy.Inherit -> SecureFlagPolicy.Inherit
        SecurePolicy.SecureOn -> SecureFlagPolicy.SecureOn
        SecurePolicy.SecureOff -> SecureFlagPolicy.SecureOff
    }
}

@Composable
internal actual fun DialogBox(
    onDismissRequest: () -> Unit,
    properties: MaterialDialogProperties,
    content: @Composable () -> Unit
) = Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(
        dismissOnBackPress = properties.dismissOnBackPress,
        dismissOnClickOutside = properties.dismissOnClickOutside,
        securePolicy = properties.securePolicy.toSecureFlagPolicy(),
        usePlatformDefaultWidth = properties.usePlatformDefaultWidth
    ),
    content = content
)