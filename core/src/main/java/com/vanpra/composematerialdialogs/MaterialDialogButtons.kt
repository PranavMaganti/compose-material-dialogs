package com.vanpra.composematerialdialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import java.util.Locale

internal enum class MaterialDialogButtonTypes(val testTag: String) {
    Text("text"),
    Positive("positive"),
    Negative("negative"),
    Accessibility("accessibility")
}

/**
 *  Adds buttons to the bottom of the dialog
 * @param content the buttons which should be displayed in the dialog.
 * See [MaterialDialogButtons] for more information about the content
 */
@Composable
internal fun MaterialDialogScope.DialogButtonsLayout(
    modifier: Modifier = Modifier,
    content: @Composable (MaterialDialogButtons.() -> Unit)?
) {
    if (content != null) {
        val interButtonPadding = with(LocalDensity.current) { 12.dp.toPx().toInt() }
        val defaultButtonHeight = with(LocalDensity.current) { 36.dp.toPx().toInt() }
        val accessibilityPadding = with(LocalDensity.current) { 12.dp.toPx().toInt() }
        val verticalPadding = with(LocalDensity.current) { 8.dp.toPx().toInt() }

        Layout(
            { content(dialogButtons) },
            modifier
                .fillMaxWidth()
                .padding(24.dp),
            { measurables, constraints ->

                if (measurables.isEmpty()) {
                    return@Layout layout(0, 0) {}
                }

                val placeables = measurables.map {
                    (it.layoutId as MaterialDialogButtonTypes) to it.measure(
                        constraints.copy(minWidth = 0, maxHeight = defaultButtonHeight)
                    )
                }
                val totalWidth = placeables.sumOf { it.second.width }
                val column = totalWidth > 0.8 * constraints.maxWidth

                val height =
                    if (column) {
                        val buttonHeight = placeables.sumOf { it.second.height }
                        val heightPadding = (placeables.size - 1) * interButtonPadding
                        buttonHeight + heightPadding + 2 * verticalPadding
                    } else {
                        placeables[0].second.height
                    }

                layout(constraints.maxWidth, height) {
                    var currX = constraints.maxWidth
                    var currY = verticalPadding

                    val posButtons = placeables.buttons(MaterialDialogButtonTypes.Positive)
                    val negButtons = placeables.buttons(MaterialDialogButtonTypes.Negative)
                    val textButtons = placeables.buttons(MaterialDialogButtonTypes.Text)
                    val accButtons = placeables.buttons(MaterialDialogButtonTypes.Accessibility)

                    val buttonInOrder = posButtons + textButtons + negButtons
                    buttonInOrder.forEach { button ->
                        if (column) {
                            button.place(currX - button.width, currY)
                            currY += button.height + interButtonPadding
                        } else {
                            currX -= button.width
                            button.place(currX, currY)
                        }
                    }

                    if (accButtons.isNotEmpty()) {
                        /* There can only be one accessibility button so take first */
                        val button = accButtons[0]
                        button.place(accessibilityPadding, height - button.height)
                    }
                }
            }
        )
    } else {
        Box(Modifier)
    }
}

/**
 * A class used to build a buttons layout for a MaterialDialog. This should be used in conjunction
 * with the [com.vanpra.composematerialdialogs.MaterialDialog.dialogButtons] function
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
                if (scope.autoDismiss && !disableDismiss) {
                    scope.dialogState.hide(focusManager)
                }

                scope.callbacks.values.forEach {
                    it()
                }

                onClick()
            },
            modifier = Modifier
                .layoutId(MaterialDialogButtonTypes.Positive)
                .testTag(MaterialDialogButtonTypes.Positive.testTag),
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
                if (scope.autoDismiss) {
                    scope.dialogState.hide(focusManager)
                }
                onClick()
            },
            modifier = Modifier
                .layoutId(MaterialDialogButtonTypes.Negative)
                .testTag(MaterialDialogButtonTypes.Negative.testTag),
        ) {
            Text(text = buttonText, style = textStyle)
        }
    }
}
