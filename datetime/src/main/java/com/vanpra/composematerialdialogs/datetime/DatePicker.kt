package com.vanpra.composematerialdialogs.datetime

import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerDefaults
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.util.shortLocalName
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle.FULL
import java.util.Locale

internal class DatePickerState(val initialDate: LocalDate) {
    var selected by mutableStateOf(initialDate)
}

/**
 * @brief A date picker body layout
 *
 * @param initialDate time to be shown to the user when the dialog is first shown.
 * Defaults to the current date if this is not set
 * @param yearRange the range of years the user should be allowed to pick from
 * @param waitForPositiveButton if true the [onComplete] callback will only be called when the
 * positive button is pressed, otherwise it will be called on every input change
 * @param onComplete callback with a LocalDateTime object when the user completes their input
 */
@Composable
fun MaterialDialog.datepicker(
    initialDate: LocalDate = LocalDate.now(),
    yearRange: IntRange = IntRange(1900, 2100),
    waitForPositiveButton: Boolean = true,
    onComplete: (LocalDate) -> Unit = {}
) {
    val datePickerState = remember { DatePickerState(initialDate) }

    DatePickerImpl(
        state = datePickerState,
        yearRange = yearRange,
        backgroundColor = dialogBackgroundColor!!
    )

    if (waitForPositiveButton) DialogCallback { onComplete(datePickerState.selected) }
}

@Composable
internal fun DatePickerImpl(
    modifier: Modifier = Modifier,
    state: DatePickerState,
    yearRange: IntRange,
    backgroundColor: Color
) {
    val pagerState = rememberPagerState(
        pageCount = (yearRange.last - yearRange.first) * 12,
        initialPage = (state.initialDate.year - yearRange.first) * 12 + state.initialDate.monthValue - 1
    )
    val yearPickerShowing = remember { mutableStateOf(false) }

    Column(modifier.size(328.dp, 460.dp)) {
        CalendarHeader(state)
        HorizontalPager(
            state = pagerState,
            offscreenLimit = 2,
            verticalAlignment = Alignment.Top,
            flingBehavior = PagerDefaults.defaultPagerFlingConfig(
                state = pagerState,
                snapAnimationSpec = spring(stiffness = 1000f)
            )
        ) { page ->
            val viewDate = remember(page) {
                LocalDate.of(yearRange.first, 1, 1).plusMonths(page.toLong())
            }

            Column {
                CalendarViewHeader(viewDate, yearPickerShowing, pagerState)
                Box {
                    androidx.compose.animation.AnimatedVisibility(
                        yearPickerShowing.value,
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(0.7f)
                            .clipToBounds(),
                        enter = slideInVertically({ -it }),
                        exit = slideOutVertically({ -it })
                    ) {
                        YearPicker(
                            yearRange,
                            viewDate,
                            yearPickerShowing,
                            pagerState,
                            backgroundColor
                        )
                    }

                    CalendarView(viewDate, state)
                }
            }
        }
    }
}

@Composable
private fun YearPicker(
    yearRange: IntRange,
    viewDate: LocalDate,
    yearPickerShowing: MutableState<Boolean>,
    pagerState: PagerState,
    backgroundColor: Color
) {
    val state = rememberLazyListState((viewDate.year - yearRange.first) / 3)
    val coroutineScope = rememberCoroutineScope()

    LazyVerticalGrid(
        cells = GridCells.Fixed(3),
        state = state,
        modifier = Modifier.background(backgroundColor)
    ) {
        itemsIndexed(yearRange.toList()) { _, item ->
            val selected = remember { item == viewDate.year }
            YearPickerItem(year = item, selected = selected) {
                if (!selected) {
                    coroutineScope.launch {
                        pagerState.scrollToPage(
                            pagerState.currentPage + (item - viewDate.year) * 12
                        )
                    }
                }
                yearPickerShowing.value = false
            }
        }
    }
}

@Composable
private fun YearPickerItem(year: Int, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colors
    val backgroundColor = remember(selected) { if (selected) colors.primary else Color.Transparent }
    val textColor = remember(selected) { if (selected) colors.onPrimary else colors.onSurface }

    Box(Modifier.size(88.dp, 52.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(72.dp, 36.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor)
                .clickable(
                    onClick = onClick,
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(year.toString(), style = TextStyle(color = textColor, fontSize = 18.sp))
        }
    }
}

@Composable
private fun CalendarViewHeader(
    viewDate: LocalDate,
    yearPickerShowing: MutableState<Boolean>,
    pagerState: PagerState
) {
    val coroutineScope = rememberCoroutineScope()
    val month = remember(viewDate.month) {
        viewDate.month.getDisplayName(FULL, Locale.getDefault())
    }
    val year = remember(viewDate.year) { viewDate.year }
    val yearDropdownIcon = remember(yearPickerShowing.value) {
        if (yearPickerShowing.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
    }

    Box(
        Modifier
            .padding(top = 16.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
            .height(24.dp)
            .fillMaxWidth()
            .zIndex(1f)
    ) {
        Row(
            Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .clickable(onClick = { yearPickerShowing.value = !yearPickerShowing.value })
        ) {
            Text(
                "$month $year",
                modifier = Modifier
                    .paddingFromBaseline(top = 16.dp)
                    .wrapContentSize(Alignment.Center),
                style = TextStyle(fontSize = 14.sp, fontWeight = W600),
                color = MaterialTheme.colors.onBackground
            )

            Spacer(Modifier.width(4.dp))
            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                Image(
                    yearDropdownIcon,
                    contentDescription = "Year Selector",
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
                )
            }
        }

        Row(
            Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
        ) {
            Image(
                Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous Month",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    ),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )

            Spacer(modifier = Modifier.width(24.dp))

            Image(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Next Month",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    ),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
        }
    }
}

@Composable
private fun CalendarView(viewDate: LocalDate, datePickerData: DatePickerState) {
    Column(Modifier.padding(start = 12.dp, end = 12.dp)) {
        DayOfWeekHeader()
        val month = remember(viewDate) { getDates(viewDate) }
        val possibleSelected = remember(datePickerData.selected, viewDate) {
            viewDate.year == datePickerData.selected.year &&
                    viewDate.month == datePickerData.selected.month
        }

        LazyVerticalGrid(cells = GridCells.Fixed(7)) {
            items(month) {
                if (it != -1) {
                    val selected = remember(possibleSelected) {
                        possibleSelected && it == datePickerData.selected.dayOfMonth
                    }
                    DateSelectionBox(it, selected) {
                        datePickerData.selected = LocalDate.of(viewDate.year, viewDate.month, it)
                    }
                } else {
                    Box(Modifier.size(40.dp))
                }
            }
        }
    }
}

@Composable
private fun DateSelectionBox(date: Int, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colors
    val backgroundColor = remember(selected) {
        if (selected) colors.primary else Color.Transparent
    }
    val textColor = remember(selected) {
        if (selected) colors.onPrimary else colors.onSurface
    }

    Box(
        Modifier
            .size(40.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                onClick = onClick,
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            date.toString(),
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .wrapContentSize(Alignment.Center),
            style = TextStyle(color = textColor, fontSize = 12.sp)
        )
    }
}

@Composable
private fun DayOfWeekHeader() {
    Row(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEachIndexed { index, it ->
            Box(Modifier.size(40.dp)) {
                Text(
                    it,
                    modifier = Modifier
                        .alpha(0.8f)
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    style = TextStyle(fontSize = 14.sp, fontWeight = W600),
                    color = MaterialTheme.colors.onBackground
                )
            }
            if (index != 6) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

// Input: Selected Date
@Composable
private fun CalendarHeader(datePickerData: DatePickerState) {
    val month = remember(datePickerData.selected.month) {
        datePickerData.selected.month.shortLocalName
    }
    val day = remember(datePickerData.selected.dayOfWeek) {
        datePickerData.selected.dayOfWeek.shortLocalName
    }

    Box(
        Modifier
            .background(MaterialTheme.colors.primaryVariant)
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp)) {
            Text(
                text = "SELECT DATE",
                modifier = Modifier.paddingFromBaseline(top = 32.dp),
                color = MaterialTheme.colors.onPrimary,
                style = TextStyle(fontSize = 12.sp)
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(top = 64.dp)
            ) {
                Text(
                    text = "$day, $month ${datePickerData.selected.dayOfMonth}",
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = MaterialTheme.colors.onPrimary,
                    style = TextStyle(fontSize = 30.sp, fontWeight = W400)
                )
            }
        }
    }
}

private fun getDates(date: LocalDate): List<Int> {
    val numDays = date.month.length(date.isLeapYear)
    val firstDate = date.withDayOfMonth(1)
    val firstDay = firstDate.dayOfWeek.value % 7

    val dateRange = IntRange(1, numDays).toList()
    val startRangePadding = List(firstDay) { -1 }

    return startRangePadding + dateRange
}
