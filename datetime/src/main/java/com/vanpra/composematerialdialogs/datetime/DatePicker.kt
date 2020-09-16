package com.vanpra.composematerialdialogs.datetime

import androidx.compose.foundation.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope.gravity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.vanpra.composematerialdialogs.MaterialDialog
import java.time.LocalDate
import java.time.YearMonth

val dateBoxDp = 35.dp

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
    onCancel: () -> Unit = {},
    onComplete: (LocalDate) -> Unit = {}
) {
    val currentDate = remember { initialDate }
    val selectedDate = remember { mutableStateOf(currentDate) }

    DatePickerLayout(currentDate = currentDate, selectedDate = selectedDate)

    buttons {
        positiveButton("Ok") {
            onComplete(selectedDate.value)
        }
        negativeButton("Cancel") {
            onCancel()
        }
    }
}

@Composable
internal fun DatePickerLayout(
    modifier: Modifier = Modifier,
    selectedDate: MutableState<LocalDate>,
    currentDate: LocalDate
) {
    Box(modifier) {
        WithConstraints {
            ScrollableColumn(Modifier.heightIn(max = maxHeight * 0.8f)) {
                DateTitle(selectedDate)
                ViewPager(Modifier.background(color = Color.Transparent), useAlpha = true) {
                    val newDate = remember(index) {
                        currentDate.plusMonths(index.toLong())
                    }
                    val dates = remember(newDate) { getDates(newDate) }
                    val yearMonth = remember(newDate) { newDate.yearMonth }

                    Column {
                        MonthTitle(
                            this@ViewPager,
                            newDate.month.fullLocalName,
                            newDate.year.toString()
                        )
                        DaysTitle()
                        DateLayout(dates, yearMonth, selectedDate)
                    }
                }
            }
        }
    }
}

@Composable
private fun DateLayout(
    month: List<Int>,
    yearMonth: YearMonth,
    selected: MutableState<LocalDate>
) {
    val check = remember(selected.value, yearMonth) {
        selected.value.monthValue == yearMonth.monthValue &&
            selected.value.year == yearMonth.year
    }

    val textStyle = TextStyle(fontSize = 13.sp, fontWeight = W400)
    val boxSize = 35.dp
    val boxSizePx = with(DensityAmbient.current) { boxSize.toIntPx() }

    val verticalSpacing = 30
    val maxRows = 6
    val layoutHeight = maxRows * boxSizePx + (verticalSpacing * (maxRows - 1))

    Layout({
        month.fastForEach {
            if (it != -1) {
                var selectedModifier: Modifier = Modifier
                var textColor: Color = MaterialTheme.colors.onBackground

                if (check && selected.value.dayOfMonth == it) {
                    selectedModifier = Modifier.background(
                        color = MaterialTheme.colors.primaryVariant.copy(0.7f),
                        shape = CircleShape
                    )
                    textColor = Color.White
                }

                Text(
                    it.toString(),
                    modifier = Modifier.size(boxSize)
                        .clickable(
                            onClick = {
                                selected.value =
                                    LocalDate.of(yearMonth.year, yearMonth.month, it)
                            },
                            indication = null
                        )
                        .then(selectedModifier)
                        .wrapContentSize(Alignment.Center),
                    style = textStyle,
                    color = textColor
                )
            } else {
                Box(Modifier.size(boxSize))
            }
        }
    },
        Modifier.padding(
            top = 8.dp,
            start = 24.dp,
            end = 24.dp
        )
            .fillMaxWidth()
            .gravity(Alignment.CenterHorizontally),
        { measurables, constraints ->
            val horizontalSpacing = (constraints.maxWidth - (boxSizePx * 7)) / 6

            layout(constraints.maxWidth, layoutHeight) {
                measurables
                    .map { it.measure(Constraints(maxHeight = boxSizePx, maxWidth = boxSizePx)) }
                    .fastForEachIndexed { index, it ->
                        it.place(
                            x = (index % 7) * (boxSizePx + horizontalSpacing),
                            y = (index / 7) * (boxSizePx + verticalSpacing)
                        )
                    }
            }
        })
}

@Composable
private fun DaysTitle() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(top = 16.dp, bottom = 12.dp, start = 18.dp, end = 18.dp)
            .fillMaxWidth()
    ) {
        listOf("M", "T", "W", "T", "F", "S", "S").fastForEach {
            Box(Modifier.preferredSize(dateBoxDp)) {
                Text(
                    it,
                    modifier = Modifier.drawOpacity(0.8f).fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    style = TextStyle(fontSize = 14.sp, fontWeight = W600),
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}

@Composable
private fun MonthTitle(scope: ViewPagerScope, month: String, year: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Box(
            Modifier.clip(CircleShape)
                .clickable(
                    onClick = { scope.previous() },
                    enabled = true
                )
        ) {
            Image(
                Icons.Default.ChevronLeft,
                modifier = Modifier.padding(start = 24.dp).wrapContentWidth(Alignment.Start),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
        }

        Text(
            "$month $year",
            modifier = Modifier.weight(3f).wrapContentSize(Alignment.Center),
            style = TextStyle(fontSize = 16.sp, fontWeight = W500),
            color = MaterialTheme.colors.onBackground
        )

        Box(
            Modifier.clip(CircleShape)
                .clickable(
                    onClick = { scope.next() },
                    enabled = true
                )
        ) {
            Image(
                Icons.Default.ChevronRight,
                modifier = Modifier.padding(end = 24.dp).wrapContentWidth(Alignment.End),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
        }
    }
}

@Composable
private fun DateTitle(selected: MutableState<LocalDate>) {
    val month = selected.value.month.shortLocalName
    val day = selected.value.dayOfWeek.shortLocalName

    Box(backgroundColor = MaterialTheme.colors.primaryVariant, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = selected.value.year.toString(), color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.drawOpacity(0.8f).padding(bottom = 2.dp),
                style = TextStyle(fontSize = 18.sp, fontWeight = W700)
            )
            Text(
                text = "$day, $month ${selected.value.dayOfMonth}",
                color = MaterialTheme.colors.onPrimary,
                style = TextStyle(fontSize = 26.sp, fontWeight = W700)
            )
        }
    }
}

private fun getDates(date: LocalDate): List<Int> {
    val dates = mutableListOf<Int>()

    val firstDate = LocalDate.of(date.year, date.monthValue, 1)
    val firstDay = firstDate.dayOfWeek.value - 1
    val numDays = date.month.length(firstDate.isLeapYear)

    var counter = 1
    while (counter <= numDays) {
        for (x in 0..6) {
            if ((counter == 1 && x != firstDay) || counter > numDays) {
                dates.add(-1)
            } else {
                dates.add(counter)
                counter += 1
            }
        }
    }

    return dates
}
