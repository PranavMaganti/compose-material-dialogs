package com.vanpra.composematerialdialogs

import androidx.compose.*
import androidx.ui.core.*
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.SolidColor
import androidx.ui.graphics.luminance
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeightIn
import androidx.ui.layout.size
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.material.icons.filled.Done
import androidx.ui.unit.dp
import androidx.ui.util.fastForEachIndexed
import androidx.ui.util.fastMap

val itemSizeDp = 55.dp
val tickSize = 35.dp

@Composable
fun MaterialDialog.colorChooser(
    colors: List<Color>,
    subColors: List<List<Color>> = listOf(),
    waitForPositiveButton: Boolean = false,
    onColorSelected: (Color) -> Unit = {}
) {
    customView {
        var mainSelectedIndex by state { 0 }
        var selectedColor by state { colors[0] }
        var showSubColors by state { false }

        val itemSize = with(DensityAmbient.current) { itemSizeDp.toIntPx() }

        remember {
            if (waitForPositiveButton) {
                addCallback {
                    onColorSelected(selectedColor)
                }
            }
        }

        GridView(itemsInRow = 4, itemSize = itemSize) {
            if (!showSubColors) {
                colors.fastForEachIndexed { index, item ->
                    ColorView(color = item, selected = index == mainSelectedIndex) {
                        if (mainSelectedIndex != index) {
                            mainSelectedIndex = index
                            if (!waitForPositiveButton && subColors.isNotEmpty()) {
                                selectedColor = item
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
                    ColorView(color = item, selected = selectedColor == item) {
                        selectedColor = item
                        if (!waitForPositiveButton) {
                            selectedColor = item
                            onColorSelected(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorView(color: Color, selected: Boolean, onClick: () -> Unit) {
    val tickColor = if (color.luminance() > 0.6f) {
        Color.Black
    } else {
        Color.White
    }

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
                colorFilter = ColorFilter.tint(tickColor),
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