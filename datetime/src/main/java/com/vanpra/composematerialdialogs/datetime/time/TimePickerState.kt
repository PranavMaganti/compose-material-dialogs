package com.vanpra.composematerialdialogs.datetime.time

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.vanpra.composematerialdialogs.datetime.R
import com.vanpra.composematerialdialogs.datetime.util.isAM
import com.vanpra.composematerialdialogs.datetime.util.simpleHour

internal enum class ClockScreen {
    Hour,
    Minute
}

enum class TimePickerEntryMode {
    Clock,
    Text
}

internal class TimePickerState(
    options: TimePickerOptions,
    currentScreen: ClockScreen = ClockScreen.Hour,
    entryMode: TimePickerEntryMode = TimePickerEntryMode.Clock
) {
    val colors = options.colors
    var selectedTime by mutableStateOf(options.initialTime)
    var timeRange by mutableStateOf(options.timeRange)
    var is24Hour by mutableStateOf(options.is24HourClock)
    var currentScreen by mutableStateOf(currentScreen)
    var entryMode by mutableStateOf(entryMode)

    fun getHour(): Int = if (is24Hour) selectedTime.hour else selectedTime.simpleHour
    fun getMinute(): Int = selectedTime.minute

    private fun minimumMinute(isAM: Boolean, hour: Int): Int {
        return when {
            isAM == timeRange.start.isAM ->
                if (timeRange.start.hour == hour) {
                    timeRange.start.minute
                } else {
                    0
                }

            isAM -> 61
            else -> 0
        }
    }

    private fun maximumMinute(isAM: Boolean, hour: Int): Int {
        return when {
            isAM == timeRange.endInclusive.isAM ->
                if (timeRange.endInclusive.hour == hour) {
                    timeRange.endInclusive.minute
                } else {
                    60
                }

            isAM -> 60
            else -> 0
        }
    }

    fun hourRange() = timeRange.start.hour..timeRange.endInclusive.hour

    fun minuteRange(isAM: Boolean, hour: Int) = minimumMinute(isAM, hour)..maximumMinute(isAM, hour)

    @Composable
    fun getEntryModeIcon(): Painter {
        val keyboard = painterResource(id = R.drawable.keyboard_24)
        val clock = painterResource(id = R.drawable.schedule_24)

        return if (entryMode == TimePickerEntryMode.Clock) keyboard else clock
    }
}
