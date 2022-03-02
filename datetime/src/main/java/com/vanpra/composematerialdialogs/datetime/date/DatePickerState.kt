package com.vanpra.composematerialdialogs.datetime.date

import android.text.format.DateUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Calendar
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

        private val isoDayOfWeekToCalendarDayOfWeek = mapOf(
            DayOfWeek.MONDAY to Calendar.MONDAY,
            DayOfWeek.TUESDAY to Calendar.TUESDAY,
            DayOfWeek.WEDNESDAY to Calendar.WEDNESDAY,
            DayOfWeek.THURSDAY to Calendar.THURSDAY,
            DayOfWeek.FRIDAY to Calendar.FRIDAY,
            DayOfWeek.SATURDAY to Calendar.SATURDAY,
            DayOfWeek.SUNDAY to Calendar.SUNDAY
        )

        val dayHeaders = WeekFields.of(locale).firstDayOfWeek.let { firstDayOfWeek ->
            (0L until 7L).map {
                DateUtils.getDayOfWeekString(isoDayOfWeekToCalendarDayOfWeek[firstDayOfWeek.plus(it)] ?: 0, DateUtils.LENGTH_SHORTEST)
            }
        }
    }
}
