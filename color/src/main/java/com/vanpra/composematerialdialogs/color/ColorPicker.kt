package com.vanpra.composematerialdialogs.color

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.Text
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import com.vanpra.composematerialdialogs.MaterialDialog

val itemSizeDp = 55.dp
val tickSize = 35.dp

/**
 * @brief Adds a color chooser to the dialog
 *
 * @param colors a list of colors for the user to choose. See [ColorPalette] for predefined colors
 * @param subColors a list of subsets of [colors] for the user to choose from once a main color from
 * colors has been chosen. See [ColorPalette] for predefined sub-colors colors
 * @param initialSelection the index of the color which is selected initially
 * @param allowCustomArgb if true this will allow the user to choose a custom color using
 * ARGB sliders
 * @param waitForPositiveButton if true the [onColorSelected] callback will only be called when the
 * positive button is pressed, otherwise it will be called when the a new color is selected
 * @param onColorSelected a function which is called with a [Color]. The timing of this call is
 * dictated by [waitForPositiveButton]
 */
@Composable
fun MaterialDialog.colorChooser(
    colors: List<Color>,
    subColors: List<List<Color>> = listOf(),
    initialSelection: Int = 0,
    allowCustomArgb: Boolean = false,
    waitForPositiveButton: Boolean = false,
    onColorSelected: (Color) -> Unit = {}
) {
    WithConstraints {
        val selectedColor = remember { mutableStateOf(colors[initialSelection]) }
        val flingConfig = FlingConfig(listOf(0f, constraints.maxWidth.toFloat()))

        val scrollerPosition =
            ScrollState(
                initial = 0f, flingConfig = flingConfig,
                animationClock = AnimationClockAmbient.current
            )

        remember {
            if (waitForPositiveButton) {
                callbacks.add {
                    onColorSelected(selectedColor.value)
                }
            }
        }

        Column(Modifier.padding(bottom = 8.dp)) {
            if (allowCustomArgb) {
                PageIndicator(scrollerPosition, constraints)
                ScrollableRow(scrollState = scrollerPosition, children = {
                    Box(Modifier.width(maxWidth)) {
                        ColorGridLayout(
                            colors = colors,
                            selectedColor = selectedColor,
                            subColors = subColors,
                            waitForPositiveButton = waitForPositiveButton,
                            onColorSelected = onColorSelected
                        )
                    }
                    Box(Modifier.width(maxWidth)) {
                        CustomARGB(selectedColor)
                    }
                })
            } else {
                ColorGridLayout(
                    colors = colors,
                    selectedColor = selectedColor,
                    subColors = subColors,
                    waitForPositiveButton = waitForPositiveButton,
                    onColorSelected = onColorSelected
                )
            }
        }
    }
}

@Composable
private fun PageIndicator(scrollerState: ScrollState, constraints: Constraints) {
    Row(
        Modifier.fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        val ratio = scrollerState.value / constraints.maxWidth
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
}

@Composable
private fun CustomARGB(selectedColor: MutableState<Color>) {
    Column(Modifier.padding(start = 24.dp, end = 24.dp)) {
        Box(
            Modifier.fillMaxWidth().height(70.dp).background(selectedColor.value),
            alignment = Alignment.Center
        ) {
            Text(
                "#${Integer.toHexString(selectedColor.value.toArgb())}",
                color = selectedColor.value.foreground(),
                style = TextStyle(fontWeight = FontWeight.Bold),
                textDecoration = TextDecoration.Underline,
                fontSize = 18.sp
            )
        }
        SliderLayout(selectedColor)
    }
}

@Composable
private fun SliderLayout(selectedColor: MutableState<Color>) {
    LabelSlider(
        modifier = Modifier.padding(top = 16.dp),
        label = "A",
        value = selectedColor.value.alpha * 255,
        sliderColor = Color.DarkGray
    ) {
        selectedColor.value = selectedColor.value.copy(alpha = it / 255f)
    }

    LabelSlider(
        modifier = Modifier.padding(top = 16.dp),
        label = "R",
        value = selectedColor.value.red * 255,
        sliderColor = Color.Red
    ) {
        selectedColor.value = selectedColor.value.copy(red = it / 255f)
    }

    LabelSlider(
        modifier = Modifier.padding(top = 16.dp),
        label = "G",
        value = selectedColor.value.green * 255,
        sliderColor = Color.Green
    ) {
        selectedColor.value = selectedColor.value.copy(green = it / 255f)
    }

    LabelSlider(
        modifier = Modifier.padding(top = 16.dp),
        label = "B",
        value = selectedColor.value.blue * 255,
        sliderColor = Color.Blue
    ) {
        selectedColor.value = selectedColor.value.copy(blue = it / 255f)
    }
}

@Composable
private fun LabelSlider(
    modifier: Modifier = Modifier,
    label: String,
    value: Float,
    sliderColor: Color,
    onSliderChange: (Float) -> Unit
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            label,
            style = MaterialTheme.typography.h6,
            fontSize = 16.sp,
            color = MaterialTheme.colors.onBackground
        )
        Slider(
            value = value,
            onValueChange = onSliderChange,
            valueRange = 0f..255f,
            steps = 255,
            modifier = Modifier.padding(start = 16.dp, end = 24.dp),
            activeTrackColor = sliderColor,
            thumbColor = sliderColor
        )

        Box(Modifier.width(30.dp).align(Alignment.CenterVertically)) {
            Text(
                value.toInt().toString(),
                style = MaterialTheme.typography.h6,
                fontSize = 16.sp,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
private fun ColorGridLayout(
    colors: List<Color>,
    selectedColor: MutableState<Color>,
    subColors: List<List<Color>> = listOf(),
    waitForPositiveButton: Boolean = false,
    onColorSelected: (Color) -> Unit = {}
) {
    var mainSelectedIndex by remember { mutableStateOf(0) }
    var showSubColors by remember { mutableStateOf(false) }

    val itemSize = with(DensityAmbient.current) { itemSizeDp.toIntPx() }

    GridView(itemSize = itemSize) {
        if (!showSubColors) {
            colors.fastForEachIndexed { index, item ->
                ColorView(color = item, selected = index == mainSelectedIndex) {
                    if (mainSelectedIndex != index) {
                        mainSelectedIndex = index
                        if (!waitForPositiveButton && subColors.isNotEmpty()) {
                            selectedColor.value = item
                            onColorSelected(item)
                        }
                    }
                    if (subColors.isNotEmpty()) {
                        showSubColors = true
                    }
                }
            }
        } else {
            Box(
                Modifier.size(itemSizeDp).clip(CircleShape).clickable(
                    onClick = {
                        showSubColors = false
                    },
                    indication = null
                ),
                alignment = Alignment.Center
            ) {
                Image(
                    Icons.Default.ArrowBack,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(tickSize)
                )
            }

            subColors[mainSelectedIndex].fastForEachIndexed { _, item ->
                ColorView(color = item, selected = selectedColor.value == item) {
                    selectedColor.value = item
                    if (!waitForPositiveButton) {
                        onColorSelected(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorView(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.size(itemSizeDp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, MaterialTheme.colors.onBackground, CircleShape)
            .clickable(onClick = onClick, indication = null),
        alignment = Alignment.Center
    ) {
        if (selected) {
            Image(
                Icons.Default.Done,
                colorFilter = ColorFilter.tint(color.foreground()),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(tickSize)
            )
        }
    }
}

@Composable
private fun GridView(
    itemsInRow: Int = 4,
    itemSize: Int,
    content: @Composable () -> Unit
) {
    WithConstraints {
        ScrollableColumn(
            modifier = Modifier.preferredHeightIn(max = (maxHeight * 0.7f)),
            children = {
                Layout({
                    content()
                },
                    Modifier.padding(
                        top = 8.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    { measurables, constraints ->
                        val spacing =
                            (constraints.maxWidth - (itemSize * itemsInRow)) / (itemsInRow - 1)
                        val rows = (measurables.size / itemsInRow) + 1

                        val layoutHeight = (rows * itemSize) + ((rows - 1) * spacing)

                        layout(constraints.maxWidth, layoutHeight) {
                            measurables
                                .fastMap {
                                    it.measure(
                                        Constraints(
                                            maxHeight = itemSize,
                                            maxWidth = itemSize
                                        )
                                    )
                                }
                                .fastForEachIndexed { index, it ->
                                    it.place(
                                        x = (index % itemsInRow) * (itemSize + spacing),
                                        y = (index / itemsInRow) * (itemSize + spacing)
                                    )
                                }
                        }
                    })
            })
    }
}

private fun Color.foreground(): Color = if (this.luminance() > 0.5f) Color.Black else Color.White
