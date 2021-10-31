package com.vanpra.composematerialdialogdemos.demos

import androidx.compose.runtime.Composable
import com.vanpra.composematerialdialogdemos.DialogAndShowButton
import com.vanpra.composematerialdialogs.MaterialDialogButtons
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.title

/**
 * @brief Color Picker Demos
 */
@Composable
fun ColorDialogDemo() {
    DialogAndShowButton(
        buttonText = "Color Picker Dialog",
        buttons = { defaultColorDialogButtons() }
    ) {
        title("Select a Color")
        colorChooser(colors = ColorPalette.Primary) {
            println(it)
        }
    }

    DialogAndShowButton(
        buttonText = "Color Picker Dialog With Sub Colors",
        buttons = { defaultColorDialogButtons() }
    ) {
        title("Select a Sub Color")
        colorChooser(
            colors = ColorPalette.Primary,
            subColors = ColorPalette.PrimarySub,
        ) {
            println(it)
        }
    }

    DialogAndShowButton(
        buttonText = "Color Picker Dialog With Initial Selection",
        buttons = { defaultColorDialogButtons() }
    ) {
        title("Select a Sub Color")
        colorChooser(
            colors = ColorPalette.Primary,
            subColors = ColorPalette.PrimarySub,
            initialSelection = 5
        ) {
            println(it)
        }
    }

    DialogAndShowButton(
        buttonText = "Color Picker Dialog With RGB Selector",
        buttons = { defaultColorDialogButtons() }
    ) {
        title("Custom RGB")
        colorChooser(
            colors = ColorPalette.Primary,
            subColors = ColorPalette.PrimarySub,
            argbPickerState = ARGBPickerState.WithoutAlphaSelector
        ) {
            println(it)
        }
    }

    DialogAndShowButton(
        buttonText = "Color Picker Dialog With ARGB Selector",
        buttons = { defaultColorDialogButtons() }
    ) {
        title("Custom ARGB")
        colorChooser(
            colors = ColorPalette.Primary,
            subColors = ColorPalette.PrimarySub,
            argbPickerState = ARGBPickerState.WithAlphaSelector
        ) {
            println(it)
        }
    }
}

@Composable
private fun MaterialDialogButtons.defaultColorDialogButtons() {
    positiveButton("Select")
    negativeButton("Cancel")
}
