package com.vanpra.composematerialdialogs.datetime

import androidx.compose.Composable
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.core.clip
import androidx.ui.core.drawOpacity
import androidx.ui.foundation.Canvas
import androidx.ui.foundation.Image
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.ScrollableRow
import androidx.ui.foundation.clickable
import androidx.ui.foundation.rememberScrollState
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.geometry.Offset
import androidx.ui.graphics.ColorFilter
import androidx.ui.layout.Row
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.heightIn
import androidx.ui.layout.padding
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.wrapContentHeight
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

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
    title: String,
    initialDateTime: LocalDateTime = LocalDateTime.now(),
    onCancel: () -> Unit = {},
    onComplete: (LocalDateTime) -> Unit = {}
) {
    val currentDate = initialDateTime.toLocalDate()
    val selectedDate = state { currentDate }

    val currentTime = remember { initialDateTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES) }
    val selectedTime = state { currentTime }

    val scrollState = rememberScrollState()
    val columnScrollState = rememberScrollState()

    val scrollTo = state { 0f }
    val currentScreen = state { 0 }

    WithConstraints {
        ScrollableColumn(
            Modifier.heightIn(maxHeight = maxHeight * 0.8f),
            scrollState = columnScrollState
        ) {
            Stack(Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 24.dp)) {
                val ratio = scrollState.value / constraints.maxWidth
                Image(
                    Icons.Default.ArrowBack,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                    modifier = Modifier.padding(start = 16.dp)
                        .clip(CircleShape)
                        .clickable(
                            onClick = {
                                scrollState.smoothScrollTo(0f)
                                currentScreen.value = 0
                            }
                        )
                        .drawOpacity(1f * ratio)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
                DialogTitle(title)
            }

            Row(Modifier.fillMaxWidth().wrapContentSize(Alignment.Center).height(10.dp)) {
                val ratio = scrollState.value / constraints.maxWidth
                val color = MaterialTheme.colors.onBackground
                Canvas(modifier = Modifier) {
                    val offset = Offset(30f, 0f)
                    drawCircle(
                        color.copy(0.7f + 0.3f * (1 - ratio)),
                        radius = 8f + 7f * (1 - ratio),
                        center = center - offset
                    )
                    drawCircle(
                        color.copy(0.7f + 0.3f * ratio),
                        radius = 8f + 7f * ratio,
                        center = center + offset
                    )
                }
            }

            scrollTo.value = constraints.maxWidth.toFloat()
            ScrollableRow(
                scrollState = scrollState,
                isScrollEnabled = false,
                children = {
                    DatePickerLayout(
                        Modifier.padding(top = 16.dp).preferredWidth(maxWidth),
                        selectedDate,
                        currentDate
                    )
                    TimePickerLayout(
                        Modifier.padding(top = 16.dp).preferredWidth(maxWidth),
                        selectedTime
                    )
                })
        }
    }

    buttons {
        positiveButton(
            text = if (currentScreen.value == 0) {
                "Next"
            } else {
                "Ok"
            },
            disableDismiss = currentScreen.value == 0
        ) {
            if (currentScreen.value == 0) {
                scrollState.smoothScrollTo(scrollTo.value)
                columnScrollState.smoothScrollTo(0f)
                currentScreen.value = 1
            } else {
                onComplete(LocalDateTime.of(selectedDate.value, selectedTime.value))
            }
        }

        negativeButton("Cancel") {
            onCancel()
        }
    }
}
