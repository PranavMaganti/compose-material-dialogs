package com.vanpra.composematerialdialogdemos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogButtons
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.MaterialTimePickerDialog
import com.vanpra.composematerialdialogs.datetime.time.TimePickerColors
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.rememberTimePickerOptions
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalTime

/**
 * @brief Builds a dialog and adds button to the layout which shows the dialog on click
 */
@Composable
fun DialogAndShowButton(
    buttonText: String,
    buttons: @Composable MaterialDialogButtons.() -> Unit = {},
    content: @Composable MaterialDialogScope.() -> Unit
) {
    val dialogState = rememberMaterialDialogState()

    MaterialDialog(state = dialogState, buttons = buttons) {
        content()
    }

    DialogDemoButton(state = dialogState, buttonText = buttonText)
}

@Composable
fun TimePickerDialogAndShowButton(
    buttonText: String,
    buttons: @Composable MaterialDialogButtons.() -> Unit = {},
    initialTime: LocalTime = LocalTime.now(),
    colors: TimePickerColors = TimePickerDefaults.colors(),
    timeRange: ClosedRange<LocalTime> = LocalTime.MIN..LocalTime.MAX,
    is24HourClock: Boolean = false,
    onTimeChange: (LocalTime) -> Unit = {}
) {
    val dialogState = rememberMaterialDialogState()

    MaterialTimePickerDialog(
        state = dialogState,
        buttons = buttons,
        timePickerOptions = rememberTimePickerOptions(
            initialTime = initialTime,
            colors = colors,
            timeRange = timeRange,
            is24HourClock = is24HourClock,
            onTimeChange = onTimeChange
        )
    )
    DialogDemoButton(state = dialogState, buttonText = buttonText)
}

@Composable
fun DialogDemoButton(state: MaterialDialogState, buttonText: String) {
    TextButton(
        onClick = { state.show() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            buttonText,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

/**
 * @brief Add title to top of layout
 */
@Composable
fun DialogSection(title: String, content: @Composable () -> Unit) {
    Text(
        title,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
    )

    content()
}
