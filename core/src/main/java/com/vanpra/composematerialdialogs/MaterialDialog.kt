package com.vanpra.composematerialdialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.concurrent.atomic.AtomicInteger

/**
 *  The MaterialDialog class is used to build and display a dialog using both pre-made and
 * custom views
 *
 * @param autoDismiss when true the dialog will be automatically dismissed when a positive or
 * negative button is pressed
 * @param onCloseRequest a callback for when the user tries to exit the dialog by clicking outside
 * the dialog. This callback takes the current MaterialDialog as
 * a parameter to allow for the hide method of the dialog to be called if required. By default
 * this callback hides the dialog.
 */
class MaterialDialog(
    private val autoDismiss: Boolean = true,
    private val onCloseRequest: (MaterialDialog) -> Unit = { it.hide() },
) {
    private val showing: MutableState<Boolean> = mutableStateOf(false)

    val buttons = MaterialDialogButtons(this)

    val callbacks = mutableMapOf<Int, () -> Unit>()
    private val callbackCounter = AtomicInteger(0)

    var positiveEnabled = mutableStateMapOf<Int, Boolean>()
    private val positiveEnabledCounter = AtomicInteger(0)
    var positiveButtonEnabledOverride by mutableStateOf(true)

    /**
     *  Dialog background color with elevation overlay
     */
    var dialogBackgroundColor by mutableStateOf<Color?>(null)

    internal fun setPositiveEnabled(index: Int, value: Boolean) = positiveEnabled[index] = value

    /**
     *  Shows the dialog
     */
    fun show() {
        showing.value = true
    }

    /**
     * Clears focus with a given [FocusManager] and then hides the dialog
     *
     * @param focusManager the focus manager of the dialog view
     */
    fun hide(focusManager: FocusManager? = null) {
        focusManager?.clearFocus()
        showing.value = false
    }

    /**
     * Hides the dialog and calls any callbacks from components in the dialog
     */
    fun submit() {
        hide()
        callbacks.values.forEach {
            it()
        }
    }

    /**
     *  Disables the positive dialog button if present
     */
    fun disablePositiveButton() {
        positiveButtonEnabledOverride = false
    }

    /**
     *  Enables the positive dialog button if present
     */
    fun enablePositiveButton() {
        positiveButtonEnabledOverride = true
    }

    /**
     * Adds a callback to the dialog
     */
    @Composable
    fun DialogCallback(waitForPositiveButton: Boolean, callback: () -> Unit) {
        val callbackIndex = rememberSaveable { callbackCounter.getAndIncrement() }

        DisposableEffect(Unit) {
            callbacks[callbackIndex] = if (waitForPositiveButton) callback else emptyCallback
            onDispose { callbacks[callbackIndex] = {} }
        }
    }

    @Composable
    fun addPositiveButtonEnabled(valid: Boolean, onDispose: () -> Unit = {}): Int {
        val positiveEnabledIndex = remember { positiveEnabledCounter.getAndIncrement() }

        DisposableEffect(Unit) {
            positiveEnabled[positiveEnabledIndex] = valid

            onDispose {
                setPositiveEnabled(positiveEnabledIndex, true)
                onDispose()
            }
        }

        return positiveEnabledIndex
    }

    /**
     *  Checks if autoDismiss is set
     * @return true if autoDismiss is set to true and false otherwise
     */
    fun isAutoDismiss() = autoDismiss

    private fun resetDialog() {
        positiveEnabled.clear()
        callbacks.clear()

        positiveEnabledCounter.set(0)
        callbackCounter.set(0)
    }

    /**
     *  Builds a dialog with the given content
     * @param backgroundColor background color of the dialog
     * @param shape shape of the dialog and components used in the dialog
     * @param border border stoke of the dialog
     * @param elevation elevation of the dialog
     * @param content the body content of the dialog
     */
    @Composable
    fun build(
        backgroundColor: Color = MaterialTheme.colors.surface,
        shape: Shape = MaterialTheme.shapes.medium,
        border: BorderStroke? = null,
        elevation: Dp = 24.dp,
        content: @Composable MaterialDialog.() -> Unit
    ) {
        dialogBackgroundColor = LocalElevationOverlay.current?.apply(
            color = backgroundColor,
            elevation = elevation
        ) ?: MaterialTheme.colors.surface

        if (showing.value) {
            ThemedDialog(onCloseRequest = { onCloseRequest(this) }) {
                DisposableEffect(Unit) {
                    onDispose { resetDialog() }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clipToBounds(),
                    shape = shape,
                    color = backgroundColor,
                    border = border,
                    elevation = elevation
                ) {
                    Column {
                        this@MaterialDialog.content()
                    }
                }
            }
        }
    }

    /**
     *  Adds a title with the given text to the dialog
     * @param text title text from a string literal
     * @param res title text from a string resource
     * @param center text is aligned to center when true
     */
    @Composable
    fun title(
        text: String? = null,
        @StringRes res: Int? = null,
        center: Boolean = false
    ) {
        val titleText = getString(res, text)
        var modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp)
            .height(64.dp)
            .wrapContentHeight(Alignment.CenterVertically)

        modifier = modifier.then(
            Modifier.wrapContentWidth(
                if (center) {
                    Alignment.CenterHorizontally
                } else {
                    Alignment.Start
                }
            )
        )

        Text(
            text = titleText,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.h6,
            modifier = modifier
        )
    }

    /**
     *  Adds a title with the given text and icon to the dialog
     * @param text title text from a string literal
     * @param textRes title text from a string resource
     * @param icon optional icon displayed at the start of the title
     */
    @Composable
    fun iconTitle(
        text: String? = null,
        @StringRes textRes: Int? = null,
        icon: @Composable () -> Unit = {},
    ) {
        val titleText = getString(textRes, text)
        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(Modifier.width(14.dp))
            Text(
                text = titleText,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h6
            )
        }
    }

    /**
     *  Adds paragraph of text to the dialog
     * @param text message text from a string literal
     * @param res message text from a string resource
     */
    @Composable
    fun message(text: String? = null, @StringRes res: Int? = null) {
        val messageText = getString(res, text)

        Text(
            text = messageText,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .padding(bottom = 28.dp, start = 24.dp, end = 24.dp)
        )
    }

    /**
     *  Adds buttons to the bottom of the dialog
     * @param content the buttons which should be displayed in the dialog.
     * See [MaterialDialogButtons] for more information about the content
     */
    @Composable
    fun buttons(content: @Composable MaterialDialogButtons.() -> Unit) {
        val interButtonPadding = with(LocalDensity.current) { 12.dp.toPx().toInt() }
        val defaultBoxHeight = with(LocalDensity.current) { 36.dp.toPx().toInt() }
        val accessibilityPadding = with(LocalDensity.current) { 12.dp.toPx().toInt() }

        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                .layoutId("buttons")
        ) {
            Layout(
                { content(buttons) }, Modifier,
                { measurables, constraints ->
                    val placeables = measurables.map {
                        (it.layoutId as MaterialDialogButtonTypes) to it.measure(constraints)
                    }
                    val totalWidth = placeables.map { it.second.width }.sum()
                    val column = totalWidth > 0.8 * constraints.maxWidth

                    val height =
                        if (column) {
                            val buttonHeight = placeables.map { it.second.height }.sum()
                            val heightPadding = (placeables.size - 1) * interButtonPadding
                            buttonHeight + heightPadding
                        } else {
                            defaultBoxHeight
                        }

                    layout(constraints.maxWidth, height) {
                        var currX = constraints.maxWidth
                        var currY = 0

                        val posButtons = placeables.buttons(MaterialDialogButtonTypes.Positive)
                        val negButtons = placeables.buttons(MaterialDialogButtonTypes.Negative)
                        val textButtons = placeables.buttons(MaterialDialogButtonTypes.Text)
                        val accButtons = placeables.buttons(MaterialDialogButtonTypes.Accessibility)

                        val buttonInOrder = posButtons + textButtons + negButtons
                        buttonInOrder.forEach { button ->
                            if (column) {
                                button.place(currX - button.width, currY)
                                currY += button.height + interButtonPadding
                            } else {
                                currX -= button.width
                                button.place(currX, 0)
                            }
                        }

                        if (accButtons.isNotEmpty()) {
                            /* There can only be one accessibility button so take first */
                            val button = accButtons[0]
                            button.place(accessibilityPadding, height - button.height)
                        }
                    }
                }
            )
        }
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
     * @param isTextValid a function which is called to check if the user input is valid
     * @param onInput a function which is called with the user input. The timing of this call is
     * dictated by [waitForPositiveButton]
     */
    @Composable
    fun input(
        label: String,
        hint: String = "",
        prefill: String = "",
        waitForPositiveButton: Boolean = true,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        keyboardOptions: KeyboardOptions = KeyboardOptions(),
        keyboardActions: KeyboardActions = KeyboardActions(),
        errorMessage: String = "",
        isTextValid: (String) -> Boolean = { true },
        onInput: (String) -> Unit = {}
    ) {
        var text by remember { mutableStateOf(prefill) }
        val valid = remember(text) { isTextValid(text) }
        val focusManager = LocalFocusManager.current

        val positiveEnabledIndex = addPositiveButtonEnabled(valid = valid) {
            focusManager.clearFocus()
        }

        DisposableEffect(valid) {
            setPositiveEnabled(positiveEnabledIndex, valid)
            onDispose { }
        }

        DialogCallback(waitForPositiveButton = waitForPositiveButton) { onInput(text) }

        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)) {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    if (!waitForPositiveButton) {
                        onInput(text)
                    }
                },
                label = { Text(label, color = MaterialTheme.colors.onBackground.copy(0.8f)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(hint, color = MaterialTheme.colors.onBackground.copy(0.5f)) },
                isError = !valid,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                textStyle = TextStyle(MaterialTheme.colors.onBackground, fontSize = 16.sp)
            )

            if (!valid) {
                Text(
                    errorMessage,
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }

    /**
     * Create an view in the dialog with the given content and appropriate padding
     * @param content the content of the custom view
     */
    @Composable
    fun customView(content: @Composable () -> Unit) {
        Box(modifier = Modifier.padding(bottom = 28.dp, start = 24.dp, end = 24.dp)) {
            content()
        }
    }
}
