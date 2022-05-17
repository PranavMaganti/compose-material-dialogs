package com.vanpra.composematerialdialogs

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.MaterialDialogScope

enum class TextFieldStyle {
    Filled,
    Outlined
}

/**
 * Adds an input field with the given parameters to the dialog
 * @param label string to be shown in the input field before selection eg. Username
 * @param placeholder hint to be shown in the input field when it is selected but empty eg. Joe
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
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String = "",
    prefill: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    textFieldStyle: TextFieldStyle = TextFieldStyle.Filled,
    waitForPositiveButton: Boolean = true,
    errorMessage: String = "",
    focusRequester: FocusRequester = FocusRequester.Default,
    focusOnShow: Boolean = false,
    isTextValid: (String) -> Boolean = { true },
    onInput: (String) -> Unit = {}

) {
    var text by remember { mutableStateOf(prefill) }
    val valid = remember(text) { isTextValid(text) }

    PositiveButtonEnabled(valid = valid, onDispose = {})

    if (waitForPositiveButton) {
        DialogCallback { onInput(text) }
    }

    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)) {
        TextFieldWithStyle(
            modifier = modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .testTag("dialog_input"),
            value = text,
            onValueChange = {
                text = it
                if (!waitForPositiveButton) {
                    onInput(text)
                }
            },
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = { Text(label, color = MaterialTheme.colors.onBackground.copy(0.8f)) },
            placeholder = { Text(placeholder, color = MaterialTheme.colors.onBackground.copy(0.5f)) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = !valid,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            interactionSource = interactionSource,
            colors = colors,
            style = textFieldStyle
        )

        if (!valid) {
            Text(
                errorMessage,
                fontSize = 14.sp,
                color = MaterialTheme.colors.error,
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

@Composable
private fun TextFieldWithStyle(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    style: TextFieldStyle = TextFieldStyle.Filled
) {
    when (style) {
        TextFieldStyle.Filled -> {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                interactionSource = interactionSource,
                colors = colors
            )
        }

        TextFieldStyle.Outlined -> {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                interactionSource = interactionSource,
                colors = colors
            )
        }
    }
}
