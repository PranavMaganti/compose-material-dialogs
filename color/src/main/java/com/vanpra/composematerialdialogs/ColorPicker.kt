package com.vanpra.composematerialdialogs

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.*
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.SolidColor
import androidx.ui.graphics.luminance
import androidx.ui.layout.*
import androidx.ui.layout.ColumnScope.gravity
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Done
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.util.fastForEachIndexed
import androidx.ui.util.fastMap

@Composable
fun MaterialDialog.colorpicker(colors: List<Color>,
                               subColors: List<List<Color>> = listOf()) {
    customView {
        ColorPicker(colors, subColors)
    }
}


@Composable
fun ColorPicker(colors: List<Color>, subColors: List<List<Color>> = listOf()) {
    val itemSizeDp = 55.dp
    val itemSize = with(DensityAmbient.current) { itemSizeDp.toIntPx() }
    val selected = state { 0 }
    val tickSize = 35.dp

    GridView(itemsInRow = 4, itemSize = itemSize, maxItemsInColumn = 5) {
        colors.fastForEachIndexed { index, item ->
            val tickColor = if (item.luminance() > 0.6f) {
                Color.Black
            } else {
                Color.White
            }

            Box(
                Modifier.size(itemSizeDp).clip(CircleShape).clickable(onClick = {
                    selected.value = index
                }, indication = null),
                shape = CircleShape,
                border = Border(1.dp, SolidColor(MaterialTheme.colors.onBackground)),
                backgroundColor = item,
                gravity = ContentGravity.Center
            ) {
                if (index == selected.value) {
                    Image(
                        Icons.Default.Done,
                        colorFilter = ColorFilter.tint(tickColor),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(tickSize)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorPickerPreview() {
    val colors = listOf(Color.White, Color.Red, Color.Green, Color.Blue)

    ColorPicker(colors = colors)
}

@Composable
fun GridView(
    itemsInRow: Int,
    itemSize: Int,
    maxItemsInColumn: Int,
    content: @Composable() () -> Unit
) {
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
        val layoutHeight = (maxItemsInColumn * itemSize) + ((maxItemsInColumn - 1) * spacing)

        layout(constraints.maxWidth, layoutHeight) {
            measurables
                .fastMap { it.measure(Constraints(maxHeight = itemSize, maxWidth = itemSize)) }
                .fastForEachIndexed { index, it ->
                    if (index / itemsInRow < maxItemsInColumn) {
                        it.place(
                            x = (index % itemsInRow) * (itemSize + spacing),
                            y = (index / itemsInRow) * (itemSize + spacing)
                        )
                    }
                }
        }
    }
}