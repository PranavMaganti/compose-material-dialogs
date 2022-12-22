package com.vanpra.composematerialdialogs.datetime.time

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.vanpra.composematerialdialogs.DialogConstants
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogButtons
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.util.noSeconds
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalTime

data class TimePickerOptions(
    val initialTime: LocalTime,
    val colors: TimePickerColors,
    val waitForPositiveButton: Boolean,
    val timeRange: ClosedRange<LocalTime>,
    val is24HourClock: Boolean,
    val onTimeChange: (LocalTime) -> Unit
)

@Composable
fun rememberTimePickerOptions(
    initialTime: LocalTime = LocalTime.now().noSeconds(),
    colors: TimePickerColors = TimePickerDefaults.colors(),
    waitForPositiveButton: Boolean = true,
    timeRange: ClosedRange<LocalTime> = LocalTime.MIN..LocalTime.MAX,
    is24HourClock: Boolean = false,
    onTimeChange: (LocalTime) -> Unit = {}
) = remember {
    TimePickerOptions(
        initialTime,
        colors,
        waitForPositiveButton,
        timeRange,
        is24HourClock,
        onTimeChange
    )
}

/**
 * @brief A time picker dialog
 *
 * @param initialTime The time to be shown to the user when the dialog is first shown.
 * Defaults to the current time if this is not set
 * @param colors see [TimePickerColors]
 * @param waitForPositiveButton if true the [onTimeChange] callback will only be called when the
 * positive button is pressed, otherwise it will be called on every input change
 * @param timeRange any time outside this range will be disabled
 * @param is24HourClock uses the 24 hour clock face when true
 * @param onTimeChange callback with a LocalTime object when the user completes their input
 */
@Composable
fun MaterialTimePickerDialog(
    state: MaterialDialogState = rememberMaterialDialogState(),
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    border: BorderStroke? = null,
    elevation: Dp = DialogConstants.Elevation,
    buttonsPadding: PaddingValues = PaddingValues(
        start = DialogConstants.InternalPadding,
        end = DialogConstants.InternalPadding,
        bottom = DialogConstants.InternalPadding
    ),
    onCloseRequest: (MaterialDialogState) -> Unit = { it.hide() },
    timePickerOptions: TimePickerOptions = rememberTimePickerOptions(),
    buttons: @Composable MaterialDialogButtons.() -> Unit = {}
) {
    val timePickerState = remember { TimePickerState(options = timePickerOptions) }

    MaterialDialog(
        state = state,
        properties = properties,
        backgroundColor = backgroundColor,
        shape = shape,
        border = border,
        elevation = elevation,
        buttonsPadding = buttonsPadding,
        onCloseRequest = onCloseRequest,
        buttons = {
            buttons()
            accessibilityButton(icon = timePickerState.getEntryModeIcon()) {
                timePickerState.entryMode =
                    if (timePickerState.entryMode == TimePickerEntryMode.Clock) {
                        TimePickerEntryMode.Text
                    } else {
                        TimePickerEntryMode.Clock
                    }
                timePickerState.currentScreen = ClockScreen.Hour
            }
        },
        content = {
            if (timePickerOptions.waitForPositiveButton) {
                DialogCallback { timePickerOptions.onTimeChange(timePickerState.selectedTime) }
            } else {
                DisposableEffect(timePickerState.selectedTime) {
                    timePickerOptions.onTimeChange(timePickerState.selectedTime)
                    onDispose { }
                }
            }

            BoxWithConstraints {
                if (constraints.maxWidth > constraints.maxHeight) {
                    HorizontalTimePickerImpl(title = "Select time", state = timePickerState)
                } else {
                    VerticalTimePickerImpl(title = "Select time", state = timePickerState)
                }
            }
        }
    )
}

@Composable
internal fun VerticalTimePickerImpl(
    modifier: Modifier = Modifier,
    title: String,
    state: TimePickerState
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.align(Alignment.Start),
            text = title,
            color = state.colors.headlineText(),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(20.dp))

        VerticalTimeLayout(state = state)

        AnimatedVisibility(visible = state.entryMode == TimePickerEntryMode.Clock) {
            Column {
                Spacer(modifier = Modifier.height(36.dp))

                Crossfade(state.currentScreen) {
                    when (it) {
                        ClockScreen.Hour -> if (state.is24Hour) {
                            ExtendedClockHourLayout(state = state)
                        } else {
                            ClockHourLayout(state = state)
                        }

                        ClockScreen.Minute -> ClockMinuteLayout(state = state)
                    }
                }
            }
        }
    }
}

@Composable
internal fun HorizontalTimePickerImpl(
    modifier: Modifier = Modifier,
    title: String,
    state: TimePickerState
) {
    Column(modifier = modifier.padding(24.dp)) {
        Text(
            modifier = Modifier.align(Alignment.Start),
            text = title,
            color = state.colors.headlineText(),
            style = MaterialTheme.typography.labelMedium
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalTimeLayout(state = state)

            AnimatedVisibility(visible = state.entryMode == TimePickerEntryMode.Clock) {
                Row {
                    Spacer(modifier = Modifier.width(52.dp))

                    Crossfade(state.currentScreen) {
                        when (it) {
                            ClockScreen.Hour -> if (state.is24Hour) {
                                ExtendedClockHourLayout(state = state)
                            } else {
                                ClockHourLayout(state = state)
                            }

                            ClockScreen.Minute -> ClockMinuteLayout(state = state)
                        }
                    }
                }
            }
        }
    }
}

object TimePickerConstants {
    const val DisabledAlpha = 0.38f
}
