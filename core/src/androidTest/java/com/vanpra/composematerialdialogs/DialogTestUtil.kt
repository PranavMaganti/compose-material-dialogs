package com.vanpra.composematerialdialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag

private const val dialogTag = "dialog"

@Composable
internal fun DialogWithContent(
    autoDismiss: Boolean = true,
    content: @Composable MaterialDialog.() -> Unit
) {
    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            val dialog = MaterialDialog(autoDismiss = autoDismiss)
            dialog.build { content() }
            SideEffect { dialog.show() }
        }
    }
}

internal fun ComposeTestRule.onPositiveButton() =
    this.onNodeWithTag(MaterialDialogButtonTypes.Positive.toString())

internal fun ComposeTestRule.onNegativeButton() =
    this.onNodeWithTag(MaterialDialogButtonTypes.Positive.toString())

internal fun ComposeTestRule.assertDialogExists() =
    this.onNodeWithTag(dialogTag).assertExists()

internal fun ComposeTestRule.assertDialogDoesNotExist() =
    this.onNodeWithTag(dialogTag).assertDoesNotExist()
