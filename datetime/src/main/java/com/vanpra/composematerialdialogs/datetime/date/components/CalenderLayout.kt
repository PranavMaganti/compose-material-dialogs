package com.vanpra.composematerialdialogs.datetime.date.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.PagerState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerColors
import com.vanpra.composematerialdialogs.datetime.date.DatePickerConstants
import com.vanpra.composematerialdialogs.datetime.util.getNarrowName
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
internal fun NextPreviousMonth(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row {
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

@Composable
internal fun CalendarMonth(
    modifier: Modifier = Modifier,
    viewDate: LocalDate,
    colors: DatePickerColors
) {
    val locale = Locale.getDefault()
    val firstDayOfWeek = remember(locale) { WeekFields.of(locale).firstDayOfWeek }
    val numDays = viewDate.month.length(viewDate.isLeapYear)
    val firstDay = (viewDate.withDayOfMonth(1).dayOfWeek.value - firstDayOfWeek.value) % 7

    DateGrid(
        modifier = modifier,
        rows = 6,
        cols = 7,
        startOffset = firstDay,
        numDays = numDays,
        header = {
            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = firstDayOfWeek.plus(it.toLong()).getNarrowName(locale),
                    color = colors.weekdaysText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    ) {
        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            Text(
                text = it.toString(),
                color = colors.dateText(active = false).value,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun DateGrid(
    modifier: Modifier = Modifier,
    rows: Int,
    cols: Int,
    startOffset: Int,
    numDays: Int,
    header: @Composable (Int) -> Unit,
    item: @Composable (Int) -> Unit
) {
    Layout(modifier = modifier, content = {
        for (x in 0..rows) header(x)
        for (x in 1..numDays) item(x)
    }) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }

        // Assume all items have fixed height and width
        val itemHeight = placeables[0].height
        val itemWidth = placeables[0].width

        val headerPlaceables = placeables.subList(0, rows + 1)
        val datePlaceables = placeables.subList(rows + 1, placeables.size)

        val height = (rows + 1) * itemHeight
        val width = itemWidth * cols
        val spacing = (constraints.maxWidth - width) / (cols - 1)

        layout(constraints.maxWidth, height) {
            headerPlaceables.forEachIndexed { index, placeable ->
                placeable.place(
                    x = index * (itemWidth + spacing),
                    y = 0
                )
            }
            datePlaceables.forEachIndexed { index, placeable ->
                placeable.place(
                    x = ((index + startOffset) % cols) * (itemWidth + spacing),
                    y = ((index + startOffset) / cols + 1) * itemHeight
                )
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
