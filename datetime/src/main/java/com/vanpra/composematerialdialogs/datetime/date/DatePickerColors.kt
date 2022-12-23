package com.vanpra.composematerialdialogs.datetime.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color

/**
 * Represents the colors used by a [timepicker] and its parts in different states
 *
 * See [DatePickerDefaults.colors] for the default implementation
 */
interface DatePickerColors {
    val titleText: Color
    val headlineText: Color
    val weekdaysText: Color

    val activeDateText: Color
    val activeDateContainer: Color
    val inactiveDateText: Color
    val inactiveDateContainer: Color
    val todayDateText: Color
    val todayDateContainer: Color

    /**
     * Gets the background color dependant on if the item is active or not
     *
     * @param active true if the component/item is selected and false otherwise
     * @return background color as a State
     */
    @Composable
    fun dateContainer(active: Boolean): State<Color>

    /**
     * Gets the text color dependant on if the item is active or not
     *
     * @param active true if the component/item is selected and false otherwise
     * @return text color as a State
     */
    @Composable
    fun dateText(active: Boolean): State<Color>
}

internal class DefaultDatePickerColors(
    override val titleText: Color,
    override val headlineText: Color,
    override val weekdaysText: Color,
    override val activeDateText: Color,
    override val activeDateContainer: Color,
    override val inactiveDateText: Color,
    override val inactiveDateContainer: Color,
    override val todayDateText: Color,
    override val todayDateContainer: Color
) : DatePickerColors {

    @Composable
    override fun dateContainer(active: Boolean): State<Color> {
        return rememberUpdatedState(if (active) activeDateContainer else inactiveDateContainer)
    }

    @Composable
    override fun dateText(active: Boolean): State<Color> {
        return rememberUpdatedState(if (active) activeDateText else inactiveDateText)
    }
}
