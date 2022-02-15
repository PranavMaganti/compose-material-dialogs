package com.vanpra.composematerialdialogs

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import java.util.Locale

object MaterialDialogButtonId {
    const val Positive = "positive_dialog_btn"
    const val Negative = "negative_dialog_btn"

    val Ids = setOf(Positive, Negative)
}

object MaterialDialogButtonConstants {
    val OuterButtonPadding = 16.dp
    val InterButtonPadding = 8.dp
    val MaxHeight = 40.dp
    const val ButtonStackThreshold = 0.8
}

/**
 * A class used to build a buttons layout for a MaterialDialog
 */
class MaterialDialogButtons(private val scope: MaterialDialogScope) {
    /**
     * Adds a positive button to the dialog
     *
     * @param text the string literal text shown in the button
     * @param res the string resource text shown in the button
     * @param disableDismiss when true this will stop the dialog closing when the button is pressed
     * even if autoDismissing is disabled
     * @param onClick a callback which is called when the button is pressed
     */
    @Composable
    fun positiveButton(
        text: String? = null,
        textStyle: TextStyle =
            MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary),
        @StringRes res: Int? = null,
        disableDismiss: Boolean = false,
        onClick: () -> Unit = {}
    ) {
        val buttonText = getString(res, text).uppercase(Locale.ROOT)
        val buttonEnabled = scope.positiveButtonEnabled.values.all { it }
        val focusManager = LocalFocusManager.current

        TextButton(
            onClick = {
                if (!disableDismiss) {
                    scope.dialogState.hide(focusManager)
                }

                scope.callbacks.values.forEach {
                    it()
                }

                onClick()
            },
            modifier = Modifier
                .layoutId(MaterialDialogButtonId.Positive)
                .testTag(MaterialDialogButtonId.Positive),
            enabled = buttonEnabled
        ) {
            Text(text = buttonText, style = textStyle)
        }
    }

    /**
     * Adds a negative button to the dialog
     *
     * @param text the string literal text shown in the button
     * @param res the string resource text shown in the button
     * even if autoDismissing is disabled
     * @param onClick a callback which is called when the button is pressed
     */
    @Composable
    fun negativeButton(
        text: String? = null,
        textStyle: TextStyle =
            MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary),
        @StringRes res: Int? = null,
        onClick: () -> Unit = {}
    ) {
        val buttonText = getString(res, text).uppercase(Locale.ROOT)
        val focusManager = LocalFocusManager.current

        TextButton(
            onClick = {
                scope.dialogState.hide(focusManager)
                onClick()
            },
            modifier = Modifier
                .layoutId(MaterialDialogButtonId.Negative)
                .testTag(MaterialDialogButtonId.Negative),
        ) {
            Text(text = buttonText, style = textStyle)
        }
    }
}
