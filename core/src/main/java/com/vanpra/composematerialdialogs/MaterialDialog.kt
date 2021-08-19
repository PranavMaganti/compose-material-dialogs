package com.vanpra.composematerialdialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

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

    val dialogButtons = MaterialDialogButtons(this)

    val callbacks = mutableMapOf<Int, () -> Unit>()
    private val callbackCounter = AtomicInteger(0)

    var positiveEnabled = mutableStateMapOf<Int, Boolean>()
    private val positiveEnabledCounter = AtomicInteger(0)
    var positiveButtonEnabledOverride by mutableStateOf(true)

    /**
     *  Dialog background color with elevation overlay
     */
    var dialogBackgroundColor by mutableStateOf<Color?>(null)

    internal fun setPositiveEnabled(index: Int, value: Boolean) {
        positiveEnabled[index] = value
    }

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
     * Adds a callback to the dialog which is called on positive button press
     *
     * @param callback called when positive button is pressed
     */
    @Composable
    fun DialogCallback(callback: () -> Unit) {
        val callbackIndex = rememberSaveable { callbackCounter.getAndIncrement() }

        DisposableEffect(Unit) {
            callbacks[callbackIndex] = callback
            onDispose { callbacks[callbackIndex] = {} }
        }
    }

    /**
     * Adds a value to the [positiveEnabled] list and returns the index used to store the boolean
     *
     * @param valid boolean value to initialise the index in the list
     * @param onDispose cleanup callback when component calling this gets destroyed
     */
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
     *
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
    @Deprecated(
        "Use instead the rememberMaterialDialog function.",
        replaceWith = ReplaceWith("rememberMaterialDialog(backgroundColor, shape, " +
                "border, elevation, buttons, content)")
    )
    @Composable
    fun build(
        backgroundColor: Color = MaterialTheme.colors.surface,
        shape: Shape = MaterialTheme.shapes.medium,
        border: BorderStroke? = null,
        elevation: Dp = 24.dp,
        buttons: @Composable MaterialDialogButtons.() -> Unit = {},
        content: @Composable MaterialDialog.() -> Unit
    ) {
        BoxWithConstraints {
            val maxHeight = if (isLargeDevice()) {
                LocalConfiguration.current.screenHeightDp.dp - 90.dp
            } else {
                560.dp
            }

            val maxHeightPx = with(LocalDensity.current) { maxHeight.toPx().toInt() }
            val isDialogFullWidth = LocalConfiguration.current.screenWidthDp.dp == maxWidth
            val padding = if (isDialogFullWidth) 16.dp else 0.dp

            if (showing.value) {
                dialogBackgroundColor = LocalElevationOverlay.current?.apply(
                    color = backgroundColor,
                    elevation = elevation
                ) ?: MaterialTheme.colors.surface

                Dialog(onDismissRequest = { onCloseRequest(this@MaterialDialog) }) {
                DisposableEffect(Unit) {
                    onDispose { resetDialog() }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(maxHeight = maxHeight, maxWidth = 560.dp)
                        .padding(horizontal = padding)
                        .clipToBounds()
                        .wrapContentHeight()
                        .testTag("dialog"),
                    shape = shape,
                    color = backgroundColor,
                    border = border,
                    elevation = elevation
                ) {
                    Layout(
                        content = {
                            DialogButtons(
                                modifier = Modifier.layoutId("buttons"),
                                dialogButtons = dialogButtons,
                                dialog = this@MaterialDialog,
                                content = buttons
                            )
                            Column(Modifier.layoutId("content")) { content() }
                        }
                    ) { measurables, constraints ->
                        val buttonsHeight =
                            measurables[0].minIntrinsicHeight(constraints.maxWidth)
                        val buttonsPlaceable = measurables[0].measure(
                            constraints.copy(maxHeight = buttonsHeight, minHeight = 0)
                        )

                        val contentPlaceable = measurables[1].measure(
                            constraints.copy(
                                maxHeight = maxHeightPx - buttonsPlaceable.height,
                                minHeight = 0
                            )
                        )

                        val height =
                            min(maxHeightPx, buttonsPlaceable.height + contentPlaceable.height)

                        return@Layout layout(constraints.maxWidth, height) {
                            contentPlaceable.place(0, 0)
                            buttonsPlaceable.place(0, height - buttonsPlaceable.height)
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
fun rememberMaterialDialog(
    backgroundColor: Color = MaterialTheme.colors.surface,
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke? = null,
    elevation: Dp = 24.dp,
    buttons: @Composable MaterialDialogButtons.() -> Unit = {},
    content: @Composable MaterialDialog.() -> Unit,
) = remember { MaterialDialog() }.apply {
    build(backgroundColor, shape, border, elevation, buttons, content)
}
