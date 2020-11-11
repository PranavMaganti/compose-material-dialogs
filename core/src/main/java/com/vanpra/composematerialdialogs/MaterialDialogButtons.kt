package com.vanpra.composematerialdialogs

import androidx.annotation.StringRes
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.ContextAmbient
import java.util.Locale

/**
 * A class used to build a buttons layout for a MaterialDialog. This should be used in conjunction
 * with the [com.vanpra.composematerialdialogs.MaterialDialog.buttons] function
 */
class MaterialDialogButtons(private val dialog: MaterialDialog) {
    val buttonsTagOrder = mutableListOf<Int>()

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
    fun MaterialDialogButtons.positiveButton(
        text: String? = null,
        @StringRes res: Int? = null,
        disableDismiss: Boolean = false,
        onClick: () -> Unit = {}
    ) {
        val buttonText = ContextAmbient.current.getString(res, text).toUpperCase(Locale.ROOT)
        val buttonId = remember { buttonsTagOrder.size }

        TextButton(
            onClick = {
                if (dialog.isAutoDismiss() && !disableDismiss) {
                    dialog.hide()
                }

                dialog.callbacks.forEach {
                    it()
                }

                onClick()
            },
            modifier = Modifier.layoutId("button_$buttonId"),
            enabled = dialog.positiveEnabled.all { it }
        ) {
            Text(text = buttonText, style = MaterialTheme.typography.button)
        }

        remember {
            buttonsTagOrder.add(0, buttonsTagOrder.size)
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
    fun MaterialDialogButtons.negativeButton(
        text: String? = null,
        @StringRes res: Int? = null,
        onClick: () -> Unit = {}
    ) {
        val buttonText = ContextAmbient.current.getString(res, text).toUpperCase(Locale.ROOT)
        val buttonId = remember { buttonsTagOrder.size }

        TextButton(
            onClick = {
                if (dialog.isAutoDismiss()) {
                    dialog.hide()
                }
                onClick()
            },
            modifier = Modifier.layoutId("button_$buttonId")
        ) {
            Text(text = buttonText, style = MaterialTheme.typography.button)
        }

        remember {
            buttonsTagOrder.add(buttonsTagOrder.size)
        }
    }
}
