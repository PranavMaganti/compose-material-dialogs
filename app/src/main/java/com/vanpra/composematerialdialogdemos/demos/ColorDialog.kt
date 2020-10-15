package com.vanpra.composematerialdialogdemos.demos

import androidx.compose.runtime.Composable
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogdemos.DialogAndShowButton

/**
 * @brief Color Picker Demos
 */
@Composable
fun ColorDialogDemo() {
    DialogAndShowButton(buttonText = "Color Picker Dialog") {
        title("Select a Color")
        colorChooser(colors = ColorPalette.Primary)
        colorChooserButtons()
    }

    DialogAndShowButton(buttonText = "Color Picker Dialog With Sub Colors") {
        title("Select a Sub Color")
        colorChooser(colors = ColorPalette.Primary, subColors = ColorPalette.PrimarySub)
        colorChooserButtons()
    }

    DialogAndShowButton(buttonText = "Color Picker Dialog With ARGB Selector") {
        title("Custom ARGB")
        colorChooser(
            colors = ColorPalette.Primary,
            subColors = ColorPalette.PrimarySub,
            allowCustomArgb = true
        )
        colorChooserButtons()
    }
}

@Composable
private fun MaterialDialog.colorChooserButtons() {
    buttons {
        positiveButton("Select")
        negativeButton("Cancel")
    }
}
