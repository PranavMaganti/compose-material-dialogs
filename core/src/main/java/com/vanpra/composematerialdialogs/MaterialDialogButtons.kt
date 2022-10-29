package com.vanpra.composematerialdialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle

internal enum class MaterialDialogButtonTypes(val testTag: String) {
    Text("text"), Positive("positive"), Negative("negative"), Accessibility("accessibility")
}

/**
 *  Adds buttons to the bottom of the dialog
 * @param content the buttons which should be displayed in the dialog.
 * See [MaterialDialogButtons] for more information about the content
 */
@Composable
internal fun MaterialDialogScope.DialogButtonsLayout(
    modifier: Modifier = Modifier, content: @Composable MaterialDialogButtons.() -> Unit
) {
    val interButtonPadding =
        with(LocalDensity.current) { DialogConstants.PaddingBetweenButtons.roundToPx() }
    val defaultButtonHeight =
        with(LocalDensity.current) { DialogConstants.ButtonHeight.roundToPx() }

    Layout({ content(dialogButtons) }, modifier.fillMaxWidth(), { measurables, constraints ->

        if (measurables.isEmpty()) {
            return@Layout layout(0, 0) {}
        }

        val placeables = measurables.map {
            (it.layoutId as MaterialDialogButtonTypes) to it.measure(
                constraints.copy(minWidth = 0, maxHeight = defaultButtonHeight)
            )
        }
        val totalWidth = placeables.sumOf { it.second.width }
        val column = totalWidth > DialogConstants.ButtonColumnRatio * constraints.maxWidth

        val height = if (column) {
            val buttonHeight = placeables.sumOf { it.second.height }
            val heightPadding = (placeables.size - 1) * interButtonPadding
            buttonHeight + heightPadding
        } else {
            defaultButtonHeight
        }

        layout(constraints.maxWidth, height) {
            var currX = constraints.maxWidth
            var currY = 0

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

            if (accButtons.isNotEmpty()) {/* There can only be one accessibility button so take first */
                val button = accButtons[0]
                button.place(0, height - button.height)
            }
        }
    })
}

/**
 * A class used to build a buttons layout for a MaterialDialog. This should be used in conjunction
 * with the [com.vanpra.composematerialdialogs.MaterialDialog.dialogButtons] function
 */
class MaterialDialogButtons(private val scope: MaterialDialogScope) {
    /**
     * Adds a button which is always enabled to the bottom of the dialog. This should
     * only be used for neutral actions.
     *
     * @param text the string literal text shown in the button
     * @param res the string resource text shown in the button
     * @param onClick a callback which is called when the button is pressed
     */
    @Composable
    fun button(
        text: String? = null,
        textStyle: TextStyle = MaterialTheme.typography.labelLarge,
        textColor: Color = MaterialTheme.colorScheme.primary,
        @StringRes res: Int? = null,
        onClick: () -> Unit = {}
    ) {
        val buttonText = getString(res, text)
        TextButton(
            onClick = {
                onClick()
            },
            modifier = Modifier
                .layoutId(MaterialDialogButtonTypes.Text)
                .testTag(MaterialDialogButtonTypes.Text.testTag)
        ) {
            Text(text = buttonText, style = textStyle, color = textColor)
        }
    }

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
        textStyle: TextStyle = MaterialTheme.typography.labelLarge,
        textColor: Color = MaterialTheme.colorScheme.primary,
        @StringRes res: Int? = null,
        onClick: () -> Unit = {}
    ) {
        val buttonText = getString(res, text)
        val buttonEnabled = scope.positiveButtonEnabled.values.all { it }

        TextButton(
            onClick = {
                scope.callbacks.values.forEach {
                    it()
                }
                onClick()
                this.scope.dialogState.hide()
            },
            modifier = Modifier
                .layoutId(MaterialDialogButtonTypes.Positive)
                .testTag(MaterialDialogButtonTypes.Positive.testTag),
            enabled = buttonEnabled
        ) {
            Text(text = buttonText, style = textStyle, color = textColor)
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
        textStyle: TextStyle = MaterialTheme.typography.labelLarge,
        textColor: Color = MaterialTheme.colorScheme.primary,
        @StringRes res: Int? = null,
        onClick: () -> Unit = {}
    ) {
        val buttonText = getString(res, text)

        TextButton(
            onClick = {
                onClick()
                this.scope.dialogState.hide()
            },
            modifier = Modifier
                .layoutId(MaterialDialogButtonTypes.Negative)
                .testTag(MaterialDialogButtonTypes.Negative.testTag)
        ) {
            Text(text = buttonText, style = textStyle, color = textColor)
        }
    }

    /**
     * Adds a accessibility button to the bottom left of the dialog
     *
     * @param icon the icon to be shown on the button
     * @param onClick a callback which is called when the button is pressed
     */
    @Composable
    fun accessibilityButton(
        icon: ImageVector,
        colorFilter: ColorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        onClick: () -> Unit
    ) {
        IconButton(
            modifier = Modifier
                .layoutId(MaterialDialogButtonTypes.Accessibility)
                .testTag(MaterialDialogButtonTypes.Accessibility.testTag), onClick = onClick
        ) {
            Image(
                icon,
                contentDescription = null,
                modifier = Modifier.size(DialogConstants.IconSize),
                colorFilter = colorFilter
            )
        }
    }
}
