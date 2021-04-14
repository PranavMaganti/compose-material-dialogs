package com.vanpra.composematerialdialogs.test.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.swipeUp
import com.vanpra.composematerialdialogs.MaterialDialog
import java.util.Locale

private const val dialogTag = "dialog"

@Composable
fun DialogWithContent(
    autoDismiss: Boolean = true,
    dialog: MaterialDialog = MaterialDialog(autoDismiss),
    content: @Composable MaterialDialog.() -> Unit
) {
    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            dialog.build { content() }
            SideEffect { dialog.show() }
        }
    }
}

enum class ColorPickerSlider {
    Alpha,
    Red,
    Blue,
    Green
}

fun ComposeTestRule.onPositiveButton() =
    this.onNodeWithTag("positive")

fun ComposeTestRule.onNegativeButton() =
    this.onNodeWithTag("negative")

fun ComposeTestRule.onDialog() =
    this.onNodeWithTag(dialogTag)

fun ComposeTestRule.assertDialogExists() =
    this.onDialog().assertExists()

fun ComposeTestRule.assertDialogDoesNotExist() =
    this.onDialog().assertDoesNotExist()

fun ComposeTestRule.onDialogList() =
    this.onNodeWithTag("dialog_list")

fun ComposeTestRule.onDialogListItem(index: Int): SemanticsNodeInteraction {
    try {
        onNodeWithTag("dialog_list_item_$index").assertExists()
    } catch (e: AssertionError) {
        onDialogList().performGesture { swipeUp() }
        waitForIdle()
    }

    return onNodeWithTag("dialog_list_item_$index").assertExists()
}

fun ComposeTestRule.onDialogInput() =
    this.onNodeWithTag("dialog_input")

fun ComposeTestRule.onDialogInputError() =
    this.onNodeWithTag("dialog_input_error")

fun ComposeTestRule.onDialogColorPicker() =
    this.onNodeWithTag("dialog_color_picker")

fun ComposeTestRule.onDialogColorSelector(index: Int) =
    this.onNodeWithTag("dialog_color_selector_$index")

fun ComposeTestRule.onDialogSubColorSelector(index: Int) =
    this.onNodeWithTag("dialog_sub_color_selector_$index")

fun ComposeTestRule.onDialogSubColorBackButton() =
    this.onNodeWithTag("dialog_sub_color_back_btn")

fun ComposeTestRule.onDialogColorSlider(slider: ColorPickerSlider) =
    this.onNodeWithTag("dialog_color_picker_${slider.toString().toLowerCase(Locale.ROOT)}_slider")

fun <T> Collection<T>.powerSet(): Set<Set<T>> = when {
    isEmpty() -> setOf(setOf())
    else -> drop(1).powerSet().let { it + it.map { rest -> rest + first() } }
}
