package com.vanpra.composematerialdialogs.datetime.date

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

internal class DatePickerState(
    initialDate: LocalDate,
    val colors: DatePickerColors,
    val yearRange: IntRange,
    val dialogBackground: Color
) {
    var selected by mutableStateOf(initialDate)
    var yearPickerShowing by mutableStateOf(false)

    companion object {

        private val locale = Locale.getDefault()

        val dayHeaders = WeekFields.of(locale).firstDayOfWeek.let { firstDayOfWeek ->
            (0L until 7L).map {
                firstDayOfWeek.plus(it).getDisplayName(TextStyle.NARROW, locale)
            }
        }
    }
}
