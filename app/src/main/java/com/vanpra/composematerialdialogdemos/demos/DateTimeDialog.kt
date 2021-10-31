package com.vanpra.composematerialdialogdemos.demos

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.vanpra.composematerialdialogdemos.DialogAndShowButton
import com.vanpra.composematerialdialogs.MaterialDialogButtons
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerColors
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * @brief Date and Time Picker Demos
 */
@Composable
fun DateTimeDialogDemo() {
    val context = LocalContext.current

    DialogAndShowButton(
        buttonText = "Time Picker Dialog",
        buttons = { defaultDateTimeDialogButtons() }
    ) {
        timepicker {
            println(it.toString())
            Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
        }
    }

    DialogAndShowButton(
        buttonText = "Time Picker Dialog With Min/Max",
        buttons = { defaultDateTimeDialogButtons() }
    ) {
        timepicker(
            timeRange = LocalTime.of(9, 35)..LocalTime.of(21, 13),
            is24HourClock = false
        ) {
            println(it.toString())
            Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
        }
    }

    DialogAndShowButton(
        buttonText = "Time Picker Dialog 24H",
        buttons = { defaultDateTimeDialogButtons() }
    ) {
        timepicker(is24HourClock = true) {
            println(it.toString())
            Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
        }
    }

    DialogAndShowButton(
        buttonText = "Time Picker Dialog 24H With Min/Max",
        buttons = { defaultDateTimeDialogButtons() }
    ) {
        timepicker(
            timeRange = LocalTime.of(9, 35)..LocalTime.of(21, 13),
            is24HourClock = true
        ) {
            println(it.toString())
            Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
        }
    }

    DialogAndShowButton(
        buttonText = "Date Picker Dialog",
        buttons = { defaultDateTimeDialogButtons() }
    ) {
        datepicker {
            println(it.toString())
        }
    }

    DialogAndShowButton(
        buttonText = "Date Picker Dialog with date restrictions",
        buttons = { defaultDateTimeDialogButtons() }
    ) {
        datepicker(allowedDateValidator = {
            it.dayOfWeek !== DayOfWeek.SATURDAY && it.dayOfWeek !== DayOfWeek.SUNDAY
        }) {
            println(it.toString())
        }
    }
}

@Composable
private fun MaterialDialogButtons.defaultDateTimeDialogButtons() {
    positiveButton("Ok")
    negativeButton("Cancel")
}
