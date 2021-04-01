package com.vanpra.composematerialdialogdemos.demos

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.vanpra.composematerialdialogdemos.DialogAndShowButton
import com.vanpra.composematerialdialogs.datetime.TimePickerColors
import com.vanpra.composematerialdialogs.datetime.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.datepicker
import com.vanpra.composematerialdialogs.datetime.datetimepicker
import com.vanpra.composematerialdialogs.datetime.timepicker

/**
 * @brief Date and Time Picker Demos
 */
@Composable
fun DateTimeDialogDemo() {
    val purple = remember { Color(0xFF3700B3) }

    val colors: TimePickerColors = if (isSystemInDarkTheme()) {
        TimePickerDefaults.colors(
            activeBackgroundColor = purple.copy(0.3f),
            activeTextColor = Color.White,
            selectorColor = purple,
            inactiveBackgroundColor = Color(0xFF292929),
        )
    } else {
        TimePickerDefaults.colors(
            inactiveBackgroundColor = Color.LightGray,
            activeBackgroundColor = purple.copy(0.1f),
            activeTextColor = purple,
            selectorColor = purple
        )
    }

    DialogAndShowButton(buttonText = "Time Picker Dialog") {
        timepicker(colors = colors) {
            println(it.toString())
        }

        buttons {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    }

    DialogAndShowButton(buttonText = "Date Picker Dialog") {
        datepicker {
            println(it.toString())
        }

        buttons {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    }

    DialogAndShowButton(buttonText = "Date and Time Picker Dialog") {
        datetimepicker(timePickerColors = colors) {
            println(it.toString())
        }
    }
}
