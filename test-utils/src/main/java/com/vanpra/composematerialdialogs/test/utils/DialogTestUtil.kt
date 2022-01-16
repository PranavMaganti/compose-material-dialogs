package com.vanpra.composematerialdialogs.test.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.vanpra.composematerialdialogs.*

@Composable
fun DialogWithContent(
    autoDismiss: Boolean = true,
    dialogState: MaterialDialogState = rememberMaterialDialogState(true),
    buttons: @Composable MaterialDialogButtons.() -> Unit = {},
    content: @Composable MaterialDialogScope.() -> Unit = {}
) {
    MaterialDialog(dialogState = dialogState, buttons = buttons, autoDismiss = autoDismiss) {
        content()
    }
    SideEffect { dialogState.show() }
}

@Composable
fun MaterialDialogButtons.defaultButtons() {
    negativeButton("Cancel")
    positiveButton("Ok")
}

fun <T> Collection<T>.powerSet(): Set<Set<T>> = when {
    isEmpty() -> setOf(setOf())
    else -> drop(1).powerSet().let { it + it.map { rest -> rest + first() } }
}
