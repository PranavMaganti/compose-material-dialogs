package com.vanpra.composematerialdialogs

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed

private const val listRatio = 0.6f
val bottomPadding = Modifier.padding(bottom = 8.dp)

/**
 * Adds a selectable plain text list to the dialog
 *
 * @param list the strings to be displayed in the list
 * @param onClick callback with the index and string of an item when it is clicked
 */
@Composable
fun MaterialDialog.listItems(
    list: List<String>,
    closeOnClick: Boolean = true,
    onClick: (index: Int, item: String) -> Unit = { _, _ -> }
) {
    WithConstraints {
        var modifier = Modifier.heightIn(max = maxHeight * listRatio)
        if (buttons.buttonsTagOrder.isEmpty()) {
            modifier = modifier.then(bottomPadding)
        }

        ScrollableColumn(modifier = modifier, children = {
            list.fastForEachIndexed { index, it ->
                Text(
                    it,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                if (closeOnClick) {
                                    hide()
                                }
                                onClick(index, it)
                            }
                        )
                        .padding(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
                        .wrapContentWidth(Alignment.Start)
                )
            }
        })
    }
}

/**
 * Adds a selectable list with custom items to the dialog
 *
 * @param list list of given generic type
 * @param onClick callback with the index and item when a list object is clicked
 * @param isEnabled a function to check if the item at a given index is enabled/clickable
 * @param item a composable function which takes an object of given generic type
 */
@Composable
fun <T> MaterialDialog.listItems(
    list: List<T>,
    closeOnClick: Boolean = true,
    onClick: (index: Int, item: T) -> Unit = { _, _ -> },
    isEnabled: (index: Int) -> Boolean = { _ -> true },
    item: @Composable (index: Int, T) -> Unit
) {

    WithConstraints {
        var modifier = Modifier.heightIn(max = maxHeight * listRatio)
        if (buttons.buttonsTagOrder.isEmpty()) {
            modifier = modifier.then(bottomPadding)
        }
        ScrollableColumn(modifier = modifier, children = {
            list.fastForEachIndexed { index, it ->
                Box(
                    Modifier.fillMaxWidth()
                        .clickable(
                            onClick = {
                                if (closeOnClick) {
                                    hide()
                                }
                                onClick(index, it)
                            },
                            enabled = isEnabled(index)
                        )
                        .padding(start = 24.dp, end = 24.dp)
                ) {
                    item(index, it)
                }
            }
        })
    }
}

/**
 * Adds a multi-choice list view to the dialog
 * @param list a list of string labels for the multi-choice items
 * @param disabledIndices a list of indices which should be disabled/unselectable
 * @param initialSelection a list of indices which should be selected initially
 * @param waitForPositiveButton if true the [onCheckedChange] callback will only be called when the
 * positive button is pressed, otherwise it will be called when the a new item is selected
 * @param onCheckedChange a function which is called with a list of selected indices. The timing of
 * this call is dictated by [waitForPositiveButton]
 */
@Composable
fun MaterialDialog.listItemsMultiChoice(
    list: List<String>,
    disabledIndices: List<Int> = listOf(),
    initialSelection: List<Int> = listOf(),
    waitForPositiveButton: Boolean = false,
    onCheckedChange: (indices: List<Int>) -> Unit = {}
) {
    var selectedItems by remember { mutableStateOf(initialSelection.toMutableList()) }
    val onChecked = { index: Int ->
        if (index !in disabledIndices) {
            val newSelectedItems = selectedItems.toMutableList()
            if (index in selectedItems) {
                newSelectedItems.remove(index)
            } else {
                newSelectedItems.add(index)
            }
            selectedItems = newSelectedItems
            if (!waitForPositiveButton) {
                onCheckedChange(selectedItems)
            }
        }
    }
    remember {
        if (waitForPositiveButton) {
            callbacks.add {
                onCheckedChange(selectedItems)
            }
        }
    }

    val isEnabled = { index: Int -> index !in disabledIndices }

    listItems(
        list = list,
        onClick = { index, _ -> onChecked(index) },
        isEnabled = isEnabled,
        closeOnClick = false
    ) { index, item ->
        val enabled = remember(disabledIndices) { index !in disabledIndices }
        val selected = index in selectedItems

        Row(
            Modifier.fillMaxWidth().preferredHeight(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = selected, onCheckedChange = { onChecked(index) }, enabled = enabled)
            Spacer(modifier = Modifier.fillMaxHeight().width(32.dp))
            Text(
                item,
                color = if (enabled) {
                    MaterialTheme.colors.onSurface
                } else {
                    AmbientEmphasisLevels.current.disabled.applyEmphasis(
                        MaterialTheme.colors.onSurface
                    )
                },
                style = MaterialTheme.typography.body1
            )
        }
    }
}

/**
 * Adds a single-choice list view to the dialog
 * @param list a list of string labels for the single-choice items
 * @param disabledIndices a list of indices which should be disabled/unselectable
 * @param initialSelection the index of the item that should initially be selected
 * @param waitForPositiveButton if true the [onChoiceChange] callback will only be called when the
 * positive button is pressed, otherwise it will be called when the a new item is selected
 * @param onChoiceChange a function which is called with the index of the selected item.
 * The timing of this call is dictated by [waitForPositiveButton]
 */
@Composable
fun MaterialDialog.listItemsSingleChoice(
    list: List<String>,
    disabledIndices: List<Int> = listOf(),
    initialSelection: Int? = null,
    waitForPositiveButton: Boolean = false,
    onChoiceChange: (selected: Int) -> Unit = {}
) {
    val disableIndex = remember { positiveEnabled.size }
    remember {
        positiveEnabled.add(disableIndex, initialSelection != null)
    }

    var selected by remember { mutableStateOf(initialSelection) }
    val onSelect = { index: Int ->
        if (index !in disabledIndices) {
            if (!positiveEnabled[disableIndex]) {
                val tempList = positiveEnabled.toMutableList()
                tempList[disableIndex] = true
                positiveEnabled = tempList
            }

            selected = index
            if (!waitForPositiveButton) {
                onChoiceChange(selected!!)
            }
        }
    }

    remember {
        if (waitForPositiveButton) {
            callbacks.add { onChoiceChange(selected!!) }
        }
    }

    val isEnabled = { index: Int -> index !in disabledIndices }
    listItems(
        list = list,
        closeOnClick = false,
        onClick = { index, _ ->
            onSelect(index)
        },
        isEnabled = isEnabled
    ) { index, item ->
        SingleChoiceItem(
            item = item,
            index = index,
            disabledIndices = disabledIndices,
            selected = selected,
            isEnabled = isEnabled,
            onSelect = onSelect
        )
    }
}

@Composable
private fun SingleChoiceItem(
    item: String,
    index: Int,
    disabledIndices: List<Int>,
    selected: Int?,
    isEnabled: (index: Int) -> Boolean,
    onSelect: (index: Int) -> Unit
) {
    val enabled = remember(disabledIndices) { index !in disabledIndices }

    Row(
        Modifier.fillMaxWidth().preferredHeight(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected == index,
            onClick = {
                if (isEnabled(index)) {
                    onSelect(index)
                }
            },
            enabled = isEnabled(index)
        )
        Spacer(modifier = Modifier.fillMaxHeight().width(32.dp))
        Text(
            item,
            color = if (enabled) {
                MaterialTheme.colors.onSurface
            } else {
                AmbientEmphasisLevels.current.disabled.applyEmphasis(
                    MaterialTheme.colors.onSurface
                )
            },
            style = MaterialTheme.typography.body1
        )
    }
}
