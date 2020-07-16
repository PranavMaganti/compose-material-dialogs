package com.vanpra.composematerialdialogs

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.TextButton
import androidx.ui.unit.dp

@Composable
fun DialogAndShowButton(buttonText: String, content: @Composable()  MaterialDialog.() -> Unit) {
    val dialog = MaterialDialog()
    dialog.build {
        content()
    }

    TextButton(
        onClick = { dialog.show() },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        backgroundColor = Color.DarkGray
    ) {
        Text(
            buttonText,
            modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center)
        )
    }
}