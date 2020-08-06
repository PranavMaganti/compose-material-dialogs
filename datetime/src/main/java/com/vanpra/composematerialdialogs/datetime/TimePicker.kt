package com.vanpra.composematerialdialogs.datetime

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Box
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope.gravity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.gesture.pressIndicatorGestureFilter
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.MaterialDialog
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

private data class SelectedOffset(
    val lineOffset: Offset = Offset.Zero,
    val selectedOffset: Offset = Offset.Zero
)

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
    val selectedTime = state { initialTime.truncatedTo(ChronoUnit.MINUTES) }

    TimePickerLayout(selectedTime = selectedTime)

    buttons {
        positiveButton("Ok") {
            onComplete(selectedTime.value)
        }
        negativeButton("Cancel") {
            onCancel()
        }
    }
}

@Composable
internal fun TimePickerLayout(
    modifier: Modifier = Modifier,
    selectedTime: MutableState<LocalTime>
) {
    val currentScreen = state { 0 }
    Column(modifier) {
        TimeLayout(currentScreen, selectedTime)
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
                            selectedTime.value = selectedTime.value.withHour(hours)
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
                        selectedTime.value = selectedTime.value.withMinute(mins)
                    },
                    selectedTime = selectedTime
                )
            }
        }
    }
}

@Composable
private fun TimeLayout(currentScreen: MutableState<Int>, selectedTime: MutableState<LocalTime>) {
    Box(Modifier.fillMaxWidth().background(color = MaterialTheme.colors.primaryVariant)) {
        val textSize = 60.sp
        val color = MaterialTheme.colors.onPrimary
        val hourAlpha = 1f - 0.4f * currentScreen.value
        val minAlpha = 0.6f + 0.4f * currentScreen.value

        Row(
            Modifier.gravity(Alignment.CenterVertically)
                .wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            Text(
                selectedTime.value.hour.toString().padStart(2, '0'),
                fontSize = textSize,
                color = color.copy(hourAlpha),
                modifier = Modifier.clickable(
                    onClick = { currentScreen.value = 0 },
                    indication = null
                )
            )

            Text(":", fontSize = textSize, color = color)

            Text(
                selectedTime.value.minute.toString().padStart(2, '0'),
                fontSize = textSize,
                color = color.copy(minAlpha),
                modifier = Modifier.clickable(
                    onClick = { currentScreen.value = 1 },
                    indication = null
                )
            )
        }
    }
}

@Composable
private fun ClockLayout(
    isHours: Boolean,
    anchorPoints: Int,
    label: (Int) -> String,
    selectedTime: MutableState<LocalTime>,
    onAnchorChange: (Int) -> Unit = {},
    onLift: () -> Unit = {}
) {
    val outerRadius = with(DensityAmbient.current) { 100.dp.toPx() }
    val innerRadius = with(DensityAmbient.current) { 60.dp.toPx() }
    val selectedRadius = 70f

    val offset = state { Offset.Zero }
    val center = state { Offset.Zero }
    val namedAnchor = state { true }

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

            if (isHours) {
                val selectedInnerOffset = innerRadius.getOffset(angle)
                val lineInnerOffset = (innerRadius - selectedRadius).getOffset(angle)
                anchors.add(
                    SelectedOffset(
                        lineInnerOffset,
                        selectedInnerOffset
                    )
                )
            }
        }
        anchors
    }

    val anchoredOffset = state {
        if (!isHours) {
            namedAnchor.value = selectedTime.value.minute % 5 == 0
            anchors[selectedTime.value.minute]
        } else {
            when (selectedTime.value.hour) {
                0 -> anchors[1]
                in 1..11 -> anchors[selectedTime.value.hour * 2]
                in 13..23 -> anchors[(selectedTime.value.hour - 12) * 2 + 1]
                else -> anchors[0]
            }
        }
    }

    fun updateAnchor() {
        val absDiff =
            anchors.map {
                val diff = it.selectedOffset - offset.value + center.value
                diff.x.pow(2) + diff.y.pow(2)
            }
        val minAnchor = absDiff.withIndex().minByOrNull { (_, f) -> f }?.index
        if (anchoredOffset.value.selectedOffset != anchors[minAnchor!!].selectedOffset) {
            onAnchorChange(
                if (isHours && minAnchor % 2 == 1) {
                    if (minAnchor != 1) {
                        (minAnchor / 2 + 12)
                    } else {
                        0
                    }
                } else if (isHours) {
                    label(minAnchor / 2).toInt()
                } else {
                    label(minAnchor).toInt()
                }
            )

            anchoredOffset.value = anchors[minAnchor]
            if (!isHours) {
                namedAnchor.value = minAnchor % 5 == 0
            }
        }
    }

    val dragObserver =
        object : DragObserver {
            override fun onStart(downPosition: Offset) {
                offset.value = Offset(downPosition.x, downPosition.y)
            }

            override fun onStop(velocity: Offset) {
                super.onStop(velocity)
                onLift()
            }

            override fun onDrag(dragDistance: Offset): Offset {
                offset.value = Offset(
                    offset.value.x + dragDistance.x,
                    offset.value.y + dragDistance.y
                )
                updateAnchor()
                return dragDistance
            }
        }

    val touchFilter = { pos: Offset ->
        offset.value = pos
        updateAnchor()
    }

    WithConstraints {
        Box(
            Modifier.preferredSize(maxWidth).pressIndicatorGestureFilter(touchFilter, onLift)
                .dragGestureFilter(dragObserver)
        ) {
            remember {
                center.value =
                    Offset(constraints.maxWidth / 2f, constraints.maxWidth / 2f)
                offset.value = center.value
            }
            val textColor = MaterialTheme.colors.onBackground.toArgb()
            val selectedColor = MaterialTheme.colors.secondary

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(selectedColor, radius = 16f)

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

                drawCanvas { canvas, _ ->
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

                        if (isHours) {
                            val textInner = if (x != 0) {
                                (x + 12).toString()
                            } else {
                                "00"
                            }

                            drawText(
                                45f,
                                textInner,
                                center.value,
                                angle.toFloat(),
                                canvas,
                                innerRadius,
                                200,
                                color = textColor
                            )
                        }
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
