package com.vanpra.composematerialdialogs.datetime.time

import android.graphics.Paint
import android.graphics.Rect
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
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
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.datetime.util.getOffset
import com.vanpra.composematerialdialogs.datetime.util.isAM
import com.vanpra.composematerialdialogs.datetime.util.isSmallDevice
import com.vanpra.composematerialdialogs.datetime.util.noSeconds
import com.vanpra.composematerialdialogs.datetime.util.simpleHour
import com.vanpra.composematerialdialogs.datetime.util.toAM
import com.vanpra.composematerialdialogs.datetime.util.toPM
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
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
    title: String = "SELECT TIME",
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
internal fun TimePickerExpandedImpl(
    modifier: Modifier = Modifier,
    title: String,
    state: TimePickerState,
) {
    Column(modifier.padding(start = 24.dp, end = 24.dp)) {
        Box(Modifier.align(Alignment.Start)) {
            TimePickerTitle(Modifier.height(36.dp), title)
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Column(
                Modifier
                    .padding(top = 72.dp, bottom = 50.dp)
                    .width(216.dp)
            ) {
                TimeLayout(state = state)
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalPeriodPicker(state = state)
            }

            /* This isn't an exact match to the material spec as there is a contradiction it.
            Dialogs should be limited to the size of 560 dp but given sizes for extended
            time picker go over this limit */
            Spacer(modifier = Modifier.width(40.dp))
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

@Composable
internal fun TimePickerImpl(
    modifier: Modifier = Modifier,
    title: String,
    state: TimePickerState
) {
    Column(
        modifier.padding(start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title != "") {
            Box(Modifier.align(Alignment.Start)) {
                TimePickerTitle(Modifier.height(52.dp), title)
            }
        }

        TimeLayout(state = state)

        Spacer(modifier = Modifier.height(if (isSmallDevice()) 24.dp else 36.dp))
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

        Spacer(modifier = Modifier.height(24.dp))
    }
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

@Composable
internal fun TimePickerTitle(modifier: Modifier, text: String) {
    Box(modifier) {
        Text(
            text,
            modifier = Modifier.paddingFromBaseline(top = 28.dp),
            style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}

@Composable
internal fun ClockLabel(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(if (isSmallDevice()) 80.dp else 96.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 50.sp,
                    color = textColor
                )
            )
        }
    }
}

@Composable
internal fun TimeLayout(modifier: Modifier = Modifier, state: TimePickerState) {
    val clockHour: String = remember(
        state.is24Hour,
        state.selectedTime,
        state.selectedTime.hour
    ) {
        if (state.is24Hour) {
            state.selectedTime.hour.toString().padStart(2, '0')
        } else {
            state.selectedTime.simpleHour.toString()
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .height(80.dp)
            .fillMaxWidth()
    ) {
        ClockLabel(
            text = clockHour,
            backgroundColor = state.colors.backgroundColor(state.currentScreen.isHour()).value,
            textColor = state.colors.textColor(state.currentScreen.isHour()).value,
            onClick = { state.currentScreen = ClockScreen.Hour }
        )

        Box(
            Modifier
                .width(24.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ":",
                style = TextStyle(fontSize = 50.sp, color = MaterialTheme.colorScheme.onBackground)
            )
        }

        ClockLabel(
            text = state.selectedTime.minute.toString().padStart(2, '0'),
            backgroundColor = state.colors.backgroundColor(state.currentScreen.isMinute()).value,
            textColor = state.colors.textColor(state.currentScreen.isMinute()).value,
            onClick = { state.currentScreen = ClockScreen.Minute }

        )

        if (!state.is24Hour) {
            VerticalPeriodPicker(state = state)
        }
    }
}

@Composable
private fun VerticalPeriodPicker(state: TimePickerState) {
    val topPeriodShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    val bottomPeriodShape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)

    val isAMEnabled = remember(state.timeRange) { state.timeRange.start.hour <= 12 }
    val isPMEnabled = remember(state.timeRange) { state.timeRange.endInclusive.hour >= 0 }

    Spacer(modifier = Modifier.width(12.dp))

    Column(
        Modifier
            .fillMaxHeight()
            .width(52.dp)
            .border(state.colors.border, RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .size(height = 40.dp, width = 52.dp)
                .clip(topPeriodShape)
                .background(state.colors.periodBackgroundColor(state.selectedTime.isAM).value)
                .then(
                    if (isAMEnabled) Modifier.clickable {
                        state.selectedTime = state.selectedTime
                            .toAM()
                            .coerceIn(state.timeRange)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "AM",
                style = TextStyle(
                    state.colors
                        .textColor(state.selectedTime.isAM).value
                        .copy(alpha = if (isAMEnabled) ContentAlpha.high else ContentAlpha.disabled)
                )
            )
        }

        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(state.colors.border.brush)
        )

        Box(
            modifier = Modifier
                .size(height = 40.dp, width = 52.dp)
                .clip(bottomPeriodShape)
                .background(state.colors.periodBackgroundColor(!state.selectedTime.isAM).value)
                .then(
                    if (isPMEnabled) Modifier.clickable {
                        state.selectedTime = state.selectedTime
                            .toPM()
                            .coerceIn(state.timeRange)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "PM",
                style = TextStyle(
                    state.colors.textColor(!state.selectedTime.isAM).value.copy(
                        alpha = if (isPMEnabled) ContentAlpha.high else ContentAlpha.disabled
                    )
                )
            )
        }
    }
}

@Composable
private fun HorizontalPeriodPicker(state: TimePickerState) {
    val leftPeriodShape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
    val rightPeriodShape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
    val isAMEnabled = remember(state.timeRange) { state.timeRange.start.hour <= 12 }
    val isPMEnabled = remember(state.timeRange) { state.timeRange.endInclusive.hour >= 0 }

    Spacer(modifier = Modifier.width(12.dp))

    Row(
        Modifier
            .fillMaxWidth()
            .height(height = 40.dp)
            .border(state.colors.border, RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .clip(leftPeriodShape)
                .background(state.colors.periodBackgroundColor(state.selectedTime.isAM).value)
                .then(
                    if (isAMEnabled) Modifier.clickable {
                        state.selectedTime = state.selectedTime
                            .toAM()
                            .coerceIn(state.timeRange)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "AM",
                style = TextStyle(
                    state.colors
                        .textColor(state.selectedTime.isAM).value
                        .copy(alpha = if (isAMEnabled) ContentAlpha.high else ContentAlpha.disabled)
                )
            )
        }

        Spacer(
            Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(state.colors.border.brush)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(rightPeriodShape)
                .background(state.colors.periodBackgroundColor(!state.selectedTime.isAM).value)
                .then(
                    if (isPMEnabled) Modifier.clickable {
                        state.selectedTime = state.selectedTime
                            .toPM()
                            .coerceIn(state.timeRange)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "PM",
                style = TextStyle(
                    state.colors.textColor(!state.selectedTime.isAM).value.copy(
                        alpha = if (isPMEnabled) ContentAlpha.high else ContentAlpha.disabled
                    )
                )
            )
        }
    }
}

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
        val innerRadiusPx = remember(outerRadiusPx) { outerRadiusPx * 0.6f }

        val textSizePx = with(LocalDensity.current) { 18.sp.toPx() }
        val innerTextSizePx = remember(textSizePx) { textSizePx * 0.8f }

        val selectedRadius = remember(outerRadiusPx) { outerRadiusPx * 0.2f }
        val selectedInnerDotRadius = remember(selectedRadius) { selectedRadius * 0.2f }
        val innerSelectedRadius = remember(innerRadiusPx) { innerRadiusPx * 0.3f }

        val centerCircleRadius = remember(selectedRadius) { selectedRadius * 0.4f }
        val selectedLineWidth = remember(centerCircleRadius) { centerCircleRadius * 0.5f }

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
                        lineOuterOffset,
                        selectedOuterOffset,
                        selectedRadius
                    )
                )
            }
            for (x in 0 until innerAnchorPoints) {
                val angle = (2 * PI / innerAnchorPoints) * (x - 15)
                val selectedOuterOffset = innerRadiusPx.getOffset(angle)
                val lineOuterOffset = (innerRadiusPx - innerSelectedRadius).getOffset(angle)

                anchors.add(
                    SelectedOffset(
                        lineOuterOffset,
                        selectedOuterOffset,
                        innerSelectedRadius
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
            detectDragGestures(
                onDragStart = { dragSuccess.value = true },
                onDragCancel = { dragSuccess.value = false },
                onDragEnd = { if (dragSuccess.value) onLift() },
            ) { change, _ ->
                dragSuccess.value = updateAnchor(change.position)
                change.consumePositionChange()
            }
        }

        val tapObserver: suspend PointerInputScope.() -> Unit = {
            detectTapGestures(
                onPress = {
                    val anchorsChanged = updateAnchor(it)
                    val success = tryAwaitRelease()

                    if ((success || !dragSuccess.value) && anchorsChanged) {
                        onLift()
                    }
                }
            )
        }

        val inactiveTextColor = colors.textColor(false).value.toArgb()
        val clockBackgroundColor = colors.backgroundColor(false).value
        val selectorColor = remember { colors.selectorColor() }
        val selectorTextColor = remember { colors.selectorTextColor().toArgb() }

        val enabledAlpha = ContentAlpha.high
        val disabledAlpha = ContentAlpha.disabled

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

            drawIntoCanvas { canvas ->
                fun drawAnchorText(
                    anchor: Int,
                    textSize: Float,
                    radius: Float,
                    angle: Double,
                    alpha: Int = 255
                ) {
                    val textOuter = label(anchor)
                    val textColor = if (selectedAnchor.value == anchor) {
                        selectorTextColor
                    } else {
                        inactiveTextColor
                    }

                    val contentAlpha = if (isAnchorEnabled(anchor)) enabledAlpha else disabledAlpha

                    drawText(
                        textSize,
                        textOuter,
                        center,
                        angle.toFloat(),
                        canvas,
                        radius,
                        alpha = (255f * contentAlpha).roundToInt().coerceAtMost(alpha),
                        color = textColor
                    )
                }

                for (x in 0 until 12) {
                    val angle = (2 * PI / 12) * (x - 15)
                    drawAnchorText(x * anchorPoints / 12, textSizePx, outerRadiusPx, angle)

                    if (innerAnchorPoints > 0) {
                        drawAnchorText(
                            x * innerAnchorPoints / 12 + anchorPoints,
                            innerTextSizePx,
                            innerRadiusPx,
                            angle,
                            alpha = (255 * 0.8f).toInt()
                        )
                    }
                }
            }
        }
    }
}

private fun drawText(
    textSize: Float,
    text: String,
    center: Offset,
    angle: Float,
    canvas: Canvas,
    radius: Float,
    alpha: Int = 255,
    color: Int = android.graphics.Color.WHITE
) {
    val outerText = Paint()
    outerText.color = color
    outerText.textSize = textSize
    outerText.textAlign = Paint.Align.CENTER
    outerText.alpha = alpha

    val r = Rect()
    outerText.getTextBounds(text, 0, text.length, r)

    canvas.nativeCanvas.drawText(
        text,
        center.x + (radius * cos(angle)),
        center.y + (radius * sin(angle)) + (abs(r.height())) / 2,
        outerText
    )
}
