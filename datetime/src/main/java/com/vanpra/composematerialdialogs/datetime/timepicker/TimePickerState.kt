package com.vanpra.composematerialdialogs.datetime.timepicker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vanpra.composematerialdialogs.datetime.util.isAM
import java.time.LocalTime

internal enum class ClockScreen {
    Hour,
    Minute;

    fun isHour() = this == Hour
    fun isMinute() = this == Minute
}

internal class TimePickerState(
    val colors: TimePickerColors,
    selectedTime: LocalTime,
    currentScreen: ClockScreen = ClockScreen.Hour,
    clockInput: Boolean = true,
    minimumTime: LocalTime,
    maximumTime: LocalTime,
    is24Hour: Boolean,
) {
    var selectedTime by mutableStateOf(selectedTime)
    var minimumTime by mutableStateOf(minimumTime)
    var maximumTime by mutableStateOf(maximumTime)
    var is24Hour by mutableStateOf(is24Hour)
    var currentScreen by mutableStateOf(currentScreen)
    var clockInput by mutableStateOf(clockInput)

    private fun minimumMinute(isAM: Boolean, hour: Int): Int {
        return when {
            isAM == minimumTime.isAM ->
                if (minimumTime.hour == hour) {
                    minimumTime.minute
                } else {
                    0
                }
            isAM -> 61
            else -> 0
        }
    }

    private fun maximumMinute(isAM: Boolean, hour: Int): Int {
        return when {
            isAM == maximumTime.isAM ->
                if (maximumTime.hour == hour) {
                    maximumTime.minute
                } else {
                    60
                }
            isAM -> 60
            else -> 0
        }
    }

    fun hourRange() = minimumTime.hour..maximumTime.hour

    fun minuteRange(isAM: Boolean, hour: Int) = minimumMinute(isAM, hour)..maximumMinute(isAM, hour)
}
