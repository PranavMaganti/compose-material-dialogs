package com.vanpra.composematerialdialogs

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimation
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.defaultDecayAnimationSpec
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.window.Dialog
import kotlin.math.abs

@Composable
internal fun getString(@StringRes res: Int? = null, default: String? = null): String {
    return if (res != null) {
        LocalContext.current.getString(res)
    } else default
        ?: throw IllegalArgumentException("Function must receive one non null string parameter")
}

@Composable
internal fun ThemedDialog(onCloseRequest: () -> Unit, children: @Composable () -> Unit) {
    val colors = MaterialTheme.colors
    val typography = MaterialTheme.typography

    Dialog(onDismissRequest = onCloseRequest) {
        MaterialTheme(colors = colors, typography = typography) {
            children()
        }
    }
}

fun List<Pair<MaterialDialogButtonTypes, Placeable>>.buttons(type: MaterialDialogButtonTypes)
    = this.filter { it.first == type }.map { it.second }