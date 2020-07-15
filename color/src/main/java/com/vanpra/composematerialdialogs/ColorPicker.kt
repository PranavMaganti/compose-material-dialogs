package com.vanpra.composematerialdialogs

import androidx.compose.*
import androidx.ui.core.*
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.*
import androidx.ui.layout.*
import androidx.ui.material.FilledTextField
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.material.icons.filled.Done
import androidx.ui.unit.dp
import androidx.ui.util.fastForEachIndexed
import androidx.ui.util.fastMap
//import com.vanpra.viewpager.ViewPager

val itemSizeDp = 55.dp
val tickSize = 35.dp

@Composable
fun MaterialDialog.colorChooser(
    colors: List<Color>,
    subColors: List<List<Color>> = listOf(),
    allowCustomArgb: Boolean = false,
    waitForPositiveButton: Boolean = false,
    onColorSelected: (Color) -> Unit = {}
) {
    val selectedColor = state { colors[0] }

    remember {
        if (waitForPositiveButton) {
            addCallback {
                onColorSelected(selectedColor.value)
            }
        }
    }

    customView {
//        ViewPager(range = IntRange(0, 1)) {
//            if (index == 0) {
                ColorGridLayout(
                    colors = colors,
                    selectedColor = selectedColor,
                    subColors = subColors,
                    waitForPositiveButton = waitForPositiveButton,
                    onColorSelected = onColorSelected
                )
//            } else {
//                CustomARGB(selectedColor)
//            }
//        }
    }
}

@Composable
fun CustomARGB(selectedColor: MutableState<Color>) {
    Column {
        Box(
            Modifier.padding(8.dp).fillMaxWidth().height(30.dp),
            backgroundColor = selectedColor.value,
            gravity = ContentGravity.Center
        ) {
            Text(selectedColor.value.toArgb().toString(), color = selectedColor.value.foreground())
        }
    }
}

@Composable
fun ColorGridLayout(
    colors: List<Color>,
    selectedColor: MutableState<Color>,
    subColors: List<List<Color>> = listOf(),
    waitForPositiveButton: Boolean = false,
    onColorSelected: (Color) -> Unit = {}
) {
    var mainSelectedIndex by state { 0 }
    var showSubColors by state { false }

    val itemSize = with(DensityAmbient.current) { itemSizeDp.toIntPx() }

    GridView(itemsInRow = 4, itemSize = itemSize) {
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
                    showSubColors = true
                }
            }
        } else {
            Box(
                Modifier.size(itemSizeDp).clickable(onClick = {
                    showSubColors = false
                }, indication = null),
                shape = CircleShape,
                gravity = ContentGravity.Center
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
fun ColorView(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.size(itemSizeDp).clip(CircleShape).clickable(onClick = onClick, indication = null),
        shape = CircleShape,
        border = Border(1.dp, SolidColor(MaterialTheme.colors.onBackground)),
        backgroundColor = color,
        gravity = ContentGravity.Center
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
fun GridView(
    itemsInRow: Int,
    itemSize: Int,
    content: @Composable() () -> Unit
) {
    WithConstraints {
        VerticalScroller(modifier = Modifier.preferredHeightIn(maxHeight = (maxHeight * 0.7f))) {
            Layout(
                children = {
                    content()
                },
                modifier = Modifier.padding(
                    top = 8.dp,
                    start = 24.dp,
                    end = 24.dp
                )
                    .fillMaxWidth()
                    .gravity(Alignment.CenterHorizontally)
            ) { measurables, constraints, _ ->
                val spacing = (constraints.maxWidth - (itemSize * itemsInRow)) / (itemsInRow - 1)
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
            }
        }
    }
}

fun Color.foreground(): Color = if (this.luminance() > 0.5f) Color.Black else Color.White

