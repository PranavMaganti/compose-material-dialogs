package com.vanpra.composematerialdialogs

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.accompanist.coil.CoilImage
import java.util.concurrent.atomic.AtomicInteger

/**
 * @brief The MaterialDialog class is used to build and display a dialog using both pre-made and
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
    private val onCloseRequest: (MaterialDialog) -> Unit = { it.hide() }
) {
    private val showing: MutableState<Boolean> = mutableStateOf(false)

    val buttons = MaterialDialogButtons(this)
    val callbacks = mutableListOf<() -> Unit>()
    val callbackCounter = AtomicInteger(0)

    var positiveEnabled by mutableStateOf(mutableListOf<Boolean>())
    val positiveEnabledCounter = AtomicInteger(0)
    var positiveButtonEnabledOverride by mutableStateOf(true)

    internal fun setPositiveEnabled(index: Int, value: Boolean) {
        // Have to make temp list in order for state to register change
        synchronized(positiveEnabled) {
            val tempList = positiveEnabled.toMutableList()
            tempList[index] = value
            positiveEnabled = tempList
        }
    }

    /**
     * @brief Shows the dialog
     */
    fun show() {
        showing.value = true
    }

    /**
     * @brief Hides the dialog
     */
    fun hide() {
        showing.value = false
    }

    /**
     * @brief Disables the positive dialog button if present
     */
    fun disablePositiveButton() {
        positiveButtonEnabledOverride = false
    }

    /**
     * @brief Enables the positive dialog button if present
     */
    fun enablePositiveButton() {
        positiveButtonEnabledOverride = true
    }

    /**
     * @brief Checks if autoDismiss is set
     * @return true if autoDismiss is set to true and false otherwise
     */
    fun isAutoDismiss() = autoDismiss

    /**
     * @brief Builds a dialog with the given content
     * @param content the body of the dialog
     */
    @Composable
    fun build(
        backgroundColor: Color = MaterialTheme.colors.background,
        content: @Composable MaterialDialog.() -> Unit
    ) {
        if (showing.value) {
            ThemedDialog(onCloseRequest = { onCloseRequest(this) }) {
                DisposableEffect(Unit) {
                    onDispose {
                        positiveEnabled.clear()
                        callbacks.clear()

                        positiveEnabledCounter.set(0)
                        callbackCounter.set(0)
                    }
                }
                Column(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    this@MaterialDialog.content()
                }
            }
        }
    }

    /**
     * @brief Adds a title with the given text to the dialog
     * @param text title text from a string literal
     * @param res title text from a string resource
     * @param center text is aligned to center when true
     */
    @Composable
    fun MaterialDialog.title(
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
     * @brief Adds a title with the given text and icon to the dialog
     * @param text title text from a string literal
     * @param textRes title text from a string resource
     * @param iconRes icon/image from a drawable resource
     * @param iconAsset an icon/image from a VectorAsset
     * @param assetTint the tint which should be applied to the asset if it is not null
     */
    @Composable
    fun MaterialDialog.iconTitle(
        text: String? = null,
        @StringRes textRes: Int? = null,
        @DrawableRes iconRes: Int? = null,
        iconAsset: ImageVector? = null,
        assetTint: Color = MaterialTheme.colors.onBackground
    ) {
        if (iconAsset == null && iconRes == null) {
            throw IllegalArgumentException("One of iconRes or iconAsset must not be null")
        }
        val titleText = getString(textRes, text)
        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconAsset != null) {
                Image(
                    imageVector = iconAsset,
                    colorFilter = ColorFilter.tint(assetTint),
                    contentDescription = null
                )
            } else {
                CoilImage(
                    data = iconRes!!,
                    modifier = Modifier.size(36.dp),
                    contentDescription = null
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = titleText,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6
            )
        }
    }

    /**
     * @brief Adds paragraph of text to the dialog
     * @param text message text from a string literal
     * @param res message text from a string resource
     */
    @Composable
    fun MaterialDialog.message(text: String? = null, @StringRes res: Int? = null) {
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
     * @brief Adds buttons to the bottom of the dialog
     * @param content the buttons which should be displayed in the dialog.
     * See [MaterialDialogButtons] for more information about the content
     */
    @Composable
    fun MaterialDialog.buttons(content: @Composable MaterialDialogButtons.() -> Unit) {
        buttons.buttonsTagOrder.clear()

        val interButtonPadding = with(LocalDensity.current) { 12.dp.toPx().toInt() }
        val defaultBoxHeight = with(LocalDensity.current) { 36.dp.toPx().toInt() }

        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                .layoutId("buttons")
        ) {
            Layout(
                { content(buttons) }, Modifier,
                { measurables, constraints ->
                    val placeables = measurables.map { it.layoutId to it.measure(constraints) }
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

                        buttons.buttonsTagOrder.forEach { tagNum ->
                            val buttonPlaceable =
                                placeables.firstOrNull { it.first == "button_$tagNum" }

                            if (buttonPlaceable != null) {
                                val button = buttonPlaceable.second
                                currX -= button.width

                                if (!column) {
                                    button.place(currX, 0)
                                } else {
                                    button.place(currX, currY)

                                    currY += button.height + interButtonPadding
                                    currX = constraints.maxWidth
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    /**
     * @brief Adds an input field with the given parameters to the dialog
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
    fun MaterialDialog.input(
        label: String,
        hint: String = "",
        prefill: String = "",
        waitForPositiveButton: Boolean = true,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        keyboardOptions: KeyboardOptions = KeyboardOptions(),
        errorMessage: String = "",
        isTextValid: (String) -> Boolean = { true },
        onInput: (String) -> Unit = {}
    ) {
        var text by remember { mutableStateOf(prefill) }
        val valid = rememberSaveable(text) { isTextValid(text) }

        val positiveEnabledIndex =
            rememberSaveable {
                val index = positiveEnabledCounter.getAndIncrement()
                positiveEnabled.add(index, valid)
                index
            }
        val callbackIndex = rememberSaveable {
            val index = callbackCounter.getAndIncrement()
            if (waitForPositiveButton) {
                callbacks.add(index) { onInput(text) }
            } else {
                callbacks.add(index) { }
            }
            index
        }
        SideEffect {
            setPositiveEnabled(positiveEnabledIndex, valid)
        }

        DisposableEffect(Unit) {
            onDispose {
                callbacks[callbackIndex] = {}
                setPositiveEnabled(positiveEnabledIndex, true)
            }
        }

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
    fun MaterialDialog.customView(content: @Composable () -> Unit) {
        Box(modifier = Modifier.padding(bottom = 28.dp, start = 24.dp, end = 24.dp)) {
            content()
        }
    }
}
