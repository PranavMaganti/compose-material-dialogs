package com.vanpra.composematerialdialogdemos.demos

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.vanpra.composematerialdialogdemos.DatePickerDialogAndShowButton
import com.vanpra.composematerialdialogdemos.TimePickerDialogAndShowButton
import com.vanpra.composematerialdialogs.MaterialDialogButtons
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * @brief Date and Time Picker Demos
 */
@Composable
fun DateTimeDialogDemo() {
    val context = LocalContext.current

    TimePickerDialogAndShowButton(
        buttonText = "Time Picker Dialog",
        buttons = { defaultDateTimeDialogButtons() },
        onTimeChange = {
            println(it.toString())
            Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
        }
    )

    TimePickerDialogAndShowButton(
        buttonText = "Time Picker Dialog With Min/Max",
        buttons = { defaultDateTimeDialogButtons() },
        timeRange = LocalTime.of(9, 35)..LocalTime.of(21, 13),
        is24HourClock = false,
        onTimeChange = {
            println(it.toString())
            Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
        }
    )

    TimePickerDialogAndShowButton(
        buttonText = "Time Picker Dialog 24H",
        buttons = { defaultDateTimeDialogButtons() },
        is24HourClock = true,
        onTimeChange = {
            println(it.toString())
            Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
        }
    )

    TimePickerDialogAndShowButton(
        buttonText = "Time Picker Dialog 24H With Min/Max",
        buttons = { defaultDateTimeDialogButtons() },
        timeRange = LocalTime.of(9, 35)..LocalTime.of(21, 13),
        is24HourClock = true,
        onTimeChange = {
            println(it.toString())
            Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
        }
    )

    DatePickerDialogAndShowButton(
        buttonText = "Date Picker Dialog",
        buttons = { defaultDateTimeDialogButtons() },
        colors = DatePickerDefaults.colors(),
        onDateChange = {
            println(it.toString())
        }
    )

    DatePickerDialogAndShowButton(
        buttonText = "Date Picker Dialog with date restrictions",
        buttons = { defaultDateTimeDialogButtons() },
        allowedDateValidator = {
            it.dayOfWeek !== DayOfWeek.SATURDAY && it.dayOfWeek !== DayOfWeek.SUNDAY
        },
        colors = DatePickerDefaults.colors(),
        onDateChange = {
            println(it.toString())
        }
    )
}

@Composable
private fun MaterialDialogButtons.defaultDateTimeDialogButtons() {
    positiveButton("Ok")
    negativeButton("Cancel")
}
