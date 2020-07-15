package com.vanpra.composematerialdialogs

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.Composable
import androidx.ui.core.ContextAmbient
import androidx.ui.foundation.Dialog
import androidx.ui.material.MaterialTheme

@Composable
fun Context.getString(@StringRes res: Int? = null, default: String? = null): String {
    return if (res != null) {
        ContextAmbient.current.getString(res)
    } else default
        ?: throw IllegalArgumentException("Function must receive one non null string parameter")
}

@Composable
internal fun ThemedDialog(onCloseRequest: () -> Unit, children: @Composable() () -> Unit) {
    val colors = MaterialTheme.colors
    val typography = MaterialTheme.typography

    Dialog(onCloseRequest = onCloseRequest) {
        MaterialTheme(colors = colors, typography = typography) {
            children()
        }
    }
}