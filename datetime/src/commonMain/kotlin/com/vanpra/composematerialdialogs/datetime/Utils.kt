package com.vanpra.composematerialdialogs.datetime

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

// Screen Configuration

internal fun Float.getOffset(angle: Double): Offset =
    Offset((this * cos(angle)).toFloat(), (this * sin(angle)).toFloat())

internal class ScreenConfiguration(val screenWidthDp: Int, val screenHeightDp: Int)

@Composable
internal expect fun rememberScreenConfiguration(): ScreenConfiguration

@Composable
internal expect fun isSmallDevice(): Boolean

@Composable
internal expect fun isLargeDevice(): Boolean

// Canvas Functions

internal expect fun Canvas.drawText(
    text: String,
    x: Float,
    y: Float,
    color: Color,
    textSize: Float,
    angle: Float,
    radius: Float,
    isCenter: Boolean?,
    alpha: Int,
)

// Horizontal Pager For Date Picker

expect class PlatformPagerState {
    var currentPage: Int
    suspend fun scrollToPage(page: Int, pageOffset: Float = 0f)
    suspend fun animateScrollToPage(page: Int, pageOffset: Float = 0f)
}

expect val PlatformPagerState.platformPageCount: Int

expect interface PlatformPagerScope

@Composable
expect fun rememberPlatformPagerState(initialPage: Int = 0): PlatformPagerState

@Composable
expect fun PlatformHorizontalPager(
    modifier: Modifier,
    count: Int,
    state: PlatformPagerState,
    verticalAlignment: Alignment.Vertical,
    content: @Composable PlatformPagerScope.(Int) -> Unit
)

// Platform LocalDate And LocalTime

expect class PlatformLocalDate {
    val year: Int
    val month: Int
    val dayOfMonth: Int
    val monthValue: Int

    fun withDayOfMonth(dayOfMonth: Int): PlatformLocalDate
    fun getMonthShortLocalName(): String
    fun getDayOfWeekShortLocalName(): String
    fun getMonthDisplayName(): String
    fun getFirstDayOfMonth(): Int
    fun getNumDays(): Int

    companion object {
        val MIN: PlatformLocalDate
        val MAX: PlatformLocalDate
        fun now(): PlatformLocalDate
        fun of(year: Int, month: Int, dayOfMonth: Int): PlatformLocalDate
    }
}

expect class PlatformLocalTime : Comparable<PlatformLocalTime> {
    val isAM: Boolean
    val hour: Int
    val minute: Int

    companion object {
        val MIN: PlatformLocalTime
        val MAX: PlatformLocalTime
        fun now(): PlatformLocalTime
        fun of(hour: Int, minute: Int): PlatformLocalTime
        fun of(hour: Int, minute: Int, second: Int): PlatformLocalTime
        fun of(hour: Int, minute: Int, second: Int, nanosecond: Int): PlatformLocalTime
    }

    val simpleHour: Int
    fun withHour(hour: Int): PlatformLocalTime
    fun withMinute(minute: Int): PlatformLocalTime
    fun toAM(): PlatformLocalTime
    fun toPM(): PlatformLocalTime
    fun noSeconds(): PlatformLocalTime
}