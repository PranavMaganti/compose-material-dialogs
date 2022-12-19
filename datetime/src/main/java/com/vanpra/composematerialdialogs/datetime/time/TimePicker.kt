package com.vanpra.composematerialdialogs.datetime.time

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.datetime.util.getOffset
import com.vanpra.composematerialdialogs.datetime.util.isAM
import com.vanpra.composematerialdialogs.datetime.util.noSeconds
import com.vanpra.composematerialdialogs.datetime.util.simpleHour
import com.vanpra.composematerialdialogs.datetime.util.toAM
import com.vanpra.composematerialdialogs.datetime.util.toPM
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

/* Offset of the clock line and selected circle */
private data class SelectedOffset(
    val lineOffset: Offset = Offset.Zero,
    val selectedOffset: Offset = Offset.Zero,
    val selectedRadius: Float = 0.0f
)

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
fun MaterialDialogScope.timepicker(
    initialTime: LocalTime = LocalTime.now().noSeconds(),
    title: String = "Select Time",
    colors: TimePickerColors = TimePickerDefaults.colors(),
    waitForPositiveButton: Boolean = true,
    timeRange: ClosedRange<LocalTime> = LocalTime.MIN..LocalTime.MAX,
    is24HourClock: Boolean = false,
    onTimeChange: (LocalTime) -> Unit = {}
) {
    val timePickerState = remember {
        TimePickerState(
            selectedTime = initialTime.coerceIn(timeRange),
            colors = colors,
            timeRange = timeRange,
            is24Hour = is24HourClock
        )
    }

    if (waitForPositiveButton) {
        DialogCallback { onTimeChange(timePickerState.selectedTime) }
    } else {
        DisposableEffect(timePickerState.selectedTime) {
            onTimeChange(timePickerState.selectedTime)
            onDispose { }
        }
    }

    TimePickerImpl(title = title, state = timePickerState)
}


@Composable
internal fun TimePickerImpl(
    modifier: Modifier = Modifier, title: String, state: TimePickerState
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title.isNotEmpty()) {
            TimePickerTitle(
                modifier = Modifier.align(Alignment.Start),
                text = title,
                state = state
            )
        }

        Spacer(Modifier.height(20.dp))

        TimeLayout(state = state)

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

@Composable
internal fun TimePickerTitle(modifier: Modifier = Modifier, text: String, state: TimePickerState) {
    Text(
        modifier = modifier,
        text = text,
        color = state.colors.headerText(),
        style = MaterialTheme.typography.labelMedium
    )
}

@Composable
internal fun ClockLabel(
    text: String, backgroundColor: Color, textColor: Color, onClick: () -> Unit, is24Hour: Boolean
) {
    Surface(
        modifier = Modifier
            .size(height = 80.dp, width = if (is24Hour) 114.dp else 96.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = backgroundColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = textColor, style = MaterialTheme.typography.displayLarge)
        }
    }
}


@Composable
internal fun TimeLayout(modifier: Modifier = Modifier, state: TimePickerState) {
    val clockHour: String = remember(state.is24Hour, state.selectedTime, state.selectedTime.hour) {
        state.getHour().toString().padStart(2, '0')
    }

    Row(
        horizontalArrangement = Arrangement.Center, modifier = modifier
            .height(80.dp)
            .fillMaxWidth()
    ) {
        ClockLabel(
            text = clockHour,
            backgroundColor = state.colors.timeSelectorContainer(state.currentScreen.isHour()).value,
            textColor = state.colors.timeSelectorText(state.currentScreen.isHour()).value,
            onClick = { state.currentScreen = ClockScreen.Hour },
            is24Hour = state.is24Hour
        )

        Box(
            Modifier
                .width(24.dp)
                .fillMaxHeight(), contentAlignment = Alignment.Center
        ) {
            Text(
                text = ":",
                color = state.colors.timeSelectorSeparator(),
                style = MaterialTheme.typography.displayLarge
            )
        }

        ClockLabel(
            text = state.selectedTime.minute.toString().padStart(2, '0'),
            backgroundColor = state.colors.timeSelectorContainer(state.currentScreen.isMinute()).value,
            textColor = state.colors.timeSelectorText(state.currentScreen.isMinute()).value,
            onClick = { state.currentScreen = ClockScreen.Minute },
            is24Hour = state.is24Hour
        )

        if (!state.is24Hour) {
            Spacer(modifier = Modifier.width(12.dp))
            VerticalPeriodPicker(state = state)
        }
    }
}

@Composable
private fun VerticalPeriodPicker(state: TimePickerState) {
    val isAMEnabled = remember(state.timeRange) { state.timeRange.start.hour <= 12 }
    val isPMEnabled = remember(state.timeRange) { state.timeRange.endInclusive.hour >= 0 }

    Column(
        Modifier
            .fillMaxHeight()
            .width(52.dp)
            .clip(MaterialTheme.shapes.small)
            .border(
                width = 1.dp,
                color = state.colors.periodContainerOutline(),
                shape = MaterialTheme.shapes.small
            )
    ) {
        Box(
            modifier = Modifier
                .size(height = 40.dp, width = 52.dp)
                .background(state.colors.periodContainer(state.selectedTime.isAM).value)
                .clickable(
                    onClick = {
                        state.selectedTime = state.selectedTime
                            .toAM()
                            .coerceIn(state.timeRange)
                    },
                    enabled = isAMEnabled
                ),
            contentAlignment = Alignment.Center
        ) {
            var color = state.colors.periodText(state.selectedTime.isAM).value
            if (!isAMEnabled) {
                color = color.copy(alpha = TimePickerConstants.DisabledAlpha)
            }
            Text(
                "AM", style = MaterialTheme.typography.titleMedium, color = color
            )
        }

        Divider(thickness = 1.dp)

        Box(
            modifier = Modifier
                .size(height = 40.dp, width = 52.dp)
                .background(state.colors.periodContainer(!state.selectedTime.isAM).value)
                .clickable(onClick = {
                    state.selectedTime = state.selectedTime
                        .toPM()
                        .coerceIn(state.timeRange)
                }, enabled = isPMEnabled), contentAlignment = Alignment.Center
        ) {
            var color = state.colors.periodText(!state.selectedTime.isAM).value
            if (!isPMEnabled) {
                color = color.copy(alpha = TimePickerConstants.DisabledAlpha)
            }
            Text(
                "PM", style = MaterialTheme.typography.titleMedium, color = color
            )
        }
    }
}

@Composable
private fun ClockHourLayout(state: TimePickerState) {
    fun adjustedHour(hour: Int): Int {
        return if (state.selectedTime.isAM || hour == 12) hour else hour + 12
    }

    val isEnabled: (Int) -> Boolean = remember(state.timeRange, state.selectedTime) {
        { index -> adjustedHour(index) in state.hourRange() }
    }

    ClockLayout(
        anchorPoints = 12,
        label = { index -> if (index == 0) "12" else index.toString() },
        onAnchorChange = { hours ->
            val adjustedHour = when (hours) {
                12 -> if (state.selectedTime.isAM) 0 else 12
                else -> if (state.selectedTime.isAM) hours else hours + 12
            }
            state.selectedTime = state.selectedTime.withHour(adjustedHour).coerceIn(state.timeRange)
        },
        startAnchor = state.selectedTime.simpleHour % 12,
        onLift = { state.currentScreen = ClockScreen.Minute },
        colors = state.colors,
        isAnchorEnabled = isEnabled
    )
}

@Composable
private fun ExtendedClockHourLayout(state: TimePickerState) {
    fun adjustAnchor(anchor: Int): Int = when (anchor) {
        0 -> 12
        12 -> 0
        else -> anchor
    }

    val isEnabled: (Int) -> Boolean = remember(state.timeRange) {
        { index -> adjustAnchor(index) in state.hourRange() }
    }

    ClockLayout(
        anchorPoints = 12,
        innerAnchorPoints = 12,
        label = { index ->
            /* Swapping 12 and 00 as this is the standard layout */
            when (index) {
                0 -> "12"
                12 -> "00"
                else -> index.toString()
            }
        },
        onAnchorChange = { anchor ->
            /* Swapping 12 and 00 as this is the standard layout */
            state.selectedTime =
                state.selectedTime.withHour(adjustAnchor(anchor)).coerceIn(state.timeRange)
        },
        startAnchor = adjustAnchor(state.selectedTime.hour),
        onLift = { state.currentScreen = ClockScreen.Minute },
        colors = state.colors,
        isAnchorEnabled = isEnabled
    )
}

@Composable
private fun ClockMinuteLayout(state: TimePickerState) {
    val isEnabled: (Int) -> Boolean =
        remember(state.timeRange, state.selectedTime, state.selectedTime.isAM) {
            { index ->
                index in state.minuteRange(state.selectedTime.isAM, state.selectedTime.hour)
            }
        }
    ClockLayout(
        anchorPoints = 60,
        label = { index -> index.toString().padStart(2, '0') },
        onAnchorChange = { mins -> state.selectedTime = state.selectedTime.withMinute(mins) },
        startAnchor = state.selectedTime.minute,
        isNamedAnchor = { anchor -> anchor % 5 == 0 },
        colors = state.colors,
        isAnchorEnabled = isEnabled
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun ClockLayout(
    isNamedAnchor: (Int) -> Boolean = { true },
    anchorPoints: Int,
    innerAnchorPoints: Int = 0,
    label: (Int) -> String,
    startAnchor: Int,
    colors: TimePickerColors,
    isAnchorEnabled: (Int) -> Boolean,
    onAnchorChange: (Int) -> Unit = {},
    onLift: () -> Unit = {}
) {
    BoxWithConstraints {
        val faceDiameter = min(maxHeight.value, maxWidth.value).coerceAtMost(256f).dp
        val faceDiameterPx = with(LocalDensity.current) { faceDiameter.toPx() }

        val faceRadiusPx = faceDiameterPx / 2f

        val outerRadiusPx = faceRadiusPx * 0.8f
        val innerRadiusPx = remember(outerRadiusPx) { outerRadiusPx * 0.65f }

        val selectedRadius = with(LocalDensity.current) { 24.dp.toPx() }
        val selectedInnerDotRadius = with(LocalDensity.current) { 4.dp.toPx() }
        val innerSelectedRadius = remember(innerRadiusPx) { innerRadiusPx * 0.3f }

        val centerCircleRadius = with(LocalDensity.current) { 4.dp.toPx() }
        val selectedLineWidth = with(LocalDensity.current) { 2.dp.toPx() }

        val center = remember { Offset(faceRadiusPx, faceRadiusPx) }

        val namedAnchor = remember(isNamedAnchor) { mutableStateOf(isNamedAnchor(startAnchor)) }
        val selectedAnchor = remember { mutableStateOf(startAnchor) }

        val anchors = remember(anchorPoints, innerAnchorPoints) {
            val anchors = mutableListOf<SelectedOffset>()
            for (x in 0 until anchorPoints) {
                val angle = (2 * PI / anchorPoints) * (x - 15)
                val selectedOuterOffset = outerRadiusPx.getOffset(angle)
                val lineOuterOffset = (outerRadiusPx - selectedRadius).getOffset(angle)

                anchors.add(
                    SelectedOffset(
                        lineOuterOffset, selectedOuterOffset, selectedRadius
                    )
                )
            }
            for (x in 0 until innerAnchorPoints) {
                val angle = (2 * PI / innerAnchorPoints) * (x - 15)
                val selectedOuterOffset = innerRadiusPx.getOffset(angle)
                val lineOuterOffset = (innerRadiusPx - innerSelectedRadius).getOffset(angle)

                anchors.add(
                    SelectedOffset(
                        lineOuterOffset, selectedOuterOffset, innerSelectedRadius
                    )
                )
            }
            anchors
        }

        val anchoredOffset = remember(anchors, startAnchor) { mutableStateOf(anchors[startAnchor]) }

        val updateAnchor: (Offset) -> Boolean = remember(anchors, isAnchorEnabled) {
            { newOffset ->
                val absDiff = anchors.map {
                    val diff = it.selectedOffset - newOffset + center
                    diff.x.pow(2) + diff.y.pow(2)
                }

                val minAnchor = absDiff.withIndex().minByOrNull { (_, f) -> f }!!.index
                if (isAnchorEnabled(minAnchor)) {
                    if (anchoredOffset.value.selectedOffset != anchors[minAnchor].selectedOffset) {
                        onAnchorChange(minAnchor)

                        anchoredOffset.value = anchors[minAnchor]
                        namedAnchor.value = isNamedAnchor(minAnchor)
                        selectedAnchor.value = minAnchor
                    }
                    true
                } else {
                    false
                }
            }
        }

        val dragSuccess = remember { mutableStateOf(false) }

        val dragObserver: suspend PointerInputScope.() -> Unit = {
            detectDragGestures(onDragStart = { dragSuccess.value = true },
                onDragCancel = { dragSuccess.value = false },
                onDragEnd = { if (dragSuccess.value) onLift() }) { change, _ ->
                dragSuccess.value = updateAnchor(change.position)
                if (change.positionChange() != Offset.Zero) change.consume()
            }
        }

        val tapObserver: suspend PointerInputScope.() -> Unit = {
            detectTapGestures(onPress = {
                val anchorsChanged = updateAnchor(it)
                val success = tryAwaitRelease()

                if ((success || !dragSuccess.value) && anchorsChanged) {
                    onLift()
                }
            })
        }

        val clockBackgroundColor = remember { colors.clockDialContainer() }
        val selectorColor = remember { colors.clockDialSelector() }
        val activeLabelColor = colors.clockDialText(active = true).value
        val inactiveLabelColor = colors.clockDialText(active = false).value
        val dialTextStyle = MaterialTheme.typography.bodyLarge
        val textMeasurer = rememberTextMeasurer()

        Canvas(
            modifier = Modifier
                .size(faceDiameter)
                .pointerInput(null, dragObserver)
                .pointerInput(null, tapObserver)
        ) {
            drawCircle(clockBackgroundColor, radius = faceRadiusPx, center = center)
            drawCircle(selectorColor, radius = centerCircleRadius, center = center)
            drawLine(
                color = selectorColor,
                start = center,
                end = center + anchoredOffset.value.lineOffset,
                strokeWidth = selectedLineWidth,
                alpha = 0.8f
            )

            drawCircle(
                selectorColor,
                center = center + anchoredOffset.value.selectedOffset,
                radius = anchoredOffset.value.selectedRadius,
                alpha = 0.7f
            )

            if (!namedAnchor.value) {
                drawCircle(
                    Color.White,
                    center = center + anchoredOffset.value.selectedOffset,
                    radius = selectedInnerDotRadius,
                    alpha = 0.8f
                )
            }

            fun drawAnchorText(
                anchor: Int, angle: Double, radius: Float
            ) {
                val text = label(anchor)
                var textColor = if (selectedAnchor.value == anchor) {
                    activeLabelColor
                } else {
                    inactiveLabelColor
                }

                if (!isAnchorEnabled(anchor)) {
                    textColor = textColor.copy(alpha = TimePickerConstants.DisabledAlpha)
                }

                val textStyle = dialTextStyle.copy(color = textColor)
                val measuredText =
                    textMeasurer.measure(text = AnnotatedString(text), style = textStyle)

                drawText(
                    textMeasurer = textMeasurer, text = text, topLeft = Offset(
                        center.x + (radius * cos(angle)).toFloat() - measuredText.size.width / 2,
                        center.y + (radius * sin(angle)).toFloat() - measuredText.size.height / 2,
                    ), style = textStyle
                )
            }

            for (x in 0 until 12) {
                val angle = (2 * PI / 12) * (x - 15)

                drawAnchorText(
                    anchor = x * anchorPoints / 12, angle = angle, radius = outerRadiusPx
                )

                if (innerAnchorPoints > 0) {
                    drawAnchorText(
                        anchor = x * innerAnchorPoints / 12 + anchorPoints,
                        angle = angle,
                        radius = innerRadiusPx
                    )

                }
            }
        }
    }
}

object TimePickerConstants {
    const val DisabledAlpha = 0.38f
}