package com.vanpra.composematerialdialogs.test.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.swipeUp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogButtonTypes

private const val dialogTag = "dialog"

@Composable
internal fun DialogWithContent(
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

@ExperimentalTestApi
internal fun ComposeTestRule.onDialogListItem(index: Int): SemanticsNodeInteraction {
    try {
        onNodeWithTag("dialog_list_item_$index").assertExists()
    } catch (e: AssertionError) {
        onDialogList().performGesture { swipeUp() }
        waitForIdle()
    }

    return onNodeWithTag("dialog_list_item_$index").assertExists()
}

internal fun ComposeTestRule.onDialogList() =
    this.onNodeWithTag("dialog_list")

internal fun ComposeTestRule.onPositiveButton() =
    this.onNodeWithTag(MaterialDialogButtonTypes.Positive.toString())

internal fun ComposeTestRule.onNegativeButton() =
    this.onNodeWithTag(MaterialDialogButtonTypes.Positive.toString())

internal fun ComposeTestRule.onDialog() =
    this.onNodeWithTag(dialogTag)

internal fun ComposeTestRule.assertDialogExists() =
    this.onDialog().assertExists()

internal fun ComposeTestRule.assertDialogDoesNotExist() =
    this.onDialog().assertDoesNotExist()

internal fun <T> Collection<T>.powerSet(): Set<Set<T>> = when {
    isEmpty() -> setOf(setOf())
    else -> drop(1).powerSet().let { it + it.map { rest -> rest + first() } }
}
