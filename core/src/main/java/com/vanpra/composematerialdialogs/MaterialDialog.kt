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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
 *  Interface defining values and functions which are available to any code
 *  within a [MaterialDialog]'s content parameter
 */
interface MaterialDialogScope {
    val dialogState: MaterialDialogState
    val dialogButtons: MaterialDialogButtons

    val callbacks: SnapshotStateMap<Int, () -> Unit>
    val positiveButtonEnabled: SnapshotStateMap<Int, Boolean>

    val autoDismiss: Boolean

    /**
     * Hides the dialog and calls any callbacks from components in the dialog
     */
    fun submit()

    /**
     * Clears the dialog's state
     */
    fun reset()

    /**
     * Adds a value to the [positiveButtonEnabled] map and updates the value in the map when
     * [valid] changes
     *
     * @param valid boolean value to initialise the index in the list
     * @param onDispose cleanup callback when component calling this gets destroyed
     */
    @Composable
    fun PositiveButtonEnabled(valid: Boolean, onDispose: () -> Unit)

    /**
     * Adds a callback to the dialog which is called on positive button press
     *
     * @param callback called when positive button is pressed
     */
    @Composable
    fun DialogCallback(callback: () -> Unit)
}

internal class MaterialDialogScopeImpl(
    override val dialogState: MaterialDialogState,
    override val autoDismiss: Boolean = true
) : MaterialDialogScope {
    override val dialogButtons = MaterialDialogButtons(this)

    override val callbacks = mutableStateMapOf<Int, () -> Unit>()
    private val callbackCounter = AtomicInteger(0)

    override val positiveButtonEnabled = mutableStateMapOf<Int, Boolean>()
    private val positiveEnabledCounter = AtomicInteger(0)

    /**
     * Hides the dialog and calls any callbacks from components in the dialog
     */
    override fun submit() {
        dialogState.hide()
        callbacks.values.forEach {
            it()
        }
    }

    /**
     * Clears the dialog callbacks and positive button enables values along with their
     * respective counters
     */
    override fun reset() {
        positiveButtonEnabled.clear()
        callbacks.clear()

        positiveEnabledCounter.set(0)
        callbackCounter.set(0)
    }

    /**
     * Adds a value to the [positiveButtonEnabled] map and updates the value in the map when
     * [valid] changes
     *
     * @param valid boolean value to initialise the index in the list
     * @param onDispose cleanup callback when component calling this gets destroyed
     */
    @Composable
    override fun PositiveButtonEnabled(valid: Boolean, onDispose: () -> Unit) {
        val positiveEnabledIndex = remember { positiveEnabledCounter.getAndIncrement() }

        DisposableEffect(valid) {
            positiveButtonEnabled[positiveEnabledIndex] = valid
            onDispose {
                positiveButtonEnabled[positiveEnabledIndex] = true
                onDispose()
            }
        }
    }

    /**
     * Adds a callback to the dialog which is called on positive button press
     *
     * @param callback called when positive button is pressed
     */
    @Composable
    override fun DialogCallback(callback: () -> Unit) {
        val callbackIndex = rememberSaveable { callbackCounter.getAndIncrement() }

        DisposableEffect(Unit) {
            callbacks[callbackIndex] = callback
            onDispose { callbacks[callbackIndex] = {} }
        }
    }
}

/**
 *  The [MaterialDialogState] class is used to store the state for a [MaterialDialog]
 *
 * @param initialValue the initial showing state of the dialog
 */
class MaterialDialogState(initialValue: Boolean = false) {
    var showing by mutableStateOf(initialValue)

    /**
     *  Dialog background color with elevation overlay
     */
    var dialogBackgroundColor by mutableStateOf<Color?>(null)

    /**
     *  Shows the dialog
     */
    fun show() {
        showing = true
    }

    /**
     * Clears focus with a given [FocusManager] and then hides the dialog
     *
     * @param focusManager the focus manager of the dialog view
     */
    fun hide(focusManager: FocusManager? = null) {
        focusManager?.clearFocus()
        showing = false
    }

    companion object {
        /**
         * The default [Saver] implementation for [MaterialDialogState].
         */
        fun Saver(): Saver<MaterialDialogState, *> = Saver(
            save = { it.showing },
            restore = { MaterialDialogState(it) }
        )
    }
}

/**
 * Create and [remember] a [MaterialDialogState].
 *
 * @param initialValue the initial showing state of the dialog
 */
@Composable
fun rememberMaterialDialogState(initialValue: Boolean = false): MaterialDialogState {
    return rememberSaveable(saver = MaterialDialogState.Saver()) {
        MaterialDialogState(initialValue)
    }
}

/**
 *  Builds a dialog with the given content
 * @param dialogState state of the dialog
 * @param backgroundColor background color of the dialog
 * @param shape shape of the dialog and components used in the dialog
 * @param border border stoke of the dialog
 * @param elevation elevation of the dialog
 * @param autoDismiss when true the dialog is hidden on any button press
 * @param onCloseRequest function to be executed when user clicks outside dialog
 * @param buttons the buttons layout of the dialog
 * @param content the body content of the dialog
 */
@Composable
fun MaterialDialog(
    dialogState: MaterialDialogState = rememberMaterialDialogState(),
    backgroundColor: Color = MaterialTheme.colors.surface,
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke? = null,
    elevation: Dp = 24.dp,
    autoDismiss: Boolean = true,
    onCloseRequest: (MaterialDialogState) -> Unit = { it.hide() },
    buttons: @Composable MaterialDialogButtons.() -> Unit = {},
    content: @Composable MaterialDialogScope.() -> Unit
) {
    val dialogScope = remember { MaterialDialogScopeImpl(dialogState, autoDismiss) }
    DisposableEffect(dialogState.showing) {
        if (!dialogState.showing) dialogScope.reset()
        onDispose { }
    }

    BoxWithConstraints {
        val maxHeight = if (isLargeDevice()) {
            LocalConfiguration.current.screenHeightDp.dp - 96.dp
        } else {
            560.dp
        }

        val maxHeightPx = with(LocalDensity.current) { maxHeight.toPx().toInt() }
        val isDialogFullWidth = LocalConfiguration.current.screenWidthDp.dp == maxWidth
        val padding = if (isDialogFullWidth) 16.dp else 0.dp

        if (dialogState.showing) {
            dialogState.dialogBackgroundColor = LocalElevationOverlay.current?.apply(
                color = backgroundColor,
                elevation = elevation
            ) ?: MaterialTheme.colors.surface

            Dialog(onDismissRequest = { onCloseRequest(dialogState) }) {
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
                            dialogScope.DialogButtonsLayout(
                                modifier = Modifier.layoutId("buttons"),
                                content = buttons
                            )
                            Column(Modifier.layoutId("content")) { content(dialogScope) }
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
                                minHeight = 0,
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
