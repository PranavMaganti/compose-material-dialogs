package com.vanpra.composematerialdialogs.datetime.date

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.vanpra.composematerialdialogs.DialogConstants
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogButtons
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.components.CalendarMonth
import com.vanpra.composematerialdialogs.datetime.date.components.NextPreviousMonth
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.util.Locale

@Preview
@Composable
internal fun Test() {
    MaterialTheme(darkColorScheme()) {
        CalendarMonth(viewDate = LocalDate.now(), colors = DatePickerDefaults.colors())
    }
}

data class DatePickerOptions(
    val initialDate: LocalDate,
    val colors: DatePickerColors,
    val yearRange: IntRange,
    val waitForPositiveButton: Boolean,
    val allowedDateValidator: (LocalDate) -> Boolean,
    val locale: Locale,
    val onDateChange: (LocalDate) -> Unit
)

@Composable
fun rememberDatePickerOptions(
    initialDate: LocalDate = LocalDate.now(),
    colors: DatePickerColors = DatePickerDefaults.colors(),
    yearRange: IntRange = IntRange(1900, 2100),
    waitForPositiveButton: Boolean = true,
    allowedDateValidator: (LocalDate) -> Boolean = { true },
    locale: Locale = Locale.getDefault(),
    onDateChange: (LocalDate) -> Unit = {}
) = remember {
    DatePickerOptions(
        initialDate,
        colors,
        yearRange,
        waitForPositiveButton,
        allowedDateValidator,
        locale,
        onDateChange
    )
}

/**
 * @brief A date picker body layout
 *
 * @param initialDate time to be shown to the user when the dialog is first shown.
 * Defaults to the current date if this is not set
 * @param yearRange the range of years the user should be allowed to pick from
 * @param waitForPositiveButton if true the [onDateChange] callback will only be called when the
 * positive button is pressed, otherwise it will be called on every input change
 * @param onDateChange callback with a LocalDateTime object when the user completes their input
 * @param allowedDateValidator when this returns true the date will be selectable otherwise it won't be
 */
@Composable
fun MaterialDatePickerDialog(
    state: MaterialDialogState = rememberMaterialDialogState(),
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    border: BorderStroke? = null,
    elevation: Dp = DialogConstants.Elevation,
    buttonsPadding: PaddingValues = PaddingValues(
        start = DatePickerConstants.DialogButtonPadding,
        end = DatePickerConstants.DialogButtonPadding,
        bottom = DatePickerConstants.DialogButtonPadding
    ),
    onCloseRequest: (MaterialDialogState) -> Unit = { it.hide() },
    datePickerOptions: DatePickerOptions = rememberDatePickerOptions(),
    buttons: @Composable MaterialDialogButtons.() -> Unit = {}
) {
    val datePickerState = remember {
        DatePickerState(datePickerOptions)
    }

    MaterialDialog(
        state = state,
        properties = properties,
        backgroundColor = backgroundColor,
        shape = shape,
        border = border,
        elevation = elevation,
        buttonsPadding = buttonsPadding,
        onCloseRequest = onCloseRequest,
        buttons = buttons,
        content = {
            DatePickerImpl(title = "Select Date", state = datePickerState)

            if (datePickerOptions.waitForPositiveButton) {
                DialogCallback { datePickerOptions.onDateChange(datePickerState.selected) }
            } else {
                DisposableEffect(datePickerState.selected) {
                    datePickerOptions.onDateChange(datePickerState.selected)
                    onDispose { }
                }
            }
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun DatePickerImpl(title: String, state: DatePickerState) {
    val pagerState = rememberPagerState(
        initialPage = (state.selected.year - state.yearRange.first) * 12 + state.selected.monthValue - 1
    )

    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        DatePickerHeader(title, state)

        Spacer(Modifier.height(12.dp))
        Divider(Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        HorizontalPager(
            count = (state.yearRange.last - state.yearRange.first + 1) * 12,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.height(312.dp),
            userScrollEnabled = !state.yearPickerShowing
        ) { page ->
            val viewDate = remember {
                LocalDate.of(
                    state.yearRange.first + page / 12,
                    page % 12 + 1,
                    1
                )
            }

            Box {
                Column {
                    Row(
                        modifier = Modifier
                            .height(24.dp)
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        YearSelectorDropdownButton(state = state, viewDate = viewDate)
                        NextPreviousMonth(pagerState = pagerState)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box {
                        val dialogTonalElevation = LocalElevationOverlay.current
                        androidx.compose.animation.AnimatedVisibility(
                            state.yearPickerShowing,
                            modifier = Modifier
                                .zIndex(0.7f)
                                .clipToBounds(),
                            enter = slideInVertically(initialOffsetY = { -it }),
                            exit = slideOutVertically(targetOffsetY = { -it })
                        ) {
                            CompositionLocalProvider(
                                LocalElevationOverlay provides dialogTonalElevation
                            ) {
                                YearPicker(viewDate, state, pagerState)
                            }
                        }

                        CalendarMonth(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            viewDate,
                            state.colors
                        )
                    }
                }
            }
        }
    }
}

object DatePickerConstants {
    const val DisabledAlpha = 0.85f
    val DialogButtonPadding = 12.dp
}
