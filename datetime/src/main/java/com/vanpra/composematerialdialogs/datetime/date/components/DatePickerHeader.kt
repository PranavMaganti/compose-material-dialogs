package com.vanpra.composematerialdialogs.datetime.date

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.datetime.util.getShortLocalName

@Composable
internal fun DatePickerHeader(title: String, state: DatePickerState) {
    val month = remember(state.selected) { state.selected.month.getShortLocalName(state.locale) }
    val day = remember(state.selected) { state.selected.dayOfWeek.getShortLocalName(state.locale) }

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column {
            Text(
                text = title,
                color = state.colors.titleText,
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
                    color = state.colors.headlineText,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    }
}
