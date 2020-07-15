package com.vanpra.composematerialdialogs

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.tag
import androidx.ui.foundation.*
import androidx.ui.graphics.Color
import androidx.ui.graphics.imageFromResource
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.unit.dp
import androidx.ui.util.fastForEachIndexed
import androidx.ui.util.fastMap

class MaterialDialogButtons(private val dialog: MaterialDialog) {
    val buttonsTagOrder = mutableListOf<Int>()

    @Composable
    fun MaterialDialogButtons.positiveButton(
        text: String? = null,
        @StringRes res: Int? = null,
        disableDismiss: Boolean = false,
        onClick: () -> Unit = {}
    ) {
        val buttonText = ContextAmbient.current.getString(res, text)

        TextButton(onClick = {
            if (dialog.isAutoDismiss() && !disableDismiss) {
                dialog.hide()
            }

            dialog.getCallbacks().forEach {
                it()
            }

            onClick()
        }, modifier = Modifier.tag("button_${buttonsTagOrder.size}")) {
            Text(text = buttonText, style = MaterialTheme.typography.button)
        }

        buttonsTagOrder.add(0, buttonsTagOrder.size)
    }

    @Composable
    fun MaterialDialogButtons.negativeButton(
        text: String? = null,
        @StringRes res: Int? = null,
        onClick: () -> Unit = {}
    ) {
        val buttonText = ContextAmbient.current.getString(res, text)
        TextButton(onClick = {
            if (dialog.isAutoDismiss()) {
                dialog.hide()
            }
            onClick()
        }, modifier = Modifier.tag("button_${buttonsTagOrder.size}")) {
            Text(text = buttonText, style = MaterialTheme.typography.button)
        }

        buttonsTagOrder.add(buttonsTagOrder.size)
    }
}

class MaterialDialog(private val autoDismiss: Boolean = true) {
    private val showing: MutableState<Boolean> = mutableStateOf(false)
    private val buttons = MaterialDialogButtons(this)
    private val callbacks = mutableListOf<() -> Unit>()

    fun addCallback(callback: () -> Unit) {
        callbacks.add(callback)
    }

    fun getCallbacks(): List<() -> Unit> {
        return callbacks
    }

    fun show() {
        showing.value = true
    }

    fun hide() {
        showing.value = false
    }

    fun isAutoDismiss() = autoDismiss

    @Composable
    fun build(content: @Composable() MaterialDialog.() -> Unit) {
        if (showing.value) {
            ThemedDialog(onCloseRequest = { hide() }) {
                Column(Modifier.fillMaxWidth().drawBackground(MaterialTheme.colors.background)) {
                    this@MaterialDialog.content()
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
    fun MaterialDialog.title(
        text: String? = null,
        @StringRes res: Int? = null,
        center: Boolean = false
    ) {
        val titleText = ContextAmbient.current.getString(res, text)
        var modifier = Modifier.fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp)
            .preferredHeight(64.dp)
            .wrapContentHeight(Alignment.CenterVertically)

        if (center) {
            modifier = modifier.plus(Modifier.wrapContentWidth(Alignment.CenterHorizontally))
        }

        Text(
            text = titleText,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.h6,
            modifier = modifier
        )
    }

    @Composable
    fun MaterialDialog.iconTitle(
        text: String? = null,
        @StringRes textRes: Int? = null,
        @DrawableRes iconRes: Int
    ) {
        val titleText = ContextAmbient.current.getString(textRes, text)
        val icon = imageFromResource(ContextAmbient.current.resources, iconRes)

        Row(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp).preferredHeight(64.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            Image(asset = icon, modifier = Modifier.size(34.dp))
            Spacer(Modifier.width(14.dp))
            Text(
                text = titleText,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6
            )
        }
    }

    @Composable
    fun MaterialDialog.message(text: String? = null, @StringRes res: Int? = null) {
        val messageText = ContextAmbient.current.getString(res, text)

        Text(
            text = messageText,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
        )
    }

    @Composable
    fun MaterialDialog.buttons(content: @Composable() MaterialDialogButtons.() -> Unit) {
        buttons.buttonsTagOrder.clear()

        val constraints = ConstraintSet2 {
            val buttonBox = createRefFor("buttons")

            constrain(buttonBox) {
                linkTo(parent.start, parent.end, 24.dp)
                bottom.linkTo(parent.bottom)

                width = Dimension.fillToConstraints
                height = Dimension.preferredValue(52.dp).atLeast(52.dp)
            }
        }

        val buttonConstraints = ConstraintSet2 {
            val buttonRefs = buttons.buttonsTagOrder.fastMap { createRefFor("button_$it") }

            buttonRefs.fastForEachIndexed { index, item ->
                constrain(item) {
                    centerVerticallyTo(parent)
                    if (index == 0) {
                        end.linkTo(parent.end, 8.dp)
                    } else {
                        end.linkTo(buttonRefs[index - 1].start, 8.dp)
                    }
                }
            }
        }

        ConstraintLayout(constraints, Modifier.fillMaxWidth()) {
            ConstraintLayout(buttonConstraints, modifier = Modifier.tag("buttons")) {
                content(buttons)
            }

        }
    }

    @Composable
    fun MaterialDialog.listItems(
        list: List<String>,
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
                        .clickable(onClick = { onClick(index, it) })
                        .padding(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
                )
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
                addCallback {
                    onCheckedChange(selectedItems)
                }
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
        waitForPositiveButton: Boolean = false,
        onChoiceChange: (selected: Int) -> Unit = {}
    ) {
        var selected by state { initialSelection }
        val onSelect = { index: Int ->
            if (index !in disabledIndices) {
                selected = index
                if (!waitForPositiveButton) {
                    onChoiceChange(selected)
                }
            }
        }

        remember {
            if (waitForPositiveButton) {
                addCallback {
                    onChoiceChange(selected)
                }
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
        Box(modifier = Modifier.padding(bottom = 28.dp)) {
            children()
        }
    }
}

