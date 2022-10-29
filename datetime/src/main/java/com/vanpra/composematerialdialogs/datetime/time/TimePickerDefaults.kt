package com.vanpra.composematerialdialogs.datetime.time

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Object to hold default values used by [timepicker]
 */
object TimePickerDefaults {
    /**
     * Initialises a [TimePickerColors] object which represents the different colors used by
     * the [timepicker] composable
     *
     * @param activeBackgroundColor background color of selected time unit or period (AM/PM)
     * @param inactiveBackgroundColor background color of inactive items in the dialog including
     * the clock face
     * @param activeTextColor color of text on the activeBackgroundColor
     * @param inactiveTextColor color of text on the inactiveBackgroundColor
     * @param inactivePeriodBackground background color of the inactive period (AM/PM) selector
     * @param selectorColor color of clock hand/selector
     * @param selectorTextColor color of text on selectedColor
     * @param headerTextColor  Get color of title text
     * @param borderColor border color of the period (AM/PM) selector
     */
    @Composable
    fun colors(
        headerText: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        timeSelectorSeparator: Color = MaterialTheme.colorScheme.onSurface,
        activePeriodContainer: Color = MaterialTheme.colorScheme.tertiaryContainer,
        inactivePeriodContainer: Color = Color.Transparent,
        periodContainerOutline: Color = MaterialTheme.colorScheme.outline,
        activePeriodText: Color = MaterialTheme.colorScheme.onTertiaryContainer,
        inactivePeriodText: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        clockDialContainer: Color = MaterialTheme.colorScheme.surfaceVariant,
        clockDialSelector: Color = MaterialTheme.colorScheme.primary,
        activeClockDialText: Color = MaterialTheme.colorScheme.onPrimary,
        inactiveClockDialText: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        activeTimeSelectorContainer: Color = MaterialTheme.colorScheme.primaryContainer,
        inactiveTimeSelectorContainer: Color = MaterialTheme.colorScheme.surfaceVariant,
        activeTimeSelectorText: Color  = MaterialTheme.colorScheme.onPrimaryContainer,
        inactiveTimeSelectorText: Color = MaterialTheme.colorScheme.onSurfaceVariant
    ): TimePickerColors {
        return DefaultTimePickerColors(
            headerText = headerText,
            timeSelectorSeparator = timeSelectorSeparator,
            activePeriodContainer = activePeriodContainer,
            inactivePeriodContainer = inactivePeriodContainer,
            periodContainerOutline = periodContainerOutline,
            activePeriodText = activePeriodText,
            inactivePeriodText = inactivePeriodText,
            clockDialContainer = clockDialContainer,
            clockDialSelector = clockDialSelector,
            activeClockDialText = activeClockDialText,
            inactiveClockDialText = inactiveClockDialText,
            activeTimeSelectorContainer = activeTimeSelectorContainer,
            inactiveTimeSelectorContainer = inactiveTimeSelectorContainer,
            activeTimeSelectorText = activeTimeSelectorText,
            inactiveTimeSelectorText = inactiveTimeSelectorText
        )
    }
}
