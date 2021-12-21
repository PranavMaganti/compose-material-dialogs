package com.vanpra.composematerialdialogs

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext

@Composable
internal fun getString(@StringRes res: Int? = null, default: String? = null): String {
    return if (res != null) {
        LocalContext.current.getString(res)
    } else default
        ?: throw IllegalArgumentException("Function must receive one non null string parameter")
}

internal fun List<Pair<Any?, Placeable>>.filterButtons(buttonId: String) =
    this.filter { it.first == buttonId }.map { it.second }
