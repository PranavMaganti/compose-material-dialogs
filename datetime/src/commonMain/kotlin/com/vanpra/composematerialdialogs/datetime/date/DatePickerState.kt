package com.vanpra.composematerialdialogs.datetime.date

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.vanpra.composematerialdialogs.datetime.PlatformLocalDate

internal class DatePickerState(
    initialDate: PlatformLocalDate,
    val colors: DatePickerColors,
    val yearRange: IntRange,
    val dialogBackground: Color
) {
    var selected by mutableStateOf(initialDate)
    var yearPickerShowing by mutableStateOf(false)

    companion object {
        val dayHeaders = listOf("S", "M", "T", "W", "T", "F", "S")
    }
}
