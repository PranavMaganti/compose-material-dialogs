package com.vanpra.composematerialdialogs.datetime.date

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.datetime.R
import com.vanpra.composematerialdialogs.datetime.util.getShortLocalName
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.ceil
import kotlinx.coroutines.launch

/**
 * @brief A date picker body layout
 *
 * @param initialDate time to be shown to the user when the dialog is first shown.
 * Defaults to the current date if this is not set
 * @param yearRange the range of years the user should be allowed to pick from
 * @param waitForPositiveButton if true the [onDateChange] callback will only be called when the
 * positive button is pressed, otherwise it will be called on every input change
 * @param onDateChange callback with a LocalDateTime object when the user completes their input
 * @param allowedDateValidator when this returns true the date will be selectable otherwise it won't be
 */
@Composable
fun MaterialDialogScope.datepicker(
    initialDate: LocalDate = LocalDate.now(),
    title: String = "SELECT DATE",
    colors: DatePickerColors = DatePickerDefaults.colors(),
    yearRange: IntRange = IntRange(1900, 2100),
    waitForPositiveButton: Boolean = true,
    allowedDateValidator: (LocalDate) -> Boolean = { true },
    locale: Locale = Locale.getDefault(),
    onDateChange: (LocalDate) -> Unit = {}
) {
    val datePickerState = remember {
        DatePickerState(initialDate, colors, yearRange)
    }

    DatePickerImpl(title = title, state = datePickerState, allowedDateValidator, locale)

    if (waitForPositiveButton) {
        DialogCallback { onDateChange(datePickerState.selected) }
    } else {
        DisposableEffect(datePickerState.selected) {
            onDateChange(datePickerState.selected)
            onDispose { }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun DatePickerImpl(
    title: String,
    state: DatePickerState,
    allowedDateValidator: (LocalDate) -> Boolean,
    locale: Locale
) {
    val pagerState = rememberPagerState(
        initialPage = (state.selected.year - state.yearRange.first) * 12 + state.selected.monthValue - 1
    )

    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        CalendarHeader(title, state, locale)

        Spacer(Modifier.height(12.dp))
        Divider(Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        HorizontalPager(
            count = (state.yearRange.last - state.yearRange.first + 1) * 12,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.height(312.dp),
            userScrollEnabled = !state.yearPickerShowing
        ) { page ->
            val viewDate = remember {
                LocalDate.of(
                    state.yearRange.first + page / 12,
                    page % 12 + 1,
                    1
                )
            }

            Column {
                CalendarViewHeader(viewDate, state, pagerState, locale)
                Box {
                    androidx.compose.animation.AnimatedVisibility(
                        state.yearPickerShowing,
                        modifier = Modifier
                            .zIndex(0.7f)
                            .clipToBounds(),
                        enter = slideInVertically(initialOffsetY = { -it }),
                        exit = slideOutVertically(targetOffsetY = { -it })
                    ) {
                        YearPicker(viewDate, state, pagerState)
                    }

                    CalendarView(viewDate, state, locale, allowedDateValidator)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
private fun YearPicker(
    viewDate: LocalDate,
    state: DatePickerState,
    pagerState: PagerState
) {
    val gridState = rememberLazyGridState(viewDate.year - state.yearRange.first)
    val coroutineScope = rememberCoroutineScope()

    LazyVerticalGrid(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        columns = GridCells.Fixed(3),
        state = gridState
    ) {
        itemsIndexed(state.yearRange.toList()) { _, item ->
            val selected = remember { item == viewDate.year }
            YearPickerItem(year = item, selected = selected, colors = state.colors) {
                if (!selected) {
                    coroutineScope.launch {
                        pagerState.scrollToPage(
                            pagerState.currentPage + (item - viewDate.year) * 12
                        )
                    }
                }
                state.yearPickerShowing = false
            }
        }
    }
}

@Composable
private fun YearPickerItem(
    year: Int,
    selected: Boolean,
    colors: DatePickerColors,
    onClick: () -> Unit
) {
    Box(Modifier.size(88.dp, 52.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(72.dp, 36.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.dateContainer(selected).value)
                .clickable(
                    onClick = onClick,
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                year.toString(),
                style = TextStyle(
                    color = colors.dateText(selected).value,
                    fontSize = 18.sp
                )
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CalendarViewHeader(
    viewDate: LocalDate,
    state: DatePickerState,
    pagerState: PagerState,
    locale: Locale
) {
    val coroutineScope = rememberCoroutineScope()
    val month = remember { viewDate.month.getShortLocalName(locale) }
    val arrowDropUp = painterResource(id = R.drawable.baseline_arrow_drop_up_24)
    val arrowDropDown = painterResource(id = R.drawable.baseline_arrow_drop_down_24)

    Box(
        Modifier
            .padding(bottom = 16.dp, start = 12.dp, end = 12.dp)
            .height(24.dp)
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .clickable(onClick = { state.yearPickerShowing = !state.yearPickerShowing })
        ) {
            Text(
                "$month ${viewDate.year}",
                modifier = Modifier
                    .paddingFromBaseline(top = 16.dp)
                    .wrapContentSize(Alignment.Center),
                style = TextStyle(fontSize = 14.sp, fontWeight = W600),
                color = state.colors.selectedDateTitle
            )

            Spacer(Modifier.width(4.dp))
            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                Icon(
                    if (state.yearPickerShowing) arrowDropUp else arrowDropDown,
                    contentDescription = "Year Selector"
                )
            }
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    modifier = Modifier
                        .testTag("dialog_date_prev_month")
                        .size(24.dp)
                        .clickable(onClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage - 1 >= 0) {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage - 1
                                    )
                                }
                            }
                        })
                )

                Spacer(modifier = Modifier.width(24.dp))

                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    modifier = Modifier
                        .testTag("dialog_date_next_month")
                        .size(24.dp)
                        .clickable(onClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage + 1 < pagerState.pageCount) {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage + 1
                                    )
                                }
                            }
                        })
                )
            }
        }
    }
}

@Composable
private fun CalendarView(
    viewDate: LocalDate,
    state: DatePickerState,
    locale: Locale,
    allowedDateValidator: (LocalDate) -> Boolean
) {
    Column(
        Modifier
            .testTag("dialog_date_calendar")
    ) {
        DayOfWeekHeader(state, locale)
        val calendarDatesData = remember { getDates(viewDate, locale) }
        val weeks =
            remember { ceil((calendarDatesData.first + calendarDatesData.second) / 7f).toInt() }
        val possibleSelected = remember(state.selected) {
            viewDate.year == state.selected.year && viewDate.month == state.selected.month
        }

        for (i in 0..weeks) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                val baseDate = remember { i * 7 - calendarDatesData.first }
                for (day in baseDate + 1..baseDate + 7) {
                    if (day <= 0 || day > calendarDatesData.second) {
                        Box(Modifier.size(40.dp))
                    } else {
                        val selected = remember(state.selected) {
                            possibleSelected && day == state.selected.dayOfMonth
                        }
                        val date = remember { viewDate.withDayOfMonth(day) }
                        val enabled = remember(date) { allowedDateValidator(date) }

                        DateSelectionBox(day, selected, state.colors, enabled) {
                            state.selected = date
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateSelectionBox(
    date: Int,
    selected: Boolean,
    colors: DatePickerColors,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .testTag("dialog_date_selection_$date")
            .size(40.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                onClick = { if (enabled) onClick() },
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            date.toString(),
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .then(
                    if (selected) Modifier.background(MaterialTheme.colorScheme.primary) else Modifier
                )
                .wrapContentSize(Alignment.Center)
                .then(if (enabled) Modifier else Modifier.alpha(DatePickerConstants.DisabledAlpha)),
            style = TextStyle(
                fontSize = 12.sp,
                color = colors.dateContainer(active = selected).value
            )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DayOfWeekHeader(state: DatePickerState, locale: Locale) {
    val dayHeaders = WeekFields.of(locale).firstDayOfWeek.let { firstDayOfWeek ->
        (0L until 7L).map {
            firstDayOfWeek.plus(it).getDisplayName(java.time.format.TextStyle.NARROW, locale)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dayHeaders.forEach {
            Box(Modifier.size(40.dp)) {
                Text(
                    it,
                    modifier = Modifier
                        .alpha(0.8f)
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    style = TextStyle(fontSize = 14.sp, fontWeight = W600)
                )
            }
        }
    }
}

@Composable
private fun CalendarHeader(title: String, state: DatePickerState, locale: Locale) {
    val month = remember(state.selected) { state.selected.month.getShortLocalName(locale) }
    val day = remember(state.selected) { state.selected.dayOfWeek.getShortLocalName(locale) }

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column {
            Text(
                text = title,
                color = state.colors.headerText,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(Modifier.height(36.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Text(
                    text = "$day, $month ${state.selected.dayOfMonth}",
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = state.colors.selectedDateTitle,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    }
}

private fun getDates(date: LocalDate, locale: Locale): Pair<Int, Int> {
    val numDays = date.month.length(date.isLeapYear)

    val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek.value
    val firstDay = date.withDayOfMonth(1).dayOfWeek.value - firstDayOfWeek % 7

    return Pair(firstDay, numDays)
}

object DatePickerConstants {
    const val DisabledAlpha = 0.38f
}
