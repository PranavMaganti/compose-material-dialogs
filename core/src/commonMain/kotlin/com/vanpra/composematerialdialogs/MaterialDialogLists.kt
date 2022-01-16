package com.vanpra.composematerialdialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Adds a selectable list with custom items to the dialog
 *
 * @param list list of given generic type
 * @param onClick callback with the index and item when a list object is clicked
 * @param isEnabled a function to check if the item at a given index is enabled/clickable
 * @param item a composable function which takes an object of given generic type
 */
@Composable
fun <T> MaterialDialogScope.listItems(
    modifier: Modifier = Modifier,
    list: List<T>,
    closeOnClick: Boolean = true,
    onClick: (index: Int, item: T) -> Unit = { _, _ -> },
    isEnabled: (index: Int) -> Boolean = { _ -> true },
    item: @Composable (index: Int, T) -> Unit,
) {
    BoxWithConstraints {
        LazyColumn(
            Modifier
                .then(modifier)
                .testTag("dialog_list")
        ) {
            itemsIndexed(list) { index, it ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .testTag("dialog_list_item_$index")
                        .clickable(
                            onClick = {
                                if (closeOnClick) {
                                    dialogState.hide()
                                }
                                onClick(index, it)
                            },
                            enabled = isEnabled(index)
                        )
                ) {
                    item(index, it)
                }
            }
        }
    }
}

/**
 * Adds a selectable plain text list to the dialog
 *
 * @param list the strings to be displayed in the list
 * @param onClick callback with the index and string of an item when it is clicked
 */
@Composable
fun MaterialDialogScope.listItems(
    list: List<String>,
    closeOnClick: Boolean = true,
    onClick: (index: Int, item: String) -> Unit = { _, _ -> }
) {
    listItems(
        modifier = Modifier.padding(bottom = 8.dp),
        list = list,
        closeOnClick = closeOnClick,
        onClick = onClick
    ) { _, item ->
        Text(
            item,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 24.dp)
                .wrapContentWidth(Alignment.Start)
        )
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
fun MaterialDialogScope.listItemsMultiChoice(
    list: List<String>,
    disabledIndices: Set<Int> = setOf(),
    initialSelection: Set<Int> = setOf(),
    waitForPositiveButton: Boolean = true,
    onCheckedChange: (indices: Set<Int>) -> Unit = {}
) {
    var selectedItems by remember { mutableStateOf(initialSelection.toMutableSet()) }

    if (waitForPositiveButton) {
        DialogCallback { onCheckedChange(selectedItems) }
    }

    val onChecked = { index: Int ->
        if (index !in disabledIndices) {
            /* Have to create temp var as mutableState doesn't trigger on adding to set */
            val newSelectedItems = selectedItems.toMutableSet()
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

    val isEnabled = remember(disabledIndices) { { index: Int -> index !in disabledIndices } }

    listItems(
        list = list,
        onClick = { index, _ -> onChecked(index) },
        isEnabled = isEnabled,
        closeOnClick = false
    ) { index, item ->
        val enabled = remember(disabledIndices) { index !in disabledIndices }
        val selected = remember(selectedItems) { index in selectedItems }

        MultiChoiceItem(
            item = item,
            index = index,
            selected = selected,
            enabled = enabled,
            onChecked = onChecked
        )
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
fun MaterialDialogScope.listItemsSingleChoice(
    list: List<String>,
    disabledIndices: Set<Int> = setOf(),
    initialSelection: Int? = null,
    waitForPositiveButton: Boolean = true,
    onChoiceChange: (selected: Int) -> Unit = {}
) {
    var selectedItem by remember { mutableStateOf(initialSelection) }
    PositiveButtonEnabled(valid = selectedItem != null) {}

    if (waitForPositiveButton) {
        DialogCallback { onChoiceChange(selectedItem!!) }
    }

    val onSelect = { index: Int ->
        if (index !in disabledIndices) {
            selectedItem = index

            if (!waitForPositiveButton) {
                onChoiceChange(selectedItem!!)
            }
        }
    }

    val isEnabled = remember(disabledIndices) { { index: Int -> index !in disabledIndices } }
    listItems(
        list = list,
        closeOnClick = false,
        onClick = { index, _ -> onSelect(index) },
        isEnabled = isEnabled
    ) { index, item ->
        val enabled = remember(disabledIndices) { index !in disabledIndices }
        val selected = remember(selectedItem) { index == selectedItem }

        SingleChoiceItem(
            item = item,
            index = index,
            selected = selected,
            enabled = enabled,
            onSelect = onSelect
        )
    }
}

@Composable
private fun MultiChoiceItem(
    item: String,
    index: Int,
    selected: Boolean,
    enabled: Boolean,
    onChecked: (index: Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(start = 12.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = selected, onCheckedChange = { onChecked(index) }, enabled = enabled)
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(32.dp)
        )
        Text(
            item,
            color = if (enabled) {
                MaterialTheme.colors.onSurface
            } else {
                MaterialTheme.colors.onSurface.copy(ContentAlpha.disabled)
            },
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun SingleChoiceItem(
    item: String,
    index: Int,
    selected: Boolean,
    enabled: Boolean,
    onSelect: (index: Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(start = 12.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = {
                if (enabled) {
                    onSelect(index)
                }
            },
            enabled = enabled
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(32.dp)
        )
        Text(
            item,
            modifier = Modifier,
            color = if (enabled) {
                MaterialTheme.colors.onSurface
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
            },
            style = MaterialTheme.typography.body1
        )
    }
}
