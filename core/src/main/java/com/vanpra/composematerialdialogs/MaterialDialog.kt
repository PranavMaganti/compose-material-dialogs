package com.vanpra.composematerialdialogs

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.graphics.imageFromResource
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.unit.dp
import androidx.ui.util.fastForEachIndexed

class MaterialDialog(
    private val showing: MutableState<Boolean>,
    private val autoDismiss: Boolean = true
) {
    private val title = mutableListOf<@Composable() () -> Unit>()
    private val body = mutableListOf<@Composable() () -> Unit>()
    private val buttons = mutableListOf<@Composable() () -> Unit>()

    val stackButtons = false

    @Composable
    fun draw(content: @Composable() MaterialDialog.() -> Unit) {
        if (showing.value) {
            ThemedDialog(onCloseRequest = { showing.value = false }) {
                content()
                if (title.isNotEmpty()) {
                    Row(
                        Modifier.fillMaxWidth().preferredHeight(64.dp).padding(start = 24.dp),
                        verticalGravity = Alignment.CenterVertically
                    ) {
                        for (item in title) {
                            item()
                            Spacer(Modifier.fillMaxHeight().width(14.dp))
                        }
                    }
                }

                if (body.isNotEmpty()) {
                    Column {
                        for (item in body) {
                            item()
                        }
                    }
                }

                if (buttons.isNotEmpty()) {
                    Row(
                        Modifier.fillMaxWidth().height(52.dp).padding(end = 8.dp),
                        verticalGravity = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        for (item in buttons) {
                            item()
                            Spacer(Modifier.fillMaxHeight().width(8.dp))
                        }
                    }
                }
            }
        }
    }

    /**
     * @brief Creates a title with the given text
     * @param text title text from a string
     * @param res title text from a resource
     */
    @Composable
    fun MaterialDialog.title(text: String? = null, @StringRes res: Int? = null) {
        val titleText = ContextAmbient.current.getString(res, text)

        title.add {
            Text(
                text = titleText,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6
            )
        }
    }

    @Composable
    fun MaterialDialog.icon(@DrawableRes res: Int) {
        val icon = imageFromResource(ContextAmbient.current.resources, res)

        title.add(0) {
            Image(asset = icon, modifier = Modifier.size(34.dp))
        }
    }

    @Composable
    fun MaterialDialog.positiveButton(
        text: String? = null,
        @StringRes res: Int? = null,
        onClick: () -> Unit = {}
    ) {
        val buttonText = ContextAmbient.current.getString(res, text)
        buttons.add {
            TextButton(onClick = {
                if (autoDismiss) {
                    showing.value = false
                }
                onClick()
            }) {
                Text(text = buttonText, style = MaterialTheme.typography.button)
            }
        }
    }

    @Composable
    fun MaterialDialog.negativeButton(
        text: String? = null,
        @StringRes res: Int? = null,
        onClick: () -> Unit = {}
    ) {
        val buttonText = ContextAmbient.current.getString(res, text)
        buttons.add(0) {
            TextButton(onClick = {
                if (autoDismiss) {
                    showing.value = false
                }
                onClick()
            }) {
                Text(text = buttonText, style = MaterialTheme.typography.button)
            }
        }
    }

    @Composable
    fun MaterialDialog.message(text: String? = null, @StringRes res: Int? = null) {
        val messageText = ContextAmbient.current.getString(res, text)

        body.add {
            Text(
                text = messageText,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 28.dp, start = 24.dp, end = 24.dp)
            )
        }
    }

    @Composable
    fun MaterialDialog.listItems(
        list: List<String>,
        onClick: (index: Int, item: String) -> Unit = { _, _ -> }
    ) {
        body.add {
            VerticalScroller {
                list.fastForEachIndexed { index, it ->
                    Text(
                        it,
                        color = MaterialTheme.colors.onSurface,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { onClick(index, it) })
                            .padding(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
                    )
                }

            }
        }
    }

    @Composable
    fun <T> MaterialDialog.listItems(
        list: List<T>,
        onClick: (index: Int, item: T) -> Unit = { _, _ -> },
        isEnabled: (index: Int) -> Boolean = { _ -> true },
        item: @Composable() (index: Int, T) -> Unit
    ) {
        body.add {
            VerticalScroller {
                list.fastForEachIndexed { index, it ->
                    Box(
                        Modifier.fillMaxWidth()
                            .clickable(onClick = { onClick(index, it) }, enabled = isEnabled(index))
                            .padding(start = 24.dp, end = 24.dp)
                    ) {
                        item(index, it)
                    }
                }
            }
        }
    }

    @Composable
    fun MaterialDialog.listItemsMultiChoice(
        list: List<String>,
        disabledIndices: List<Int> = listOf(),
        initialSelection: List<Int> = listOf(),
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
                onCheckedChange(selectedItems)
            }
        }
        val isEnabled = { index: Int -> index !in disabledIndices }

        listItems(list = list, onClick = { index, _ ->
            onChecked(index)
        }, isEnabled = isEnabled) { index, item ->
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

    @Composable
    fun MaterialDialog.listItemsSingleChoice(
        list: List<String>,
        disabledIndices: List<Int> = listOf(),
        initialSelection: Int = 0,
        onChoiceChange: (selected: Int) -> Unit = {}
    ) {
        var selected by state { initialSelection }
        val onSelect = { index: Int ->
            if (index !in disabledIndices) {
                selected = index
                onChoiceChange(selected)
            }
        }
        val isEnabled = { index: Int -> index !in disabledIndices }

        listItems(list = list, onClick = { index, _ ->
            onSelect(index)
        }, isEnabled = isEnabled) { index, item ->
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

    @Composable
    fun MaterialDialog.customView(children: @Composable() () -> Unit) {
        body.add {
            Box(modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 28.dp)) {
                children()
            }
        }
    }
}
