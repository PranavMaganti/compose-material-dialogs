package com.vanpra.composematerialdialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 *  Adds a title with the given text to the dialog
 * @param text title text from a string literal
 * @param res title text from a string resource
 * @param center text is aligned to center when true
 */
@Composable
fun MaterialDialogScope.title(
    text: String? = null,
    @StringRes res: Int? = null
) {
    val titleText = getString(res, text)

    Column {
        Text(
            text = titleText,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)
        )
    }
}

/**
 *  Adds a title with the given text and icon to the dialog
 * @param text title text from a string literal
 * @param textRes title text from a string resource
 * @param icon optional icon displayed at the start of the title
 */
@Composable
fun MaterialDialogScope.iconTitle(
    text: String? = null,
    @StringRes textRes: Int? = null,
    icon: @Composable () -> Unit = {},
    iconContentColor: Color = MaterialTheme.colorScheme.secondary
) {
    val titleText = getString(textRes, text)
    Column(
        modifier = Modifier.padding(
            start = 24.dp,
            end = 24.dp,
            top = 24.dp,
            bottom = 16.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompositionLocalProvider(LocalContentColor provides iconContentColor) {
            icon()
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = titleText,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 *  Adds paragraph of text to the dialog
 * @param text message text from a string literal
 * @param res message text from a string resource
 */
@Composable
fun MaterialDialogScope.message(text: String? = null, @StringRes res: Int? = null) {
    val messageText = getString(res, text)

    Text(
        text = messageText,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 24.dp)
    )
}

/**
 *  Adds a divider to the dialog
 */
@Composable
fun MaterialDialogScope.divider() {
    Box(
        Modifier
            .padding(start = 24.dp, end = 24.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

/**
 *  Adds an input field with the given parameters to the dialog
 * @param label string to be shown in the input field before selection eg. Username
 * @param hint hint to be shown in the input field when it is selected but empty eg. Joe
 * @param prefill string to be input into the text field by default
 * @param waitForPositiveButton if true the [onInput] callback will only be called when the
 * positive button is pressed, otherwise it will be called when the input value is changed
 * @param visualTransformation a visual transformation of the content of the text field
 * @param keyboardOptions software keyboard options which can be used to customize parts
 * of the keyboard
 * @param errorMessage a message to be shown to the user when the input is not valid
 * @param focusRequester a [FocusRequester] which can be used to control the focus state of the
 * text field
 * @param focusOnShow if set to true this will auto focus the text field when the input
 * field is shown
 * @param isTextValid a function which is called to check if the user input is valid
 * @param onInput a function which is called with the user input. The timing of this call is
 * dictated by [waitForPositiveButton]
 */
@Composable
fun MaterialDialogScope.input(
    label: String,
    hint: String = "",
    prefill: String = "",
    waitForPositiveButton: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    errorMessage: String = "",
    focusRequester: FocusRequester = FocusRequester.Default,
    focusOnShow: Boolean = false,
    isTextValid: (String) -> Boolean = { true },
    onInput: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf(prefill) }
    val valid = remember(text) { isTextValid(text) }
    val focusManager = LocalFocusManager.current

    PositiveButtonEnabled(valid = valid) {
        focusManager.clearFocus()
    }

    if (waitForPositiveButton) {
        DialogCallback { onInput(text) }
    }

    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp)) {
        TextField(
            value = text,
            onValueChange = {
                text = it
                if (!waitForPositiveButton) {
                    onInput(text)
                }
            },
            label = { Text(label, color = MaterialTheme.colorScheme.onBackground.copy(0.8f)) },
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .testTag("dialog_input"),
            placeholder = { Text(hint, color = MaterialTheme.colorScheme.onBackground.copy(0.5f)) },
            isError = !valid,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            textStyle = TextStyle(MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
        )

        if (!valid) {
            Text(
                errorMessage,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("dialog_input_error")
            )
        }
    }

    if (focusOnShow) {
        DisposableEffect(Unit) {
            focusRequester.requestFocus()
            onDispose { }
        }
    }
}

/**
 * Create an view in the dialog with the given content and appropriate padding
 * @param content the content of the custom view
 */
@Composable
fun MaterialDialogScope.customView(content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(start = 24.dp, end = 24.dp)) {
        content()
    }
}
