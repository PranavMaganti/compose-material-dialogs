package com.vanpra.composematerialdialogs.datetime.time

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color

/**
 * Represents the colors used by a [timepicker] and its parts in different states
 *
 * See [TimePickerDefaults.colors] for the default implementation
 */
interface TimePickerColors {
    fun headerText(): Color

    fun timeSelectorSeparator(): Color

    @Composable
    fun periodContainer(active: Boolean): State<Color>

    fun periodContainerOutline(): Color

    @Composable
    fun periodText(active: Boolean): State<Color>

    fun clockDialContainer(): Color

    fun clockDialSelector(): Color

    @Composable
    fun clockDialText(active: Boolean): State<Color>
    @Composable
    fun timeSelectorContainer(active: Boolean): State<Color>

    @Composable
    fun timeSelectorText(active: Boolean): State<Color>
}

internal class DefaultTimePickerColors(
    private val headerText: Color,
    private val timeSelectorSeparator: Color,
    private val activePeriodContainer: Color,
    private val periodContainerOutline: Color,
    private val inactivePeriodContainer: Color,
    private val activePeriodText: Color,
    private val inactivePeriodText: Color,
    private val clockDialContainer: Color,
    private val clockDialSelector: Color,
    private val activeClockDialText: Color,
    private val inactiveClockDialText: Color,
    private val activeTimeSelectorContainer: Color,
    private val inactiveTimeSelectorContainer: Color,
    private val activeTimeSelectorText: Color,
    private val inactiveTimeSelectorText: Color,
) : TimePickerColors {

    override fun headerText(): Color = headerText

    override fun timeSelectorSeparator(): Color = timeSelectorSeparator

    @Composable
    override fun periodContainer(active: Boolean): State<Color> {
        return rememberUpdatedState(if (active) activePeriodContainer else inactivePeriodContainer)
    }

    override fun periodContainerOutline(): Color = periodContainerOutline

    @Composable
    override fun periodText(active: Boolean): State<Color> {
        return rememberUpdatedState(if (active) activePeriodText else inactivePeriodText)
    }

    override fun clockDialContainer(): Color = clockDialContainer

    override fun clockDialSelector(): Color = clockDialSelector

    @Composable
    override fun clockDialText(active: Boolean): State<Color> {
        return rememberUpdatedState(if (active) activeClockDialText else inactiveClockDialText)
    }

    @Composable
    override fun timeSelectorContainer(active: Boolean): State<Color> {
        return rememberUpdatedState(if (active) activeTimeSelectorContainer else inactiveTimeSelectorContainer)
    }

    @Composable
    override fun timeSelectorText(active: Boolean): State<Color> {
        return rememberUpdatedState(if (active) activeTimeSelectorText else inactiveTimeSelectorText)
    }
}
