package com.vanpra.composematerialdialogdemos.demos

import androidx.compose.runtime.Composable
import com.vanpra.composematerialdialogdemos.DialogAndShowButton
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser

/**
 * @brief Color Picker Demos
 */
@Composable
fun ColorDialogDemo() {
    DialogAndShowButton(buttonText = "Color Picker Dialog") {
        title("Select a Color")
        colorChooser(colors = ColorPalette.Primary, waitForPositiveButton = true) {
            println(it)
        }
        buttons {
            positiveButton("Select")
            negativeButton("Cancel")
        }
    }

    DialogAndShowButton(buttonText = "Color Picker Dialog With Sub Colors") {
        title("Select a Sub Color")
        colorChooser(colors = ColorPalette.Primary, subColors = ColorPalette.PrimarySub)
        buttons {
            positiveButton("Select")
            negativeButton("Cancel")
        }
    }

    DialogAndShowButton(buttonText = "Color Picker Dialog With ARGB Selector") {
        title("Custom ARGB")
        colorChooser(
            colors = ColorPalette.Primary,
            subColors = ColorPalette.PrimarySub,
            allowCustomArgb = true
        )
        buttons {
            positiveButton("Select")
            negativeButton("Cancel")
        }
    }
}