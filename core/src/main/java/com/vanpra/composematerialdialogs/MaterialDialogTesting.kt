import androidx.ui.core.Modifier
import androidx.ui.core.tag
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp

//package com.vanpra.composematerialdialogs
//
//import androidx.annotation.DrawableRes
//import androidx.annotation.StringRes
//import androidx.compose.*
//import androidx.ui.core.Alignment
//import androidx.ui.core.ContextAmbient
//import androidx.ui.core.Modifier
//import androidx.ui.core.tag
//import androidx.ui.foundation.*
//import androidx.ui.graphics.imageFromResource
//import androidx.ui.layout.*
//import androidx.ui.material.*
//import androidx.ui.unit.dp
//import androidx.ui.util.fastForEachIndexed
//
//class MaterialDialogTesting(private val autoDismiss: Boolean = true) {
//    private val showing: MutableState<Boolean> = mutableStateOf(false)
//
//    private val titleTagOrder = mutableListOf<Int>()
//    private val bodyTagOrder = mutableListOf<Int>()
//    private val buttonsTagOrder = mutableListOf<Int>()
//
//    fun show() {
//        showing.value = true
//    }
//
//    fun hide() {
//        showing.value = false
//    }
//
//    private fun clearTags() {
//        titleTagOrder.clear()
//        bodyTagOrder.clear()
//        buttonsTagOrder.clear()
//    }
//
//    @Composable
//    fun build(content: @Composable() MaterialDialogTesting.() -> Unit) {
//        clearTags()
//
//        if (showing.value) {
//            ThemedDialog(onCloseRequest = { showing.value = false }) {
//                val mainConstraints = ConstraintSet2 {
//                    val titles = titleTagOrder.map { createRefFor("title_$it") }
//                    val body = bodyTagOrder.map { createRefFor("body_$it") }
//                    val buttons = buttonsTagOrder.map { createRefFor("button_$it") }
//
//                    val titleBox = createRefFor("title_box")
//                    val bodyBox = createRefFor("body_box")
//                    val buttonBox = createRefFor("button_box")
//
//                    if (titleTagOrder.isNotEmpty()) {
//                        constrain(titleBox) {
//                            top.linkTo(parent.top)
//                            linkTo(parent.start, parent.end)
//                            width = Dimension.fillToConstraints
//                            height = Dimension.preferredValue(64.dp)
//                        }
//                    }
//
//                    if (bodyTagOrder.isNotEmpty()) {
//                        constrain(bodyBox) {
//                            top.linkTo(titleBox.bottom)
//                            linkTo(parent.start, parent.end)
//                            width = Dimension.fillToConstraints
//                        }
//                    }
//
//                    if (buttonsTagOrder.isNotEmpty()) {
//                        constrain(buttonBox) {
//                            top.linkTo(bodyBox.bottom)
//                            linkTo(parent.start, parent.end)
//                            width = Dimension.fillToConstraints
//                            height = Dimension.preferredValue(52.dp)
//                        }
//                    }
//
//                    titles.fastForEachIndexed { index, item ->
//                        constrain(item) {
//                            start.linkTo(titleBox.start)
//
//                            if (index == 0) {
//                                top.linkTo(titleBox.top, 12.dp)
//                            } else {
//                                top.linkTo(titles[index - 1].bottom, 16.dp)
//                            }
//
//                            if (index == titles.size - 1) {
//                                bottom.linkTo(titleBox.bottom, 12.dp)
//                            }
//                        }
//                    }
//                }
//
//                ConstraintLayout(
//                    mainConstraints,
//                    Modifier.fillMaxWidth().drawBackground(MaterialTheme.colors.surface)
//                ) {
//                    content()
//
//                    Box(Modifier.padding(top = 12.dp, bottom = 12.dp).tag("title_box"))
//                    Box(Modifier.tag("body_box"))
//                    Box(Modifier.tag("button_box"))
//                }
//            }
//        }
////        if (showing.value) {
////            ThemedDialog(onCloseRequest = { showing.value = false }) {
////                if (title.isNotEmpty()) {
////                    Column(
////                        Modifier.fillMaxWidth()
////                            .preferredHeightIn(64.dp)
////                            .padding(top = 16.dp, bottom = 12.dp),
////                        verticalArrangement = Arrangement.Center
////                    ) {
////                        title.fastForEachIndexed { index, item ->
////                            item()
////                            if (title.size != 0 && index != title.size - 1) {
////                                Spacer(modifier = Modifier.fillMaxWidth().height(16.dp))
////                            }
////                        }
////                    }
////                }
////
////                if (body.isNotEmpty()) {
////                    Column {
////                        for (item in body) {
////                            item()
////                        }
////                    }
////                }
////
////                if (buttons.isNotEmpty()) {
////                    Row(
////                        Modifier.fillMaxWidth().height(52.dp).padding(end = 8.dp),
////                        verticalGravity = Alignment.CenterVertically,
////                        horizontalArrangement = Arrangement.End
////                    ) {
////                        for (item in buttons) {
////                            item()
////                            Spacer(Modifier.fillMaxHeight().width(8.dp))
////                        }
////                    }
////                }
////            }
////        }
//    }
//
//    /**
//     * @brief Creates a title with the given text
//     * @param text title text from a string
//     * @param res title text from a resource
//     */
//    @Composable
//    fun MaterialDialogTesting.title(
//        text: String? = null,
//        @StringRes res: Int? = null,
//        center: Boolean = false
//    ) {
//        val titleText = ContextAmbient.current.getString(res, text)
//        var modifier = Modifier.fillMaxWidth()
//            .padding(start = 24.dp, end = 24.dp)
//            .tag("title_${titleTagOrder.size}")
//
//        titleTagOrder.add(titleTagOrder.size)
//
//        if (center) {
//            modifier = modifier.plus(Modifier.wrapContentWidth(Alignment.CenterHorizontally))
//        }
//
//        Text(
//            text = titleText,
//            color = MaterialTheme.colors.onSurface,
//            style = MaterialTheme.typography.h6,
//            modifier = modifier
//        )
//    }
//
//    @Composable
//    fun MaterialDialogTesting.iconTitle(
//        text: String? = null,
//        @StringRes textRes: Int? = null,
//        @DrawableRes iconRes: Int
//    ) {
//        val titleText = ContextAmbient.current.getString(textRes, text)
//        val icon = imageFromResource(ContextAmbient.current.resources, iconRes)
//
//        Row(
//            modifier = Modifier.padding(start = 24.dp, end = 24.dp)
//                .tag("title_${titleTagOrder.size}"),
//            verticalGravity = Alignment.CenterVertically
//        ) {
//            Image(asset = icon, modifier = Modifier.size(34.dp))
//            Spacer(Modifier.width(14.dp))
//            Text(
//                text = titleText,
//                color = MaterialTheme.colors.onSurface,
//                style = MaterialTheme.typography.h6
//            )
//        }
//
//        titleTagOrder.add(titleTagOrder.size)
//    }
//
//    @Composable
//    fun MaterialDialogTesting.positiveButton(
//        text: String? = null,
//        @StringRes res: Int? = null,
//        disableDismiss: Boolean = false,
//        onClick: () -> Unit = {}
//    ) {
//        val buttonText = ContextAmbient.current.getString(res, text)
//
//        TextButton(onClick = {
//            if (autoDismiss && !disableDismiss) {
//                showing.value = false
//            }
//            onClick()
//        }, modifier = Modifier.tag("button_${buttonsTagOrder.size}")) {
//            Text(text = buttonText, style = MaterialTheme.typography.button)
//        }
//
//        buttonsTagOrder.add(buttonsTagOrder.size)
//    }
//
//    @Composable
//    fun MaterialDialogTesting.negativeButton(
//        text: String? = null,
//        @StringRes res: Int? = null,
//        onClick: () -> Unit = {}
//    ) {
//        val buttonText = ContextAmbient.current.getString(res, text)
//        TextButton(onClick = {
//            if (autoDismiss) {
//                showing.value = false
//            }
//            onClick()
//        }, modifier = Modifier.tag("button_${buttonsTagOrder.size}")) {
//            Text(text = buttonText, style = MaterialTheme.typography.button)
//        }
//
//        buttonsTagOrder.add(0, buttonsTagOrder.size)
//    }
//
//    @Composable
//    fun MaterialDialogTesting.message(text: String? = null, @StringRes res: Int? = null) {
//        val messageText = ContextAmbient.current.getString(res, text)
//
//        Text(
//            text = messageText,
//            color = MaterialTheme.colors.onSurface,
//            style = MaterialTheme.typography.body1,
//            modifier = Modifier
//                .padding(bottom = 28.dp, start = 24.dp, end = 24.dp)
//                .tag("body_${bodyTagOrder.size}")
//        )
//
//        bodyTagOrder.add(bodyTagOrder.size)
//    }
//
//    @Composable
//    fun MaterialDialogTesting.listItems(
//        list: List<String>,
//        onClick: (index: Int, item: String) -> Unit = { _, _ -> }
//    ) {
//        VerticalScroller(modifier = Modifier.tag("body_${bodyTagOrder.size}")) {
//            list.fastForEachIndexed { index, it ->
//                Text(
//                    it,
//                    color = MaterialTheme.colors.onSurface,
//                    style = MaterialTheme.typography.body1,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable(onClick = { onClick(index, it) })
//                        .padding(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
//                )
//            }
//
//        }
//
//        bodyTagOrder.add(bodyTagOrder.size)
//    }
//
//    @Composable
//    fun <T> MaterialDialogTesting.listItems(
//        list: List<T>,
//        onClick: (index: Int, item: T) -> Unit = { _, _ -> },
//        isEnabled: (index: Int) -> Boolean = { _ -> true },
//        item: @Composable() (index: Int, T) -> Unit
//    ) {
//        VerticalScroller(modifier = Modifier.tag("body_${bodyTagOrder.size}")) {
//            list.fastForEachIndexed { index, it ->
//                Box(
//                    Modifier.fillMaxWidth()
//                        .clickable(onClick = { onClick(index, it) }, enabled = isEnabled(index))
//                        .padding(start = 24.dp, end = 24.dp)
//                ) {
//                    item(index, it)
//                }
//            }
//        }
//        bodyTagOrder.add(bodyTagOrder.size)
//    }
//
//    @Composable
//    fun MaterialDialogTesting.listItemsMultiChoice(
//        list: List<String>,
//        disabledIndices: List<Int> = listOf(),
//        initialSelection: List<Int> = listOf(),
//        onCheckedChange: (indices: List<Int>) -> Unit = {}
//    ) {
//        var selectedItems by mutableStateOf(initialSelection.toMutableList(), StructurallyEqual)
//        val onChecked = { index: Int ->
//            if (index !in disabledIndices) {
//                val newSelectedItems = selectedItems.toMutableList()
//                if (index in selectedItems) {
//                    newSelectedItems.remove(index)
//                } else {
//                    newSelectedItems.add(index)
//                }
//                selectedItems = newSelectedItems
//                onCheckedChange(selectedItems)
//            }
//        }
//        val isEnabled = { index: Int -> index !in disabledIndices }
//
//        listItems(list = list, onClick = { index, _ ->
//            onChecked(index)
//        }, isEnabled = isEnabled) { index, item ->
//            val enabled = remember(disabledIndices) { index !in disabledIndices }
//            val selected = index in selectedItems
//
//            Row(
//                Modifier.fillMaxWidth().preferredHeight(48.dp),
//                verticalGravity = Alignment.CenterVertically
//            ) {
//                Checkbox(
//                    checked = selected,
//                    onCheckedChange = { onChecked(index) },
//                    enabled = enabled
//                )
//                Spacer(modifier = Modifier.fillMaxHeight().width(32.dp))
//                Text(
//                    item,
//                    color = if (enabled) {
//                        MaterialTheme.colors.onSurface
//                    } else {
//                        EmphasisAmbient.current.disabled.applyEmphasis(
//                            MaterialTheme.colors.onSurface
//                        )
//                    },
//                    style = MaterialTheme.typography.body1
//                )
//            }
//        }
//    }
//
//    @Composable
//    fun MaterialDialogTesting.listItemsSingleChoice(
//        list: List<String>,
//        disabledIndices: List<Int> = listOf(),
//        initialSelection: Int = 0,
//        onChoiceChange: (selected: Int) -> Unit = {}
//    ) {
//        var selected by state { initialSelection }
//        val onSelect = { index: Int ->
//            if (index !in disabledIndices) {
//                selected = index
//                onChoiceChange(selected)
//            }
//        }
//        val isEnabled = { index: Int -> index !in disabledIndices }
//
//        listItems(list = list, onClick = { index, _ ->
//            onSelect(index)
//        }, isEnabled = isEnabled) { index, item ->
//            val enabled = remember(disabledIndices) { index !in disabledIndices }
//
//            Row(
//                Modifier.fillMaxWidth().preferredHeight(48.dp),
//                verticalGravity = Alignment.CenterVertically
//            ) {
//                RadioButton(
//                    selected = selected == index,
//                    onSelect = {
//                        if (isEnabled(index)) {
//                            onSelect(index)
//                        }
//                    },
//                    color = if (isEnabled(index)) {
//                        MaterialTheme.colors.secondary
//                    } else {
//                        EmphasisAmbient.current.disabled.applyEmphasis(
//                            MaterialTheme.colors.onSurface
//                        )
//                    }
//                )
//                Spacer(modifier = Modifier.fillMaxHeight().width(32.dp))
//                Text(
//                    item,
//                    color = if (enabled) {
//                        MaterialTheme.colors.onSurface
//                    } else {
//                        EmphasisAmbient.current.disabled.applyEmphasis(
//                            MaterialTheme.colors.onSurface
//                        )
//                    },
//                    style = MaterialTheme.typography.body1
//                )
//            }
//        }
//    }
//
//    @Composable
//    fun MaterialDialogTesting.customView(children: @Composable() () -> Unit) {
//        Box(modifier = Modifier.padding(bottom = 28.dp).tag("body_${bodyTagOrder.size}")) {
//            children()
//        }
//
//        bodyTagOrder.add(bodyTagOrder.size)
//    }
//}

//if (showing.value) {
//    ThemedDialog(onCloseRequest = { showing.value = false }) {
//        val constraints = ConstraintSet2 {
//            val main = createRefFor("main")
//            val text1 = createRefFor("text1")
//            val text2 = createRefFor("text2")
//
//            constrain(text1) {
//                top.linkTo(parent.top, 12.dp)
//                start.linkTo(parent.start, 24.dp)
//            }
//
//            constrain(text2) {
//                top.linkTo(text1.bottom, 8.dp)
//                bottom.linkTo(parent.bottom, 12.dp)
//
//                start.linkTo(parent.start, 24.dp)
//            }
//
//            constrain(main) {
//                width = Dimension.fillToConstraints
//                height = Dimension.preferredWrapContent.atLeast(64.dp)
//
//                top.linkTo(parent.top)
//                linkTo(parent.start, parent.end)
//            }
//        }
//
//        ConstraintLayout(
//            constraints,
//            Modifier.fillMaxWidth().drawBackground(MaterialTheme.colors.background)
//        ) {
//            ConstraintLayout(constraints, Modifier.tag("main")) {
//                Text(
//                    "Hello",
//                    modifier = Modifier.tag("text1"),
//                    color = MaterialTheme.colors.onSurface,
//                    style = MaterialTheme.typography.h6
//                )
//
//                Text(
//                    "Hello",
//                    modifier = Modifier.tag("text2"),
//                    color = MaterialTheme.colors.onSurface,
//                    style = MaterialTheme.typography.h6
//                )
//            }
//        }
//    }
//}
