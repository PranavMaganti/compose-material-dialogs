package com.vanpra.composematerialdialogs.test.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
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

internal fun ComposeTestRule.onListItem(index: Int) =
    this.onAllNodesWithTag("list_$index").onFirst()

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
