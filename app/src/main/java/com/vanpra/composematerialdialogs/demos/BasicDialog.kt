package com.vanpra.composematerialdialogs.demos

import androidx.compose.Composable
import com.vanpra.composematerialdialogs.DialogAndShowButton
import com.vanpra.composematerialdialogs.R

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
            iconRes = R.drawable.tick,
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
}
