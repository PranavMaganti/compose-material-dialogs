package com.vanpra.composematerialdialogs.datetime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import org.jetbrains.skia.Color4f
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.TextBlobBuilder
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.*

@Composable
internal actual fun rememberScreenConfiguration(): ScreenConfiguration {
    return remember {
        ScreenConfiguration(
            screenWidthDp = 600,
            screenHeightDp = 400
        )
    }
}

@Composable
internal actual fun isSmallDevice(): Boolean {
    return false
}

@Composable
internal actual fun isLargeDevice(): Boolean {
    return true
}

// todo This function needs to be corrected
internal actual fun Canvas.drawText(
    text: String,
    x: Float,
    y: Float,
    color: Color,
    textSize: Float,
    angle: Float,
    radius: Float,
    isCenter: Boolean?,
    alpha: Int,
) {
    val outerText = Paint()
    outerText.color = color.toAwtColor()

    nativeCanvas.drawTextBlob(blob = TextBlobBuilder().apply {
        this.appendRun(font = Font(), text = text, x = x, y = y)
    }.build()!!, x = x, y = y, Paint())
}

internal fun Color.toAwtColor(): Int {
    return Color4f(red, green, blue, alpha).toColor()
}

// Horizontal Pager
actual class PlatformPagerState {
    var internalPageCount: Int = 0
        internal set
    actual var currentPage: Int = 0

    actual suspend fun scrollToPage(page: Int, pageOffset: Float) {
        currentPage = page
    }

    actual suspend fun animateScrollToPage(page: Int, pageOffset: Float) {
        //todo
    }
}

actual val PlatformPagerState.platformPageCount: Int
    get() = internalPageCount

actual interface PlatformPagerScope {
    //todo
}

@Composable
actual fun rememberPlatformPagerState(initialPage: Int): PlatformPagerState {
    return remember {
        PlatformPagerState()
    }
}

@Composable
actual fun PlatformHorizontalPager(
    modifier: Modifier,
    count: Int,
    state: PlatformPagerState,
    verticalAlignment: Alignment.Vertical,
    content: @Composable PlatformPagerScope.(Int) -> Unit,
) {
    state.internalPageCount = count
    LazyRow(modifier = modifier, verticalAlignment = verticalAlignment) {
        items(count) { index ->
            Box(Modifier.fillMaxWidth()) {
                content(object : PlatformPagerScope {}, index)
            }
        }
    }
}

actual class PlatformLocalDate(val date: LocalDate) {
    actual val year : Int
        get() = date.year
    actual val month : Int
        get() = date.month.value
    actual val dayOfMonth : Int
        get() = date.dayOfMonth
    actual val monthValue : Int
        get() = date.monthValue

    actual fun withDayOfMonth(dayOfMonth : Int) : PlatformLocalDate{
        return PlatformLocalDate(date.withDayOfMonth(dayOfMonth))
    }

    actual fun getMonthShortLocalName(): String {
        return date.month.shortLocalName
    }

    actual fun getMonthDisplayName() : String {
        return date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    actual fun getDayOfWeekShortLocalName() : String {
        return date.dayOfWeek.shortLocalName
    }

    actual companion object {

        actual val MIN: PlatformLocalDate = PlatformLocalDate(LocalDate.MIN)
        actual val MAX: PlatformLocalDate = PlatformLocalDate(LocalDate.MAX)

        actual fun now() : PlatformLocalDate {
            return PlatformLocalDate(LocalDate.now())
        }
        actual fun of(year : Int,month : Int,dayOfMonth: Int) : PlatformLocalDate {
            return PlatformLocalDate(LocalDate.of(year, month, dayOfMonth))
        }
    }

    actual fun getFirstDayOfMonth(): Int {
        return date.withDayOfMonth(1).dayOfWeek.value % 7
    }

    actual fun getNumDays(): Int {
        return date.month.length(date.isLeapYear)
    }
}

actual class PlatformLocalTime(var time: LocalTime) : Comparable<PlatformLocalTime> {
    override fun compareTo(other: PlatformLocalTime): Int {
        return time.compareTo(other.time)
    }

    actual val isAM: Boolean
        get() = time.isAM
    actual val hour: Int
        get() = time.hour
    actual val minute: Int
        get() = time.minute

    actual companion object {

        actual val MIN: PlatformLocalTime = PlatformLocalTime(LocalTime.MIN)
        actual val MAX: PlatformLocalTime = PlatformLocalTime(LocalTime.MAX)

        actual fun now(): PlatformLocalTime {
            return PlatformLocalTime(LocalTime.now())
        }
        actual fun of(hour : Int,minute : Int) : PlatformLocalTime {
            return PlatformLocalTime(LocalTime.of(hour,minute))
        }
        actual fun of(hour : Int,minute : Int,second : Int) : PlatformLocalTime{
            return PlatformLocalTime(LocalTime.of(hour,minute,second))
        }
        actual fun of(hour : Int,minute : Int,second : Int,nanosecond : Int) : PlatformLocalTime{
            return PlatformLocalTime(LocalTime.of(hour,minute,second,nanosecond))
        }
    }

    actual val simpleHour: Int
        get() = time.simpleHour

    actual fun withHour(hour: Int): PlatformLocalTime {
        return PlatformLocalTime(time.withHour(hour))
    }

    actual fun withMinute(minute: Int): PlatformLocalTime {
        return PlatformLocalTime(time.withMinute(minute))
    }

    actual fun noSeconds(): PlatformLocalTime {
        return PlatformLocalTime(time.noSeconds())
    }

    actual fun toPM(): PlatformLocalTime {
        return PlatformLocalTime(time.toPM())
    }

    actual fun toAM(): PlatformLocalTime {
        return PlatformLocalTime(time.toAM())
    }
}