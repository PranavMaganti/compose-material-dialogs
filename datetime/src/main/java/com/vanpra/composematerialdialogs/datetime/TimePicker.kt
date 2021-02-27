package com.vanpra.composematerialdialogs.datetime

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.animation.Crossfade
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
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
    onCancel: () -> Unit = {},
    onComplete: (LocalTime) -> Unit = {}
) {
    val selectedTime = remember { mutableStateOf(SimpleLocalTime.fromLocalTime(initialTime)) }

    TimePickerLayout(selectedTime = selectedTime)
    buttons {
        positiveButton("Ok") {
            onComplete(selectedTime.value.toLocalTime())
        }
        negativeButton("Cancel") {
            onCancel()
        }
    }
}

@Composable
internal fun TimePickerLayout(
    modifier: Modifier = Modifier,
    selectedTime: MutableState<SimpleLocalTime>
) {
    Column(modifier.padding(start = 24.dp, end = 24.dp)) {
        val currentScreen = remember { mutableStateOf(0) }

        TimePickerTitle()
        TimeLayout(currentScreen = currentScreen, selectedTime = selectedTime)
        Spacer(modifier = Modifier.height(36.dp))
        Crossfade(currentScreen) {
            when (it.value) {
                0 ->
                    ClockLayout(
                        isHours = true,
                        anchorPoints = 12,
                        label = { index ->
                            if (index == 0) {
                                "12"
                            } else {
                                index.toString()
                            }
                        },
                        onAnchorChange = { hours ->
                            selectedTime.value.hour = hours
                        },
                        selectedTime = selectedTime
                    ) {
                        currentScreen.value = 1
                    }

                1 -> ClockLayout(
                    isHours = false,
                    anchorPoints = 60,
                    label = { index ->
                        index.toString().padStart(2, '0')
                    },
                    onAnchorChange = { mins ->
                        selectedTime.value.minute = mins
                    },
                    selectedTime = selectedTime
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

data class TimeLabelColors(
    val textColor: Color,
    val backgroundColor: Color
)

@Composable
private fun periodLabelColors(time: SimpleLocalTime, isAM: Boolean): TimeLabelColors {
    val colors = MaterialTheme.colors
    return remember(time.isAM) {
        if (time.isAM == isAM) {
            TimeLabelColors(
                colors.onPrimary,
                colors.primary.copy(0.5f)
            )
        } else {
            TimeLabelColors(
                colors.onBackground,
                colors.background
            )
        }
    }
}

@Composable
private fun timeLabelColors(
    currentScreen: Int,
    targetScreen: Int
): TimeLabelColors {
    val colors = MaterialTheme.colors
    return remember(currentScreen) {
        if (currentScreen == targetScreen) {
            TimeLabelColors(
                colors.onPrimary,
                colors.primary.copy(0.5f)
            )
        } else {
            TimeLabelColors(
                colors.onBackground,
                colors.onBackground.copy(0.3f)
            )
        }
    }
}

@Composable
internal fun TimeLayout(
    currentScreen: MutableState<Int>,
    selectedTime: MutableState<SimpleLocalTime>
) {
    val hourColors = timeLabelColors(currentScreen.value, 0)
    val minuteColors = timeLabelColors(currentScreen.value, 1)
    val amColors = periodLabelColors(selectedTime.value, isAM = true)
    val pmColors = periodLabelColors(selectedTime.value, isAM = false)

    val borderColor = MaterialTheme.colors.onBackground
    val border = BorderStroke(1.dp, borderColor)
    val topPeriodShape = MaterialTheme.shapes.medium.copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp)
    )
    val bottomPeriodShape =
        MaterialTheme.shapes.medium.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp))


    Row(Modifier.height(80.dp)) {
        Box(
            Modifier.width(96.dp).fillMaxHeight().clip(MaterialTheme.shapes.medium)
                .background(hourColors.backgroundColor).clickable { currentScreen.value = 0 },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = selectedTime.value.hour.toString(),
                style = TextStyle(fontSize = 50.sp, color = hourColors.textColor)
            )
        }

        Box(
            Modifier.width(24.dp).fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ":",
                style = TextStyle(fontSize = 60.sp, color = MaterialTheme.colors.onBackground)
            )
        }

        Box(
            modifier = Modifier.width(96.dp).fillMaxHeight().clip(MaterialTheme.shapes.medium)
                .background(minuteColors.backgroundColor).clickable { currentScreen.value = 1 },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = selectedTime.value.minute.toString().padStart(2, '0'),
                style = TextStyle(fontSize = 50.sp, color = minuteColors.textColor)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(Modifier.fillMaxHeight().border(border, MaterialTheme.shapes.medium)) {
            Box(
                modifier = Modifier.size(height = 40.dp, width = 52.dp)
                    .clip(topPeriodShape)
                    .background(amColors.backgroundColor)
                    .clickable { selectedTime.value.isAM = true },
                contentAlignment = Alignment.Center
            ) {
                Text("AM", style = TextStyle(amColors.textColor))
            }

            Spacer(Modifier.fillMaxWidth().height(1.dp).background(borderColor))

            Box(
                modifier = Modifier.size(height = 40.dp, width = 52.dp)
                    .clip(bottomPeriodShape)
                    .background(pmColors.backgroundColor)
                    .clickable { selectedTime.value.isAM = false },
                contentAlignment = Alignment.Center
            ) {
                Text("PM", style = TextStyle(pmColors.textColor))
            }
        }
    }
}

@Composable
private fun ClockLayout(
    isHours: Boolean,
    anchorPoints: Int,
    label: (Int) -> String,
    selectedTime: MutableState<SimpleLocalTime>,
    onAnchorChange: (Int) -> Unit = {},
    onLift: () -> Unit = {}
) {
    val outerRadius = with(LocalDensity.current) { 100.dp.toPx() }
    val selectedRadius = 70f

    val offset = remember { mutableStateOf(Offset.Zero) }
    val center = remember { mutableStateOf(Offset.Zero) }
    val namedAnchor = remember { mutableStateOf(true) }

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
        mutableStateOf(
            if (!isHours) {
                namedAnchor.value = selectedTime.value.minute % 5 == 0
                anchors[selectedTime.value.minute]
            } else {
                anchors[selectedTime.value.hour % 12]
            }
        )
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
            if (!isHours) {
                namedAnchor.value = minAnchor % 5 == 0
            }
        }
    }


    val dragObserver: suspend PointerInputScope.() -> Unit = {
        detectDragGestures(
//            onDragStart = { offset.value = Offset(it.x, it.y) },
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
        Modifier.padding(horizontal = 12.dp).size(256.dp).pointerInput(Unit, dragObserver)
            .pointerInput(Unit, tapObserver)
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
