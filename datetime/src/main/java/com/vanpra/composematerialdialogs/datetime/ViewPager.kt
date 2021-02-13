//package com.vanpra.composematerialdialogs.datetime
//
//import androidx.compose.animation.animatedFloat
//import androidx.compose.animation.core.AnimatedFloat
//import androidx.compose.animation.core.AnimationEndReason
//import androidx.compose.animation.core.SpringSpec
//import androidx.compose.foundation.ScrollableRow
//import androidx.compose.foundation.animation.FlingConfig
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.draggable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.preferredWidth
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.onCommit
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.WithConstraints
//import androidx.compose.ui.platform.AmbientDensity
//import androidx.compose.ui.unit.Dp
//import kotlin.math.abs
//import kotlin.math.sign
//
///**
// * @brief Interface used to pass data to children of ViewPager
// */
//interface ViewPagerScope {
//    /**
//     * @brief Index of child
//     * **/
//    val index: Int
//
//    /**
//     * @brief Scroll viewpager to next page
//     */
//    fun next()
//
//    /**
//     * @brief Scroll viewpager to previous page
//     */
//    fun previous()
//}
//
//private data class ViewPagerImpl(
//    override val index: Int,
//    val increment: (Int) -> Unit
//) : ViewPagerScope {
//    override fun next() {
//        increment(1)
//    }
//
//    override fun previous() {
//        increment(-1)
//    }
//}
//
///**
// * @brief Basic ViewPage implementation in compose
// */
//@Composable
//fun ViewPager(
//    modifier: Modifier = Modifier,
//    onNext: () -> Unit = {},
//    onPrevious: () -> Unit = {},
//    useAlpha: Boolean = false,
//    enabled: Boolean = true,
//    content: @Composable ViewPagerScope.() -> Unit
//) {
//    Column(Modifier.background(Color.Transparent)) {
//        WithConstraints {
//            val alphas = remember { mutableStateOf(mutableListOf(1f, 1f, 1f)) }
//            val width = constraints.maxWidth.toFloat()
//            val offset = animatedFloat(width)
//            offset.setBounds(0f, 2 * width)
//
//            val anchors = listOf(0f, width, 2 * width)
//            val index = remember { mutableStateOf(0) }
//
//            val flingConfig = FlingConfig(
//                anchors,
//                animationSpec = SpringSpec(dampingRatio = 0.8f, stiffness = 1000f),
//            )
//
//            val increment = { increment: Int ->
//                offset.animateTo(
//                    width * sign(increment.toDouble()).toFloat() + width,
//                    onEnd = { animationEndReason, _ ->
//                        if (animationEndReason != AnimationEndReason.Interrupted) {
//                            index.value += increment
//                            offset.snapTo(width)
//                        }
//                    }
//                )
//            }
//
//            val draggable = modifier.draggable(
//                orientation = Orientation.Horizontal,
//                onDrag = {
//                    val old = offset.value
//                    offset.snapTo(offset.value - (it * 0.5f))
//                    offset.value - old
//                },
//                onDragStopped = {
////                    offset.fling(
////                        -(it * 0.6f),
////                        config = flingConfig,
////                        onAnimationEnd = { reason, end, _ ->
////                            offset.snapTo(width)
////
////                            if (reason != AnimationEndReason.Interrupted) {
////                                if (end == width * 2) {
////                                    index.value += 1
////                                    onNext()
////                                } else if (end == 0f) {
////                                    index.value -= 1
////                                    onPrevious()
////                                }
////                            }
////                        }
////                    )
//                },
//                enabled = enabled
//            )
//
//            onCommit(
//                index.value,
//                {
//                    if (useAlpha) {
//                        if (offset.value < width) {
//                            alphas.value[0] = 1 - offset.value / width
//                        } else if (offset.value > width) {
//                            alphas.value[2] = ((offset.value - width) / width)
//                        }
//
//                        alphas.value[1] = 1 - abs(offset.value - width) / width
//                    }
//                }
//            )
//
//            ScrollableRow(
//                content = {
//                    Row(
//                        draggable.preferredWidth(maxWidth * 3)
//                            .offset(-offset.toDp())
//                    ) {
//                        for (x in -1..1) {
//                            val previous = offset.value < width && x == -1
//                            val current = x == 0
//                            val next = offset.value > width && x == 1
//
//                            Column(Modifier.preferredWidth(maxWidth).alpha(alphas.value[x + 1])) {
//                                if (previous || current || next) {
//                                    val viewPagerImpl = ViewPagerImpl(index.value + x, increment)
//                                    content(viewPagerImpl)
//                                }
//                            }
//                        }
//                    }
//                }
//            )
//        }
//    }
//}
//
//@Composable
//private fun AnimatedFloat.toDp(): Dp = with(AmbientDensity.current) { this@toDp.value.toDp() }
