package com.vanpra.composematerialdialogs.datetime


import androidx.animation.AnimatedFloat
import androidx.animation.AnimationEndReason
import androidx.animation.PhysicsBuilder
import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.animation.animatedFloat
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.core.drawOpacity
import androidx.ui.foundation.Box
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.InteractionState
import androidx.ui.foundation.animation.AnchorsFlingConfig
import androidx.ui.foundation.animation.fling
import androidx.ui.foundation.gestures.DragDirection
import androidx.ui.foundation.gestures.draggable
import androidx.ui.graphics.Color
import androidx.ui.layout.Row
import androidx.ui.layout.offset
import androidx.ui.layout.preferredWidth
import androidx.ui.unit.Dp
import kotlin.math.abs
import kotlin.math.sign

interface ViewPagerScope {
    val index: Int
    val interactionState: InteractionState

    fun next()
    fun previous()
}

private data class ViewPagerImpl(
    override val index: Int,
    override val interactionState: InteractionState,
    val increment: (Int) -> Unit
) : ViewPagerScope {
    override fun next() {
        increment(1)
    }

    override fun previous() {
        increment(-1)
    }
}


@Composable
fun ViewPager(
    modifier: Modifier = Modifier,
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    useAlpha: Boolean = false,
    range: IntRange = IntRange.EMPTY,
    enabled: Boolean = true,
    screenItem: @Composable() ViewPagerScope.() -> Unit
) {
    Box(backgroundColor = Color.Transparent) {
        WithConstraints {
            val alphas = state { mutableListOf(1f, 1f, 1f) }
            val width = constraints.maxWidth.toFloat()
            val offset = animatedFloat(width)
            offset.setBounds(0f, 2 * width)

            val anchors = listOf(0f, width, 2 * width)
            val index = state { 0 }

            val flingConfig = AnchorsFlingConfig(anchors,
                animationBuilder = PhysicsBuilder(dampingRatio = 0.8f, stiffness = 1000f),
                onAnimationEnd = { reason, end, _ ->
                    offset.snapTo(width)

                    if (reason != AnimationEndReason.Interrupted) {
                        if (end == width * 2) {
                            index.value += 1
                            onNext()
                        } else if (end == 0f) {
                            index.value -= 1
                            onPrevious()
                        }
                    }
                })

            val increment = { increment: Int ->
                offset.animateTo(
                    width * sign(increment.toDouble()).toFloat() + width,
                    onEnd = { animationEndReason, _ ->
                        if (animationEndReason != AnimationEndReason.Interrupted) {
                            index.value += increment
                            offset.snapTo(width)
                        }
                    })
            }

            val interactionState = InteractionState()

            val draggable = modifier.draggable(
                dragDirection = DragDirection.Horizontal,
                onDragDeltaConsumptionRequested = {
                    val old = offset.value
                    offset.snapTo(offset.value - (it * 0.5f))
                    offset.value - old
                }, onDragStopped = { offset.fling(flingConfig, -(it * 0.6f)) },
                interactionState = interactionState,
                enabled = enabled
            )

            if (useAlpha) {
                if (offset.value < width) {
                    alphas.value[0] = 1 - offset.value / width
                } else if (offset.value > width) {
                    alphas.value[2] = ((offset.value - width) / width)
                }

                alphas.value[1] = 1 - abs(offset.value - width) / width
            }

            HorizontalScroller(isScrollable = false) {
                Row(
                    draggable.preferredWidth(maxWidth * 3)
                        .offset(-offset.toDp())
                ) {
                    for (x in -1..1) {
                        Box(
                            Modifier.preferredWidth(maxWidth).drawOpacity(alphas.value[x + 1])
                        ) {
                            if ((offset.value < width && x == -1) || x == 0 || (offset.value > width && x == 1)) {
                                val viewPagerImpl =
                                    ViewPagerImpl(
                                        index.value + x,
                                        interactionState,
                                        increment
                                    )
                                screenItem(viewPagerImpl)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedFloat.toDp(): Dp = with(DensityAmbient.current) { this@toDp.value.toDp() }