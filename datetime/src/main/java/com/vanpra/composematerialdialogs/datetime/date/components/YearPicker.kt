package com.vanpra.composematerialdialogs.datetime.date

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.PagerState
import com.vanpra.composematerialdialogs.datetime.R
import com.vanpra.composematerialdialogs.datetime.util.getFullLocalName
import java.time.LocalDate
import kotlinx.coroutines.launch

@Composable
internal fun YearSelectorDropdownButton(state: DatePickerState, viewDate: LocalDate) {
    val month = remember { viewDate.month.getFullLocalName(state.locale) }
    val arrowDropUp = painterResource(id = R.drawable.baseline_arrow_drop_up_24)
    val arrowDropDown = painterResource(id = R.drawable.baseline_arrow_drop_down_24)

    Row(
        Modifier
            .height(24.dp)
            .clickable(onClick = { state.yearPickerShowing = !state.yearPickerShowing }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$month ${viewDate.year}",
            style = MaterialTheme.typography.labelLarge,
            color = state.colors.headlineText
        )

        Spacer(Modifier.width(4.dp))
        Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            Icon(
                if (state.yearPickerShowing) arrowDropUp else arrowDropDown,
                contentDescription = "Year Selector"
            )
        }
    }
}

@Composable
internal fun YearPicker(
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
