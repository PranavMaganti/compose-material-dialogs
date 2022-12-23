package com.vanpra.composematerialdialogs.datetime.date

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

internal class DatePickerState(
    datePickerOptions: DatePickerOptions
) {
    val colors: DatePickerColors = datePickerOptions.colors
    val yearRange: IntRange = datePickerOptions.yearRange
    val locale: Locale = datePickerOptions.locale
    var selected by mutableStateOf(datePickerOptions.initialDate)
    var yearPickerShowing by mutableStateOf(false)
}
