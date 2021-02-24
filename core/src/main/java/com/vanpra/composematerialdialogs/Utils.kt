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
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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

class DefaultAnchorFlingBehavior(
    private val anchors: List<Float>,
    private val decay: DecayAnimationSpec<Float>,
    private val currentValue: Int
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        Log.d("VELOC", initialVelocity.toString())
        val initialValue = currentValue.toFloat()
        val target = decay.calculateTargetValue(initialValue, initialVelocity)
        val point = anchors.minByOrNull { abs(it - target) } ?: 0f

        var velocityLeft = initialVelocity
        var lastValue = initialValue
        AnimationState(
            initialValue = initialValue,
            initialVelocity = initialVelocity,
        ).animateTo(point) {
            val delta = value - lastValue
            val left = scrollBy(delta)
            lastValue = value
            velocityLeft = this.velocity
            // avoid rounding errors and stop if anything is unconsumed
            if (abs(left) > 0.5f) this.cancelAnimation()
        }

        return velocityLeft
    }
}
