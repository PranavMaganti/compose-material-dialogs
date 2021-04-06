package com.vanpra.composematerialdialogs.datetime

import android.text.format.DateFormat
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import com.vanpra.composematerialdialogs.MaterialDialog
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @brief A combined date and time picker dialog
 *
 * @param initialDateTime The date and time to be shown to the user when the dialog is first shown.
 * Defaults to the current date and time if this is not set
 * @param timePickerColors see [TimePickerColors]
 * @param yearRange the range of years the user should be allowed to pick from
 * @param positiveButtonText text used for positive button label
 * @param negativeButtonText text used for negative button label
 * @param onComplete callback with a LocalDateTime object when the user completes their input
 * @param onCancel callback when the user cancels the dialog
 */
@Composable
fun MaterialDialog.datetimepicker(
    initialDateTime: LocalDateTime = LocalDateTime.now(),
    timePickerColors: TimePickerColors = TimePickerDefaults.colors(),
    yearRange: IntRange = IntRange(1900, 2100),
    minimumTime: LocalTime = LocalTime.MIN,
    maximumTime: LocalTime = LocalTime.MAX,
    is24HourClock : Boolean? = null,
    positiveButtonText: String = "Ok",
    negativeButtonText: String = "Cancel",
    onCancel: () -> Unit = {},
    onComplete: (LocalDateTime) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()

    val datePickerState = remember { DatePickerState(initialDateTime.toLocalDate()) }
    val context = LocalContext.current
    val timePickerState = remember {
        TimePickerState(selectedTime = initialDateTime.toLocalTime(),
            colors = timePickerColors,
            minimumTime = minimumTime,
            maximumTime = maximumTime,
            is24Hour = is24HourClock ?: DateFormat.is24HourFormat(context))
    }

    DisposableEffect(minimumTime) {
        timePickerState.minimumTime = SimpleLocalTime.fromLocalTime(minimumTime)
        onDispose {  }
    }
    DisposableEffect(maximumTime) {
        timePickerState.maximumTime = SimpleLocalTime.fromLocalTime(maximumTime)
        onDispose {  }
    }
    DisposableEffect(is24HourClock) {
        timePickerState.is24Hour = is24HourClock ?: DateFormat.is24HourFormat(context)
        onDispose {  }
    }

    DisposableEffect(minimumTime) {
        timePickerState.minimumTime = SimpleLocalTime.fromLocalTime(minimumTime)
        onDispose {  }
    }
    DisposableEffect(maximumTime) {
        timePickerState.maximumTime = SimpleLocalTime.fromLocalTime(maximumTime)
        onDispose {  }
    }

    val scrollPos = remember { Animatable(0f) }
    val scrollTo = remember { mutableStateOf(0f) }

    val isDateScreen =
        remember(scrollPos.value, scrollTo.value) { scrollPos.value < scrollTo.value / 2 }

    BoxWithConstraints {
        Column {
            SideEffect {
                scrollPos.updateBounds(0f, this@BoxWithConstraints.constraints.maxWidth.toFloat())
                scrollTo.value = this@BoxWithConstraints.constraints.maxWidth.toFloat()
            }

            Layout(
                content = {
                    DatePickerImpl(
                        state = datePickerState,
                        yearRange = yearRange,
                        backgroundColor = dialogBackgroundColor!!
                    )
                    TimePickerImpl(state = timePickerState) {
                        coroutineScope.launch { scrollPos.animateTo(0f) }
                    }
                }
            ) { measurables, constraints ->
                val placeables = measurables.map { it.measure(constraints) }
                val height = placeables.maxByOrNull { it.height }?.height ?: 0

                layout(constraints.maxWidth, height) {
                    placeables.forEachIndexed { index, placeable ->
                        placeable.place(
                            x = -scrollPos.value.toInt() + index * constraints.maxWidth,
                            y = 0
                        )
                    }
                }
            }
        }
    }

    buttons {
        positiveButton(
            text = if (isDateScreen) {
                "Next"
            } else {
                positiveButtonText
            },
            disableDismiss = isDateScreen
        ) {
            if (isDateScreen) {
                coroutineScope.launch {
                    scrollPos.animateTo(scrollTo.value)
                }
            } else {
                onComplete(
                    LocalDateTime.of(
                        datePickerState.selected,
                        timePickerState.selectedTime.toLocalTime()
                    )
                )
            }
        }

        negativeButton(negativeButtonText) {
            onCancel()
        }
    }
}
