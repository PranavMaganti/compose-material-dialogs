package com.vanpra.composematerialdialogs

import androidx.compose.Composable
import androidx.compose.StructurallyEqual
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.clickable
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.width
import androidx.ui.material.Checkbox
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.RadioButton
import androidx.ui.unit.dp
import androidx.ui.util.fastForEachIndexed

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
    VerticalScroller {
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
            )
        }
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
    item: @Composable() (index: Int, T) -> Unit
) {

    VerticalScroller(modifier = Modifier.padding(bottom = 8.dp)) {
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
    var selectedItems by mutableStateOf(initialSelection.toMutableList(), StructurallyEqual)
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
        onClick = { index, _ ->
            onChecked(index)
        },
        isEnabled = isEnabled
    ) { index, item ->
        val enabled = remember(disabledIndices) { index !in disabledIndices }
        val selected = index in selectedItems

        Row(
            Modifier.fillMaxWidth().preferredHeight(48.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = selected,
                onCheckedChange = { onChecked(index) },
                enabled = enabled
            )
            Spacer(modifier = Modifier.fillMaxHeight().width(32.dp))
            Text(
                item,
                color = if (enabled) {
                    MaterialTheme.colors.onSurface
                } else {
                    EmphasisAmbient.current.disabled.applyEmphasis(
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
    val disableIndex by state { positiveEnabled.value.size }
    remember {
        positiveEnabled.value.add(disableIndex, initialSelection != null)
    }

    var selected by state { initialSelection }
    val onSelect = { index: Int ->
        if (index !in disabledIndices) {
            if (!positiveEnabled.value[disableIndex]) {
                val tempList = positiveEnabled.value.toMutableList()
                tempList[disableIndex] = true
                positiveEnabled.value = tempList
            }

            selected = index
            if (!waitForPositiveButton) {
                onChoiceChange(selected!!)
            }
        }
    }

    remember {
        if (waitForPositiveButton) {
            callbacks.add {
                onChoiceChange(selected!!)
            }
        }
    }

    val isEnabled = { index: Int -> index !in disabledIndices }

    listItems(
        list = list,
        onClick = { index, _ ->
            onSelect(index)
        },
        isEnabled = isEnabled
    ) { index, item ->
        val enabled = remember(disabledIndices) { index !in disabledIndices }

        Row(
            Modifier.fillMaxWidth().preferredHeight(48.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected == index,
                onSelect = {
                    if (isEnabled(index)) {
                        onSelect(index)
                    }
                },
                color = if (isEnabled(index)) {
                    MaterialTheme.colors.secondary
                } else {
                    EmphasisAmbient.current.disabled.applyEmphasis(
                        MaterialTheme.colors.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.fillMaxHeight().width(32.dp))
            Text(
                item,
                color = if (enabled) {
                    MaterialTheme.colors.onSurface
                } else {
                    EmphasisAmbient.current.disabled.applyEmphasis(
                        MaterialTheme.colors.onSurface
                    )
                },
                style = MaterialTheme.typography.body1
            )
        }
    }
}
