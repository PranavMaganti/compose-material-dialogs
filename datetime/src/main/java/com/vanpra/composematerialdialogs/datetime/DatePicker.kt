package com.vanpra.composematerialdialogs.datetime

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.primarySurface
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vanpra.composematerialdialogs.MaterialDialog
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle.FULL
import java.util.*

internal class DatePickerData(val current: LocalDate) {
    var selected by mutableStateOf(current)
}

/**
 * @brief A date picker body layout
 *
 * @param initialDate The time to be shown to the user when the dialog is first shown.
 * Defaults to the current date if this is not set
 * @param onComplete callback with a LocalDateTime object when the user completes their input
 * @param onCancel callback when the user cancels the dialog
 */
@Composable
fun MaterialDialog.datepicker(
    initialDate: LocalDate = LocalDate.now(),
    yearRange: IntRange = IntRange(1900, 2100),
    onCancel: () -> Unit = {},
    onComplete: (LocalDate) -> Unit = {}
) {
    val datePickerData = remember { DatePickerData(initialDate) }

    DatePickerImpl(datePickerData, yearRange)

    buttons {
        positiveButton("Ok") {
            onComplete(datePickerData.selected)
        }
        negativeButton("Cancel") {
            onCancel()
        }
    }
}

@Composable
internal fun DatePickerImpl(datePickerData: DatePickerData, yearRange: IntRange) {
    /* Height doesn't include datePickerData height */
    Column(Modifier.size(328.dp, 460.dp)) {
        CalendarHeader(datePickerData)

        val yearPickerShowing = remember { mutableStateOf(false) }
        ViewPager {
            val viewDate = remember(index) { datePickerData.current.plusMonths(index.toLong()) }
            CalendarViewHeader(viewDate, yearPickerShowing)

            Box {
                androidx.compose.animation.AnimatedVisibility(
                    yearPickerShowing.value,
                    Modifier
                        .fillMaxSize()
                        .zIndex(0.7f)
                        .clipToBounds(),
                    enter = slideInVertically({ -it }),
                    exit = slideOutVertically({ -it })
                ) {
                    YearPicker(yearRange, viewDate, yearPickerShowing)
                }

                CalendarView(viewDate, datePickerData)
            }
        }
    }
}

@Composable
private fun ViewPagerScope.YearPicker(
    yearRange: IntRange,
    viewDate: LocalDate,
    yearPickerShowing: MutableState<Boolean>
) {
    val state = rememberLazyListState((viewDate.year - yearRange.first) / 3)
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .padding(start = 24.dp, end = 24.dp), state = state
    ) {
        for (i in yearRange step 3) {
            item {
                Row {
                    for (x in 0 until 3) {
                        val year = remember(yearRange) { i + x }
                        val selected = remember(yearRange, viewDate) { year == viewDate.year }
                        YearPickerItem(year = year, selected = selected) {
                            if (!selected) {
                                coroutineScope.launch {
                                    plusPages((year - viewDate.year) * 12)
                                }
                            }
                            yearPickerShowing.value = false
                        }

                        if (x != 2) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YearPickerItem(year: Int, selected: Boolean, onClick: () -> Unit) {
    val backgroundColor =
        if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    val textColor = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface

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
private fun ViewPagerScope.CalendarViewHeader(
    viewDate: LocalDate,
    yearPickerShowing: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()

    val month = remember(viewDate) { viewDate.month.getDisplayName(FULL, Locale.getDefault()) }
    val year = remember(viewDate) { viewDate.year }
    val yearDropdownIcon = remember(yearPickerShowing.value) {
        if (yearPickerShowing.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
    }

    Box(
        Modifier
            .background(MaterialTheme.colors.primarySurface)
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
                    .clickable(onClick = { coroutineScope.launch { previous() } }),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )

            Spacer(modifier = Modifier.width(24.dp))

            Image(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Next Month",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = { coroutineScope.launch { next() } }),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
        }
    }
}

@Composable
private fun CalendarView(viewDate: LocalDate, datePickerData: DatePickerData) {
    Column(Modifier.padding(start = 12.dp, end = 12.dp)) {
        DayOfWeekHeader()
        val month = remember(viewDate) { getDates(viewDate) }
        val possibleSelected = remember(datePickerData.selected, viewDate) {
            viewDate.year == datePickerData.selected.year &&
                    viewDate.month == datePickerData.selected.month
        }

        for (y in 0..5) {
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (x in 0 until 7) {
                    val day = month[y * 7 + x]
                    if (day != -1) {
                        val selected = remember(datePickerData.selected, possibleSelected) {
                            possibleSelected && day == datePickerData.selected.dayOfMonth
                        }
                        DateSelectionBox(day, selected) {
                            datePickerData.selected =
                                LocalDate.of(viewDate.year, viewDate.month, day)
                        }
                    } else {
                        Box(Modifier.size(40.dp))
                    }

                    if (x != 6) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DateSelectionBox(date: Int, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colors
    val backgroundColor = remember(selected) {
        if (selected) colors.primary else colors.surface
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
            ), contentAlignment = Alignment.Center
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
private fun CalendarHeader(datePickerData: DatePickerData) {
    val month = datePickerData.selected.month.shortLocalName
    val day = datePickerData.selected.dayOfWeek.shortLocalName

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
    val dates = mutableListOf<Int>()

    val firstDate = LocalDate.of(date.year, date.monthValue, 1)
    val firstDay = firstDate.dayOfWeek.value % 7
    val numDays = date.month.length(firstDate.isLeapYear)

    var counter = 1
    for (y in 0..5) {
        for (x in 0..6) {
            if ((y == 0 && x < firstDay && firstDay != 0) || counter > numDays) {
                dates.add(-1)
            } else {
                dates.add(counter)
                counter += 1
            }
        }
    }

    return dates
}