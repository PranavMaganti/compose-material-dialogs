package com.vanpra.composematerialdialogs

import androidx.annotation.StringRes
import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.tag
import androidx.ui.foundation.Text
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TextButton

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
        val buttonText = ContextAmbient.current.getString(res, text)

        TextButton(
            onClick = {
                if (dialog.isAutoDismiss() && !disableDismiss) {
                    dialog.hide()
                }

                dialog.callbacks.forEach {
                    it()
                }

                onClick()
            }, modifier = Modifier.tag("button_${buttonsTagOrder.size}"),
            enabled = dialog.positiveEnabled.value.all { it }
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
        val buttonText = ContextAmbient.current.getString(res, text)
        TextButton(onClick = {
            if (dialog.isAutoDismiss()) {
                dialog.hide()
            }
            onClick()
        }, modifier = Modifier.tag("button_${buttonsTagOrder.size}")) {
            Text(text = buttonText, style = MaterialTheme.typography.button)
        }

        remember {
            buttonsTagOrder.add(buttonsTagOrder.size)
        }
    }
}