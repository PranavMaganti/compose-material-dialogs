package com.vanpra.composematerialdialogs.datetime

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.MaterialDialog
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

private data class SelectedOffset(
    val lineOffset: Offset = Offset.Zero,
    val selectedOffset: Offset = Offset.Zero
)

/* Data class for 12-hour time format */
internal class SimpleLocalTime(hour: Int, minute: Int, isAM: Boolean) {
    var hour by mutableStateOf(hour)
    var minute by mutableStateOf(minute)
    var isAM by mutableStateOf(isAM)

    fun toLocalTime(): LocalTime {
        val fullHour = if (isAM && hour == 12) {
            0
        } else if (isAM) {
            hour
        } else {
            hour + 12
        }
        return LocalTime.of(fullHour, minute)
    }

    companion object {
        fun fromLocalTime(time: LocalTime): SimpleLocalTime {
            val isAM = time.hour < 12
            val hour = if (isAM && time.hour == 0) {
                12
            } else if (isAM) {
                time.hour
            } else {
                time.hour - 12
            }
            return SimpleLocalTime(hour, time.minute, isAM)
        }
    }
}

interface TimePickerColors {
    val border: BorderStroke

    @Composable
    fun backgroundColor(active: Boolean): State<Color>

    @Composable
    fun textColor(active: Boolean): State<Color>
}

private class DefaultTimePickerColors(
    private val activeBackgroundColor: Color,
    private val inactiveBackgroundColor: Color,
    private val activeTextColor: Color,
    private val inactiveTextColor: Color,
    borderColor: Color
) : TimePickerColors {
    override val border = BorderStroke(1.dp, borderColor)

    @Composable
    override fun backgroundColor(active: Boolean): State<Color> {
        return rememberUpdatedState(if (active) activeBackgroundColor else inactiveBackgroundColor)
    }

    @Composable
    override fun textColor(active: Boolean): State<Color> {
        return rememberUpdatedState(if (active) activeTextColor else inactiveTextColor)
    }
}

object DialogDefaults {
    @Composable
    fun timePickerColors(
        activeBackgroundColor: Color = MaterialTheme.colors.primary.copy(0.3f),
        inactiveBackgroundColor: Color = MaterialTheme.colors.onBackground.copy(0.3f),
        activeTextColor: Color = MaterialTheme.colors.onPrimary,
        inactiveTextColor: Color = MaterialTheme.colors.onBackground,
        borderColor: Color = MaterialTheme.colors.onBackground
    ): TimePickerColors {
        return DefaultTimePickerColors(
            activeBackgroundColor = activeBackgroundColor,
            inactiveBackgroundColor = inactiveBackgroundColor,
            activeTextColor = activeTextColor,
            inactiveTextColor = inactiveTextColor,
            borderColor = borderColor
        )
    }
}

internal enum class ClockScreen {
    Hour,
    Minute;

    fun isHour() = this == Hour
    fun isMinute() = this == Minute
}

internal class TimePickerState(
    selectedTime: SimpleLocalTime,
    currentScreen: ClockScreen = ClockScreen.Hour,
    clockInput: Boolean = true,
    val colors: TimePickerColors
) {
    var selectedTime by mutableStateOf(selectedTime)
    var currentScreen by mutableStateOf(currentScreen)
    var clockInput by mutableStateOf(clockInput)
}


/**
 * @brief A time picker dialog
 *
 * @param initialTime The time to be shown to the user when the dialog is first shown.
 * Defaults to the current time if this is not set
 * @param onComplete callback with a LocalTime object when the user completes their input
 * @param onCancel callback when the user cancels the dialog
 */
@Composable
fun MaterialDialog.timepicker(
    initialTime: LocalTime = LocalTime.now(),
    colors: TimePickerColors = DialogDefaults.timePickerColors(),
    onCancel: () -> Unit = {},
    onComplete: (LocalTime) -> Unit = {}
) {
    val simpleTime = remember { SimpleLocalTime.fromLocalTime(initialTime) }
    val timePickerState = remember { TimePickerState(selectedTime = simpleTime, colors = colors) }

    TimePickerImpl(state = timePickerState)
    buttons {
        positiveButton("Ok") {
            onComplete(timePickerState.selectedTime.toLocalTime())
        }
        negativeButton("Cancel") {
            onCancel()
        }
    }
}

@Composable
internal fun TimePickerImpl(
    modifier: Modifier = Modifier,
    state: TimePickerState,
) {
    Column(modifier.padding(start = 24.dp, end = 24.dp)) {
        TimePickerTitle()
        TimeLayout(state)

        Spacer(modifier = Modifier.height(36.dp))
        Crossfade(state.currentScreen) {
            when (it) {
                ClockScreen.Hour -> ClockLayout(
                    anchorPoints = 12,
                    label = { index -> if (index == 0) "12" else index.toString() },
                    onAnchorChange = { hours -> state.selectedTime.hour = hours },
                    startAnchor = state.selectedTime.hour,
                    onLift = { state.currentScreen = ClockScreen.Minute }
                )

                ClockScreen.Minute -> ClockLayout(
                    anchorPoints = 60,
                    label = { index -> index.toString().padStart(2, '0') },
                    onAnchorChange = { mins -> state.selectedTime.minute = mins },
                    startAnchor = state.selectedTime.minute,
                    isNamedAnchor = { anchor -> anchor % 5 == 0 }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
internal fun TimePickerTitle() {
    Box(Modifier.height(52.dp)) {
        Text(
            "SELECT TIME",
            modifier = Modifier.paddingFromBaseline(top = 28.dp),
            style = TextStyle(color = MaterialTheme.colors.onBackground)
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
    Box(
        Modifier.width(96.dp).fillMaxHeight().clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
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

@Composable
internal fun TimeLayout(state: TimePickerState) {
    val topPeriodShape = MaterialTheme.shapes.medium.copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp)
    )
    val bottomPeriodShape =
        MaterialTheme.shapes.medium.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp))


    Row(Modifier.height(80.dp)) {
        ClockLabel(
            text = state.selectedTime.hour.toString(),
            backgroundColor = state.colors.backgroundColor(state.currentScreen.isHour()).value,
            textColor = state.colors.textColor(state.currentScreen.isHour()).value,
            onClick = { state.currentScreen = ClockScreen.Hour }
        )

        Box(
            Modifier.width(24.dp).fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ":",
                style = TextStyle(fontSize = 60.sp, color = MaterialTheme.colors.onBackground)
            )
        }

        ClockLabel(
            text = state.selectedTime.minute.toString().padStart(2, '0'),
            backgroundColor = state.colors.backgroundColor(state.currentScreen.isMinute()).value,
            textColor = state.colors.textColor(state.currentScreen.isMinute()).value,
            onClick = { state.currentScreen = ClockScreen.Minute }

        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(Modifier.fillMaxHeight().border(state.colors.border, MaterialTheme.shapes.medium)) {
            Box(
                modifier = Modifier.size(height = 40.dp, width = 52.dp)
                    .clip(topPeriodShape)
                    .background(state.colors.backgroundColor(state.selectedTime.isAM).value)
                    .clickable { state.selectedTime.isAM = true },
                contentAlignment = Alignment.Center
            ) {
                Text("AM", style = TextStyle(state.colors.textColor(state.selectedTime.isAM).value))
            }

            Spacer(Modifier.fillMaxWidth().height(1.dp).background(state.colors.border.brush))

            Box(
                modifier = Modifier.size(height = 40.dp, width = 52.dp)
                    .clip(bottomPeriodShape)
                    .background(state.colors.backgroundColor(!state.selectedTime.isAM).value)
                    .clickable { state.selectedTime.isAM = false },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "PM",
                    style = TextStyle(state.colors.textColor(!state.selectedTime.isAM).value)
                )
            }
        }
    }
}

@Composable
private fun ClockLayout(
    isNamedAnchor: (Int) -> Boolean = { true },
    anchorPoints: Int,
    label: (Int) -> String,
    startAnchor: Int,
    onAnchorChange: (Int) -> Unit = {},
    onLift: () -> Unit = {}
) {
    val outerRadius = with(LocalDensity.current) { 100.dp.toPx() }
    val selectedRadius = 70f

    val offset = remember { mutableStateOf(Offset.Zero) }
    val center = remember { mutableStateOf(Offset.Zero) }
    val namedAnchor = remember { mutableStateOf(isNamedAnchor(startAnchor)) }

    val anchors = remember {
        val anchors = mutableListOf<SelectedOffset>()
        for (x in 0 until anchorPoints) {
            val angle = (2 * PI / anchorPoints) * (x - 15)
            val selectedOuterOffset = outerRadius.getOffset(angle)
            val lineOuterOffset = (outerRadius - selectedRadius).getOffset(angle)

            anchors.add(
                SelectedOffset(
                    lineOuterOffset,
                    selectedOuterOffset
                )
            )
        }
        anchors
    }

    val anchoredOffset = remember {
        mutableStateOf(anchors[startAnchor])
    }

    fun updateAnchor(newOffset: Offset) {
        val absDiff = anchors.map {
            val diff = it.selectedOffset - newOffset + center.value
            diff.x.pow(2) + diff.y.pow(2)
        }
        val minAnchor = absDiff.withIndex().minByOrNull { (_, f) -> f }?.index
        if (anchoredOffset.value.selectedOffset != anchors[minAnchor!!].selectedOffset) {
            onAnchorChange(label(minAnchor).toInt())

            anchoredOffset.value = anchors[minAnchor]
            namedAnchor.value = isNamedAnchor(minAnchor)
        }
    }


    val dragObserver: suspend PointerInputScope.() -> Unit = {
        detectDragGestures(
            onDragEnd = { onLift() }
        ) { change, _ ->
            updateAnchor(change.position)
            change.consumePositionChange()
        }
    }

    val tapObserver: suspend PointerInputScope.() -> Unit = {
        detectTapGestures(onPress = {
            updateAnchor(it)
            val success = tryAwaitRelease()
            if (success) {
                onLift()
            }
        })
    }

    BoxWithConstraints(
        Modifier.padding(horizontal = 12.dp).size(256.dp)
            .pointerInput(null, dragObserver)
            .pointerInput(null, tapObserver)
    ) {
        SideEffect {
            center.value =
                Offset(constraints.maxWidth.toFloat() / 2f, constraints.maxWidth.toFloat() / 2f)
            offset.value = center.value
        }

        val textColor = MaterialTheme.colors.onBackground.toArgb()
        val selectedColor = MaterialTheme.colors.primary
        val clockSurfaceColor = MaterialTheme.colors.onBackground.copy(0.3f)
        val clockSurfaceDiameter = constraints.maxWidth.toFloat() / 2f

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(clockSurfaceColor, radius = clockSurfaceDiameter, center = center.value)
            drawCircle(selectedColor, radius = 16f, center = center.value)
            drawLine(
                color = selectedColor,
                start = center.value,
                end = center.value + anchoredOffset.value.lineOffset,
                strokeWidth = 10f,
                alpha = 0.8f
            )

            drawCircle(
                selectedColor,
                center = center.value + anchoredOffset.value.selectedOffset,
                radius = selectedRadius,
                alpha = 0.7f
            )

            if (!namedAnchor.value) {
                drawCircle(
                    Color.White,
                    center = center.value + anchoredOffset.value.selectedOffset,
                    radius = 10f,
                    alpha = 0.8f
                )
            }

            drawIntoCanvas { canvas ->
                for (x in 0 until 12) {
                    val angle = (2 * PI / 12) * (x - 15)
                    val textOuter = label(x * anchorPoints / 12)

                    drawText(
                        60f,
                        textOuter,
                        center.value,
                        angle.toFloat(),
                        canvas,
                        outerRadius,
                        color = textColor
                    )
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
