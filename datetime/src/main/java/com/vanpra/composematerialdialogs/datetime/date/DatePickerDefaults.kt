package com.vanpra.composematerialdialogs.datetime.date

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Object to hold default values used by [datepicker]
 */
object DatePickerDefaults {
    /**
     * Initialises a [DatePickerColors] object which represents the different colors used by
     * the [datepicker] composable
     * @param headerBackgroundColor background color of header
     * @param headerTextColor color of text on the header
     * @param calendarHeaderTextColor color of text on the calendar header (year selector
     * and days of week)
     * @param dateActiveBackgroundColor background color of date when selected
     * @param dateActiveTextColor color of date text when selected
     * @param dateInactiveBackgroundColor background color of date when not selected
     * @param dateInactiveTextColor color of date text when not selected
     */
    @Composable
    fun colors(
        headerText: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedDateTitle: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        activeDateContainer: Color = MaterialTheme.colorScheme.primary,
        inactiveDateContainer: Color = Color.Transparent,
        activeDateText: Color = MaterialTheme.colorScheme.onPrimary,
        inactiveDateText: Color = MaterialTheme.colorScheme.onSurface
    ): DatePickerColors {
        return DefaultDatePickerColors(
            headerText = headerText,
            selectedDateTitle = selectedDateTitle,
            activeDateContainer = activeDateContainer,
            inactiveDateContainer = inactiveDateContainer,
            activeDateText = activeDateText,
            inactiveDateText = inactiveDateText
        )
    }
}
