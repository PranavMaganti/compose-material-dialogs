package com.vanpra.composematerialdialogs.datetime.time

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.datetime.util.VerticalDivider
import com.vanpra.composematerialdialogs.datetime.util.isAM
import com.vanpra.composematerialdialogs.datetime.util.isNumeric
import com.vanpra.composematerialdialogs.datetime.util.toAM
import com.vanpra.composematerialdialogs.datetime.util.toPM

@Composable
internal fun VerticalTimeLayout(
    modifier: Modifier = Modifier,
    state: TimePickerState
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .height(80.dp)
            .fillMaxWidth()
    ) {
        ClockDisplay(state = state)

        if (!state.is24Hour) {
            Spacer(modifier = Modifier.width(12.dp))
            VerticalPeriodPicker(state = state)
        }
    }
}

@Composable
internal fun HorizontalTimeLayout(
    modifier: Modifier = Modifier,
    state: TimePickerState
) {
    Column(modifier = modifier) {
        ClockDisplay(state = state)

        if (!state.is24Hour) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalPeriodPicker(state = state)
        }
    }
}

@Composable
internal fun ClockDisplay(state: TimePickerState) {
    val hourFocusRequester = remember { FocusRequester() }
    val minuteFocusRequester = remember { FocusRequester() }

    DisposableEffect(state.entryMode, state.currentScreen) {
        if (state.entryMode == TimePickerEntryMode.Text) {
            when (state.currentScreen) {
                ClockScreen.Hour -> hourFocusRequester.requestFocus()
                ClockScreen.Minute -> minuteFocusRequester.requestFocus()
            }
        }
        onDispose { }
    }

    Row(Modifier.height(80.dp)) {
        ClockLabel(
            state = state,
            text = state.getHour().toString().padStart(2, '0'),
            active = state.currentScreen == ClockScreen.Hour,
            focusRequester = hourFocusRequester,
            onClick = { state.currentScreen = ClockScreen.Hour },
            onValueChange = {
                state.selectedTime = state.selectedTime.withHour(it).coerceIn(state.timeRange)
            },
            isValueValid = { if (state.is24Hour) it in 0..23 else it in 1..12 },
            isInputOnly = { it == 0 }
        )

        Box(
            Modifier
                .width(24.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ":",
                color = state.colors.timeSelectorSeparator(),
                style = MaterialTheme.typography.displayLarge
            )
        }

        ClockLabel(
            state = state,
            text = state.getMinute().toString().padStart(2, '0'),
            active = state.currentScreen == ClockScreen.Minute,
            focusRequester = minuteFocusRequester,
            onClick = { state.currentScreen = ClockScreen.Minute },
            onValueChange = {
                state.selectedTime = state.selectedTime.withMinute(
                    it
                ).coerceIn(state.timeRange)
            },
            isValueValid = { it in 0..59 }
        )
    }
}

@Composable
internal fun ClockLabel(
    text: String,
    state: TimePickerState,
    active: Boolean,
    focusRequester: FocusRequester,
    onClick: () -> Unit,
    onValueChange: (Int) -> Unit,
    isValueValid: (Int) -> Boolean,
    isInputOnly: (Int) -> Boolean = { false }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val inputText = remember(state.entryMode) { mutableStateOf("") }
    val textFieldSelected = remember(state.entryMode, active) {
        state.entryMode == TimePickerEntryMode.Text && active
    }

    DisposableEffect(active) {
        if (!active && inputText.value.isNotEmpty() && isInputOnly(inputText.value.toInt())) {
            inputText.value = text
        }
        onDispose { }
    }

    Surface(
        modifier = Modifier
            .size(height = 80.dp, width = if (state.is24Hour) 114.dp else 96.dp)
            .clickable(onClick = onClick, indication = null, interactionSource = interactionSource),
        border = if (state.entryMode == TimePickerEntryMode.Text && active) {
            BorderStroke(
                2.dp,
                state.colors.entryTimeSelectorBorder()
            )
        } else {
            null
        },
        shape = MaterialTheme.shapes.small,
        color = state.colors.timeSelectorContainer(active).value
    ) {
        Box(contentAlignment = Alignment.Center) {
            BasicTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { if (it.isFocused) onClick() },
                value = if (textFieldSelected) inputText.value else text,
                onValueChange = {
                    if (it.length > 2 || !it.isNumeric()) return@BasicTextField

                    val value = if (it.isEmpty()) 0 else it.toInt()
                    println(value)
                    if (isValueValid(value)) {
                        inputText.value = it
                        onValueChange(value)
                    } else if (isInputOnly(value)) {
                        inputText.value = it
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.displayLarge.copy(
                    color = state.colors.timeSelectorText(active).value,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(state.colors.timeSelectorText(active).value),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                interactionSource = interactionSource,
                enabled = textFieldSelected
            )
        }
    }
}

@Composable
private fun HorizontalPeriodPicker(state: TimePickerState) {
    val isAMEnabled = remember(state.timeRange) { state.timeRange.start.hour <= 12 }
    val isPMEnabled = remember(state.timeRange) { state.timeRange.endInclusive.hour >= 0 }

    Row(
        Modifier
            .size(width = 216.dp, height = 36.dp)
            .clip(MaterialTheme.shapes.small)
            .border(
                width = 1.dp,
                color = state.colors.periodContainerOutline(),
                shape = MaterialTheme.shapes.small
            )
    ) {
        Box(
            modifier = Modifier
                .width(108.dp)
                .fillMaxHeight()
                .background(state.colors.periodContainer(state.selectedTime.isAM).value)
                .clickable(
                    onClick = {
                        state.selectedTime = state.selectedTime
                            .toAM()
                            .coerceIn(state.timeRange)
                        state.currentScreen = ClockScreen.Hour
                    },
                    enabled = isAMEnabled
                ),
            contentAlignment = Alignment.Center
        ) {
            var color = state.colors.periodText(state.selectedTime.isAM).value
            if (!isAMEnabled) {
                color = color.copy(alpha = TimePickerConstants.DisabledAlpha)
            }
            Text(
                "AM",
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }

        VerticalDivider(thickness = 1.dp, color = state.colors.periodContainerOutline())

        Box(
            modifier = Modifier
                .width(108.dp)
                .fillMaxHeight()
                .background(state.colors.periodContainer(!state.selectedTime.isAM).value)
                .clickable(
                    onClick = {
                        state.selectedTime = state.selectedTime
                            .toPM()
                            .coerceIn(state.timeRange)
                        state.currentScreen = ClockScreen.Hour
                    },
                    enabled = isPMEnabled
                ),
            contentAlignment = Alignment.Center
        ) {
            var color = state.colors.periodText(!state.selectedTime.isAM).value
            if (!isPMEnabled) {
                color = color.copy(alpha = TimePickerConstants.DisabledAlpha)
            }
            Text(
                "PM",
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }
    }
}

@Composable
private fun VerticalPeriodPicker(state: TimePickerState) {
    val isAMEnabled = remember(state.timeRange) { state.timeRange.start.hour <= 12 }
    val isPMEnabled = remember(state.timeRange) { state.timeRange.endInclusive.hour >= 0 }

    Column(
        Modifier
            .size(width = 52.dp, height = 80.dp)
            .clip(MaterialTheme.shapes.small)
            .border(
                width = 1.dp,
                color = state.colors.periodContainerOutline(),
                shape = MaterialTheme.shapes.small
            )
    ) {
        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .background(state.colors.periodContainer(state.selectedTime.isAM).value)
                .clickable(
                    onClick = {
                        state.selectedTime = state.selectedTime
                            .toAM()
                            .coerceIn(state.timeRange)
                        state.currentScreen = ClockScreen.Hour
                    },
                    enabled = isAMEnabled
                ),
            contentAlignment = Alignment.Center
        ) {
            var color = state.colors.periodText(state.selectedTime.isAM).value
            if (!isAMEnabled) {
                color = color.copy(alpha = TimePickerConstants.DisabledAlpha)
            }
            Text(
                "AM",
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }

        Divider(thickness = 2.dp, color = state.colors.periodContainerOutline())

        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .background(state.colors.periodContainer(!state.selectedTime.isAM).value)
                .clickable(
                    onClick = {
                        state.selectedTime = state.selectedTime
                            .toPM()
                            .coerceIn(state.timeRange)
                        state.currentScreen = ClockScreen.Hour
                    },
                    enabled = isPMEnabled
                ),
            contentAlignment = Alignment.Center
        ) {
            var color = state.colors.periodText(!state.selectedTime.isAM).value
            if (!isPMEnabled) {
                color = color.copy(alpha = TimePickerConstants.DisabledAlpha)
            }
            Text(
                "PM",
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }
    }
}
