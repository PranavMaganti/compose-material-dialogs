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
     */
    @Composable
    fun colors(
        headerBackgroundColor: Color = MaterialTheme.colorScheme.primary,
        headerTextColor: Color = MaterialTheme.colorScheme.onPrimary,
        activeBackgroundColor: Color = MaterialTheme.colorScheme.primary,
        inactiveBackgroundColor: Color = Color.Transparent,
        activeTextColor: Color = MaterialTheme.colorScheme.onPrimary,
        inactiveTextColor: Color = MaterialTheme.colorScheme.onBackground
    ): DatePickerColors {
        return DefaultDatePickerColors(
            headerBackgroundColor = headerBackgroundColor,
            headerTextColor = headerTextColor,
            activeBackgroundColor = activeBackgroundColor,
            inactiveBackgroundColor = inactiveBackgroundColor,
            activeTextColor = activeTextColor,
            inactiveTextColor = inactiveTextColor,
        )
    }
}
