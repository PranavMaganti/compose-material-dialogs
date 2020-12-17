package com.vanpra.composematerialdialogdemos.demos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vanpra.composematerialdialogdemos.DialogAndShowButton
import com.vanpra.composematerialdialogdemos.R

/**
 * @brief Basic Dialog Demos
 */
@Composable
fun BasicDialogDemo() {
    DialogAndShowButton(buttonText = "Basic Dialog") {
        title(res = R.string.location_dialog_title)
        message(res = R.string.location_dialog_message)
    }

    DialogAndShowButton(buttonText = "Basic Dialog With Buttons") {
        title(res = R.string.location_dialog_title)
        message(res = R.string.location_dialog_message)
        buttons {
            negativeButton("Disagree")
            positiveButton("Agree")
        }
    }

    DialogAndShowButton(buttonText = "Basic Dialog With Buttons and Icon Title") {
        iconTitle(
            iconAsset = Icons.Default.LocationOn,
            textRes = R.string.location_dialog_title
        )
        message(res = R.string.location_dialog_message)
        buttons {
            negativeButton("Disagree")
            positiveButton("Agree")
        }
    }

    DialogAndShowButton(buttonText = "Basic Dialog With Stacked Buttons") {
        title(res = R.string.location_dialog_title)
        message(res = R.string.location_dialog_message)
        buttons {
            negativeButton("No Thanks")
            positiveButton("Turn On Speed Boost")
        }
    }

    DialogAndShowButton(buttonText = "Basic Dialog With input") {
        title(res = R.string.input_dialog_title)
        input(label = "Name", hint = "Jon Smith")
        buttons {
            negativeButton("Cancel")
            positiveButton("Ok")
        }
    }
}
