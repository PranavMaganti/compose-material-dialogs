package com.vanpra.composematerialdialogs.datetime

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * @brief A date time picker dialog
 *
 * @param initialDateTime The date and time to be shown to the user when the dialog is first shown.
 * Defaults to the current date and time if this is not set
 * @param onComplete callback with a LocalDateTime object when the user completes their input
 * @param onCancel callback when the user cancels the dialog
 */
@Composable
fun MaterialDialog.datetimepicker(
    initialDateTime: LocalDateTime = LocalDateTime.now(),
    timePickerColors: TimePickerColors = DialogDefaults.timePickerColors(),
    yearRange: IntRange = IntRange(1900, 2100),
    onCancel: () -> Unit = {},
    onComplete: (LocalDateTime) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()

    val datePickerState = remember { DatePickerState(initialDateTime.toLocalDate()) }
    val timePickerState = remember {
        TimePickerState(selectedTime = initialDateTime.toLocalTime(), colors = timePickerColors)
    }

    val scrollPos = remember { Animatable(0f) }
    val scrollTo = remember { mutableStateOf(0f) }

    BoxWithConstraints {
        Column {
            SideEffect {
                scrollPos.updateBounds(0f, this@BoxWithConstraints.constraints.maxWidth.toFloat())
                scrollTo.value = this@BoxWithConstraints.constraints.maxWidth.toFloat()
            }


            Layout(content = {
                DatePickerImpl(state = datePickerState, yearRange = yearRange)
                TimePickerImpl(state = timePickerState) {
                    coroutineScope.launch { scrollPos.animateTo(0f) }
                }
            }) { measurables, constraints ->
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
        val isDateScreen = remember(scrollPos.value) { scrollPos.value == 0f }

        positiveButton(
            text = if (isDateScreen) {
                "Next"
            } else {
                "Ok"
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

        negativeButton("Cancel") {
            onCancel()
        }
    }
}
