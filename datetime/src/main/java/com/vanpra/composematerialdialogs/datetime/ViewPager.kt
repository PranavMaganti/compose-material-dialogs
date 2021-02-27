package com.vanpra.composematerialdialogs.datetime

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.defaultDecayAnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

/**
 * @brief Interface used to pass data to children of ViewPager
 */
interface ViewPagerScope {
    /**
     * @brief Index of child
     * **/
    val index: Int

    /**
     * @brief Scroll viewpager to next page
     */
    suspend fun next()

    /**
     * @brief Scroll viewpager to previous page
     */
    suspend fun previous()
}

private data class ViewPagerImpl(
    override val index: Int,
    val increment: suspend (Int) -> Unit
) : ViewPagerScope {
    override suspend fun next() {
        increment(1)
    }

    override suspend fun previous() {
        increment(-1)
    }
}

/**
 * @brief Basic ViewPage implementation in compose
 */
@Composable
fun ViewPager(
    modifier: Modifier = Modifier,
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    enabled: Boolean = true,
    content: @Composable ViewPagerScope.() -> Unit
) {
    Column(Modifier.background(Color.Transparent)) {
        BoxWithConstraints {
            val coroutineScope = rememberCoroutineScope()
            val width = constraints.maxWidth.toFloat()
            val offset = remember { Animatable(initialValue = 0f) }
            offset.updateBounds(lowerBound = -width, upperBound = width)

            val draggableState = rememberDraggableState {
                coroutineScope.launch {
                    val old = offset.value
                    offset.snapTo(offset.value - it)
                    offset.value - old
                }
            }

            val anchors = listOf(-width, 0f, width)
            val index = remember { mutableStateOf(0) }

            val increment: suspend (Int) -> Unit = { increment: Int ->
                val animationResult =
                    offset.animateTo(width * sign(increment.toDouble()).toFloat())
                if (animationResult.endReason == AnimationEndReason.Finished) {
                    index.value += increment
                    offset.snapTo(0f)
                }
            }

            val decayAnimation = defaultDecayAnimationSpec()
            val draggable = modifier.draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                onDragStopped = { velocity ->
                    val initialTarget = decayAnimation.calculateTargetValue(offset.value, -velocity)
                    val target = anchors.minByOrNull { abs(it - initialTarget) } ?: 0f
                    val flingResult = offset.animateTo(target, spring(stiffness = 10000f))
                    offset.snapTo(0f)

                    if (flingResult.endReason == AnimationEndReason.Finished) {
                        if (flingResult.endState.value < 0) {
                            index.value += 1
                            onNext()
                        } else if (flingResult.endState.value > 0) {
                            index.value -= 1
                            onPrevious()
                        }
                    }


                },
                reverseDirection = true,
                enabled = enabled
            )

            Layout(content = {
                for (x in -1..1) {
                    Column(Modifier.width(this@BoxWithConstraints.maxWidth).layoutId(x)) {
                        val viewPagerImpl = ViewPagerImpl(index.value + x, increment)
                        content(viewPagerImpl)
                    }
                }
            }, modifier = draggable) { measurables, constraints ->
                val placeables =
                    measurables.sortedBy { it.layoutId.toString() }.map { it.measure(constraints) }
                val height = placeables.maxByOrNull { it.height }?.height ?: 0

                layout(constraints.maxWidth, height) {
                    placeables.forEachIndexed { index, placeable ->
                        placeable.place(
                            x = offset.value.toInt() + (index - 1) * constraints.maxWidth,
                            y = 0
                        )
                    }
                }
            }
        }
    }
}