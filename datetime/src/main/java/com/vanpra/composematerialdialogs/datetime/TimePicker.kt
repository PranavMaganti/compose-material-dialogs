package com.vanpra.composematerialdialogs.datetime

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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

/* Offset of the clock line and selected circle */
private data class SelectedOffset(
    val lineOffset: Offset = Offset.Zero,
    val selectedOffset: Offset = Offset.Zero
)

/* Data class for 12-hour time format */
internal class SimpleLocalTime(hour: Int, minute: Int, isAM: Boolean) {
    var hour by mutableStateOf(hour)
    var minute by mutableStateOf(minute)
    var isAM by mutableStateOf(isAM)

    /* Converts a SimpleLocalTime object to a LocalTime object */
    fun toLocalTime(): LocalTime {
        val fullHour = if (isAM && hour == 12) {
            0
        } else if (isAM || hour == 12) {
            hour
        } else if (hour == 12) {
            12
        } else {
            hour + 12
        }
        return LocalTime.of(fullHour, minute)
    }

    companion object {
        /* Initalises a SimpleLocalTime object from a LocalTime object */
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
 * Represents the colors used by a [timepicker] and its parts in different states
 *
 * See [TimePickerDefaults.colors] for the default implementation
 */
interface TimePickerColors {
    val border: BorderStroke

    /**
     * Gets the background color dependant on if the item is active or not
     *
     * @param active true if the component/item is selected and false otherwise
     * @return background color as a State
     */
    @Composable
    fun backgroundColor(active: Boolean): State<Color>

    /**
     * Gets the text color dependant on if the item is active or not
     *
     * @param active true if the component/item is selected and false otherwise
     * @return text color as a State
     */
    @Composable
    fun textColor(active: Boolean): State<Color>

    fun selectorColor(): Color
    fun selectorTextColor(): Color

    @Composable
    fun periodBackgroundColor(active: Boolean): State<Color>
}

private class DefaultTimePickerColors(
    private val activeBackgroundColor: Color,
    private val inactiveBackgroundColor: Color,
    private val activeTextColor: Color,
    private val inactiveTextColor: Color,
    private val inactivePeriodBackground: Color,
    private val selectorColor: Color,
    private val selectorTextColor: Color,
    borderColor: Color
) : TimePickerColors {
    override val border = BorderStroke(1.dp, borderColor)

    @Composable
    override fun backgroundColor(active: Boolean): State<Color> {
        SliderDefaults
        return rememberUpdatedState(if (active) activeBackgroundColor else inactiveBackgroundColor)
    }

    @Composable
    override fun textColor(active: Boolean): State<Color> {
        return rememberUpdatedState(if (active) activeTextColor else inactiveTextColor)
    }

    override fun selectorColor(): Color {
        return selectorColor
    }

    override fun selectorTextColor(): Color {
        return selectorTextColor
    }

    @Composable
    override fun periodBackgroundColor(active: Boolean): State<Color> {
        return rememberUpdatedState(if (active) activeBackgroundColor else inactivePeriodBackground)
    }
}

/**
* Object to hold default values used by [timepicker]
*/
object TimePickerDefaults {
    /**
     * Initialises a [TimePickerColors] object which represents the different colors used by
     * the [timepicker] composable
     *
     * @param activeBackgroundColor background color of selected time unit or period (AM/PM)
     * @param inactiveBackgroundColor background color of inactive items in the dialog including the clock face
     * @param activeTextColor color of text on the activeBackgroundColor
     * @param inactiveTextColor color of text on the inactiveBackgroundColor
     * @param inactivePeriodBackground background color of the inactive period (AM/PM) selector
     * @param borderColor border color of the period (AM/PM) selector
     * @param selectorColor color of clock hand/selector
     * @param selectorTextColor color of text on selectedColor
     */
    @Composable
    fun colors(
        activeBackgroundColor: Color = MaterialTheme.colors.primary.copy(0.3f),
        inactiveBackgroundColor: Color = MaterialTheme.colors.onBackground.copy(0.3f),
        activeTextColor: Color = MaterialTheme.colors.onPrimary,
        inactiveTextColor: Color = MaterialTheme.colors.onBackground,
        inactivePeriodBackground: Color = Color.Transparent,
        borderColor: Color = MaterialTheme.colors.onBackground,
        selectorColor: Color = MaterialTheme.colors.primary,
        selectorTextColor: Color = MaterialTheme.colors.onPrimary
    ): TimePickerColors {
        return DefaultTimePickerColors(
            activeBackgroundColor = activeBackgroundColor,
            inactiveBackgroundColor = inactiveBackgroundColor,
            activeTextColor = activeTextColor,
            inactiveTextColor = inactiveTextColor,
            inactivePeriodBackground = inactivePeriodBackground,
            selectorColor = selectorColor,
            selectorTextColor = selectorTextColor,
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
    constructor(
        selectedTime: LocalTime,
        currentScreen: ClockScreen = ClockScreen.Hour,
        clockInput: Boolean = true,
        colors: TimePickerColors
    ) : this(SimpleLocalTime.fromLocalTime(selectedTime), currentScreen, clockInput, colors)

    var selectedTime by mutableStateOf(selectedTime)
    var currentScreen by mutableStateOf(currentScreen)
    var clockInput by mutableStateOf(clockInput)
}

/**
 * @brief A time picker dialog
 *
 * @param initialTime The time to be shown to the user when the dialog is first shown.
 * Defaults to the current time if this is not set
 * @param colors see [TimePickerColors]
 * @param waitForPositiveButton if true the [onComplete] callback will only be called when the
 * positive button is pressed, otherwise it will be called on every input change
 * @param onComplete callback with a LocalTime object when the user completes their input
 */
@Composable
fun MaterialDialog.timepicker(
    initialTime: LocalTime = LocalTime.now(),
    colors: TimePickerColors = TimePickerDefaults.colors(),
    waitForPositiveButton: Boolean = true,
    onComplete: (LocalTime) -> Unit = {}
) {
    val timePickerState = remember { TimePickerState(selectedTime = initialTime, colors = colors) }

    val index = remember {
        val callbackIndex = callbackCounter.getAndIncrement()
        callbacks.add(callbackIndex) {}
        callbackIndex
    }

    DisposableEffect(timePickerState.selectedTime) {
        if (waitForPositiveButton) {
            callbacks[index] = { onComplete(timePickerState.selectedTime.toLocalTime()) }
        } else {
            onComplete(timePickerState.selectedTime.toLocalTime())
        }

        onDispose { callbacks[index] = {} }
    }

    TimePickerImpl(state = timePickerState)
}

@Composable
internal fun TimePickerImpl(
    modifier: Modifier = Modifier,
    state: TimePickerState,
    onBack: (() -> Unit)? = null
) {
    Column(modifier.padding(start = 24.dp, end = 24.dp)) {
        TimePickerTitle(onBack)
        TimeLayout(state)

        Spacer(modifier = Modifier.height(36.dp))
        Crossfade(state.currentScreen) {
            when (it) {
                ClockScreen.Hour -> ClockLayout(
                    anchorPoints = 12,
                    label = { index -> if (index == 0) "12" else index.toString() },
                    onAnchorChange = { hours -> state.selectedTime.hour = hours },
                    startAnchor = if (state.selectedTime.hour == 12) 0 else state.selectedTime.hour,
                    onLift = { state.currentScreen = ClockScreen.Minute },
                    colors = state.colors
                )

                ClockScreen.Minute -> ClockLayout(
                    anchorPoints = 60,
                    label = { index -> index.toString().padStart(2, '0') },
                    onAnchorChange = { mins -> state.selectedTime.minute = mins },
                    startAnchor = state.selectedTime.minute,
                    isNamedAnchor = { anchor -> anchor % 5 == 0 },
                    colors = state.colors
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
internal fun TimePickerTitle(onBack: (() -> Unit)?) {
    if (onBack != null) {
        Row(Modifier.height(52.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.clip(CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    Icons.Default.ArrowBack,
                    contentDescription = "Go back to date selection",
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                    modifier = Modifier
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                "SELECT TIME",
                style = TextStyle(color = MaterialTheme.colors.onBackground)
            )
        }
    } else {
        Box(Modifier.height(52.dp)) {
            Text(
                "SELECT TIME",
                modifier = Modifier.paddingFromBaseline(top = 28.dp),
                style = TextStyle(color = MaterialTheme.colors.onBackground)
            )
        }
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
        modifier = Modifier.width(96.dp).fillMaxHeight().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    .background(state.colors.periodBackgroundColor(state.selectedTime.isAM).value)
                    .clickable { state.selectedTime.isAM = true },
                contentAlignment = Alignment.Center
            ) {
                Text("AM", style = TextStyle(state.colors.textColor(state.selectedTime.isAM).value))
            }

            Spacer(Modifier.fillMaxWidth().height(1.dp).background(state.colors.border.brush))

            Box(
                modifier = Modifier.size(height = 40.dp, width = 52.dp)
                    .clip(bottomPeriodShape)
                    .background(state.colors.periodBackgroundColor(!state.selectedTime.isAM).value)
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
    colors: TimePickerColors,
    onAnchorChange: (Int) -> Unit = {},
    onLift: () -> Unit = {}
) {
    val outerRadius = with(LocalDensity.current) { 100.dp.toPx() }
    val selectedRadius = 70f

    val offset = remember { mutableStateOf(Offset.Zero) }
    val center = remember { mutableStateOf(Offset.Zero) }
    val namedAnchor = remember { mutableStateOf(isNamedAnchor(startAnchor)) }
    val selectedAnchor = remember { mutableStateOf(startAnchor) }

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

    val anchoredOffset = remember { mutableStateOf(anchors[startAnchor]) }

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
            selectedAnchor.value = minAnchor
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
        detectTapGestures(
            onPress = {
                updateAnchor(it)
                val success = tryAwaitRelease()
                if (success) {
                    onLift()
                }
            }
        )
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

        val inactiveTextColor = colors.textColor(false).value.toArgb()
        val clockBackgroundColor = colors.backgroundColor(false).value
        val selectorColor = remember { colors.selectorColor() }
        val selectorTextColor = remember { colors.selectorTextColor().toArgb() }
        val clockSurfaceDiameter = remember(constraints.maxWidth) { constraints.maxWidth.toFloat() / 2f }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(clockBackgroundColor, radius = clockSurfaceDiameter, center = center.value)
            drawCircle(selectorColor, radius = 16f, center = center.value)
            drawLine(
                color = selectorColor,
                start = center.value,
                end = center.value + anchoredOffset.value.lineOffset,
                strokeWidth = 10f,
                alpha = 0.8f
            )

            drawCircle(
                selectorColor,
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
                    val textColor = if (selectedAnchor.value == textOuter.toInt()) {
                        selectorTextColor
                    } else {
                        inactiveTextColor
                    }

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
