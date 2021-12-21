package com.vanpra.composematerialdialogdemos.demos

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SecurityUpdateGood
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.ImeAction
import com.vanpra.composematerialdialogdemos.DialogAndShowButton
import com.vanpra.composematerialdialogdemos.R
import com.vanpra.composematerialdialogs.iconTitle
import com.vanpra.composematerialdialogs.input
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.title

/**
 * @brief Basic Dialog Demos
 */
@Composable
fun BasicDialogDemo() {

    DialogAndShowButton(
        buttonText = "Basic Dialog",
        buttons = {
            negativeButton("Disagree")
            positiveButton("Agree")
        }
    ) {
        title(res = R.string.location_dialog_title)
        message(res = R.string.location_dialog_message)
    }

    DialogAndShowButton(
        buttonText = "Basic Dialog With Icon Title",
        buttons = {
            negativeButton("Disagree")
            positiveButton("Agree")
        }
    ) {
        iconTitle(
            icon = {
                Image(
                    Icons.Default.SecurityUpdateGood,
                    contentDescription = "Device tick",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            },
            text = "Dialog with hero icon"
        )
        message(res = R.string.hero_icon_dialog_message)
    }

    DialogAndShowButton(
        buttonText = "Basic Dialog With Stacked Buttons",
        buttons = {
            negativeButton("No Thanks")
            positiveButton("Turn On Speed Boost")
        }
    ) {
        title(res = R.string.location_dialog_title)
        message(res = R.string.location_dialog_message)
    }

    DialogAndShowButton(
        buttonText = "Basic Input Dialog",
        buttons = {
            negativeButton("Cancel")
            positiveButton("Ok")
        }
    ) {
        title(res = R.string.input_dialog_title)
        input(label = "Name", hint = "Jon Smith") {
            Log.d("SELECTION:", it)
        }
    }

    DialogAndShowButton(
        buttonText = "Basic Input Dialog With Immediate Focus",
        buttons = {
            negativeButton("Cancel")
            positiveButton("Ok")
        }
    ) {
        val focusRequester = remember { FocusRequester() }
        title(res = R.string.input_dialog_title)
        input(
            label = "Name",
            hint = "Jon Smith",
            focusRequester = focusRequester,
            focusOnShow = true
        ) {
            Log.d("SELECTION:", it)
        }
    }

    DialogAndShowButton(
        buttonText = "Input Dialog with submit on IME Action",
        buttons = {
            negativeButton("Cancel")
            positiveButton("Ok")
        }
    ) {
        title(res = R.string.input_dialog_title)
        input(
            label = "Name", hint = "Jon Smith",
            keyboardActions = KeyboardActions(
                onDone = { submit() }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        ) {
            Log.d("SELECTION:", it)
        }
    }

    DialogAndShowButton(
        buttonText = "Input Dialog with input validation",
        buttons = {
            negativeButton("Cancel")
            positiveButton("Ok")
        }
    ) {
        title("Please enter your email")
        input(
            label = "Email",
            hint = "hello@example.com",
            errorMessage = "Invalid email",
            isTextValid = {
                Patterns.EMAIL_ADDRESS.matcher(it).matches() && it.isNotEmpty()
            }
        ) {
            Log.d("SELECTION:", it)
        }
    }
}
