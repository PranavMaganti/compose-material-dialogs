package com.vanpra.composematerialdialogsdemos

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TextButton
import androidx.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogsdemos.ui.dialogBackground

/**
 * @brief Builds a dialog and adds button to the layout which shows the dialog on click
 */
@Composable
fun DialogAndShowButton(buttonText: String, content: @Composable() MaterialDialog.() -> Unit) {
    val dialog = MaterialDialog()

    dialog.build(dialogBackground) {
        content()
    }

    TextButton(
        onClick = { dialog.show() },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) {
        Text(
            buttonText,
            modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center),
            color = MaterialTheme.colors.onPrimary
        )
    }
}

/**
 * @brief Add title to top of layout
 */
@Composable
fun DialogSection(title: String, content: @Composable() () -> Unit) {
    Text(
        title,
        color = MaterialTheme.colors.onSurface,
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
    )

    content()
}
