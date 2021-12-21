package com.vanpra.composematerialdialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    override val dialogState: MaterialDialogState
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
            onDispose { onDispose() }
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

        DisposableEffect(callback) {
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
 * @param state state of the dialog
 * @param properties properties of the compose dialog
 * @param backgroundColor background color of the dialog
 * @param shape shape of the dialog and components used in the dialog
 * @param border border stoke of the dialog
 * @param elevation elevation of the dialog
 * @param onCloseRequest function to be executed when user clicks outside dialog
 * @param buttons the buttons layout of the dialog
 * @param content the body content of the dialog
 */
@Composable
fun MaterialDialog(
    state: MaterialDialogState = rememberMaterialDialogState(),
    properties: DialogProperties = DialogProperties(),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(28.dp),
    onCloseRequest: (MaterialDialogState) -> Unit = { it.hide() },
    buttons: @Composable (MaterialDialogButtons.() -> Unit) = {},
    content: @Composable MaterialDialogScope.() -> Unit
) {
    val dialogScope = remember { MaterialDialogScopeImpl(state) }
    DisposableEffect(state.showing) {
        if (!state.showing) dialogScope.reset()
        onDispose { }
    }

    if (state.showing) {
        Dialog(properties = properties, onDismissRequest = { onCloseRequest(state) }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
                    .testTag("dialog"),
                shape = shape,
                color = backgroundColor,
                tonalElevation = MaterialDialogConstants.Elevation
            ) {
                MaterialDialogLayout(
                    scope = dialogScope,
                    buttons = buttons,
                    content = content
                )
            }
        }
    }
}

@Composable
fun MaterialDialogLayout(
    scope: MaterialDialogScope,
    buttons: @Composable (MaterialDialogButtons.() -> Unit),
    content: @Composable MaterialDialogScope.() -> Unit
) {
    val maxDimenPx = with(LocalDensity.current) { MaterialDialogConstants.MaxDimen.roundToPx() }
    val btnInterPaddingPx =
        with(LocalDensity.current) { MaterialDialogButtonConstants.InterButtonPadding.roundToPx() }
    val btnOuterPaddingPx =
        with(LocalDensity.current) { MaterialDialogButtonConstants.OuterButtonPadding.roundToPx() }
    val btnMaxHeight =
        with(LocalDensity.current) { MaterialDialogButtonConstants.MaxHeight.roundToPx() }

    Layout(content = {
        buttons(scope.dialogButtons)
        Column {
            content(scope)
        }
    }) { measurables, constraints ->
        val width = min(constraints.maxWidth, maxDimenPx)

        val partitionedMeasureables =
            measurables.partition { it.layoutId in MaterialDialogButtonId.Ids }

        val buttonMeasureables = partitionedMeasureables.first
        val contentMeasureable = partitionedMeasureables.second[0] // Always have a single column

        val buttonPlaceables = buttonMeasureables
            .map {
                it.layoutId to it.measure(
                    constraints.copy(
                        minWidth = 0,
                        minHeight = 0,
                        maxHeight = btnMaxHeight
                    )
                )
            }
        val positiveBtns = buttonPlaceables.filterButtons(MaterialDialogButtonId.Positive)
        val negativeBtns = buttonPlaceables.filterButtons(MaterialDialogButtonId.Negative)

        val totalInterButtonPadding = (buttonPlaceables.size - 1) * btnInterPaddingPx
        val totalBtnWidth = buttonPlaceables.sumOf { it.second.width } + totalInterButtonPadding
        val stackBtns = totalBtnWidth > MaterialDialogButtonConstants.ButtonStackThreshold * width

        val btnHeight = if (buttonPlaceables.isEmpty()) {
            0
        } else if (stackBtns) {
            val buttonHeight = buttonPlaceables.sumOf { it.second.height }
            buttonHeight + totalInterButtonPadding + 2 * btnOuterPaddingPx
        } else {
            buttonPlaceables[0].second.height + 2 * btnOuterPaddingPx
        }

        val contentPlaceable =
            contentMeasureable.measure(
                constraints.copy(
                    minWidth = 0,
                    minHeight = 0,
                    maxHeight = maxDimenPx - btnHeight
                )
            )

        val height = min(contentPlaceable.height + btnHeight, maxDimenPx)

        layout(width, height) {
            contentPlaceable.place(0, 0)

            if (buttonPlaceables.isNotEmpty()) {
                var buttonX = constraints.maxWidth - btnOuterPaddingPx
                var buttonY = height - btnOuterPaddingPx

                if (stackBtns) {
                    (positiveBtns + negativeBtns)
                        .reversed()
                        .forEach { button ->
                            button.place(buttonX - button.width, buttonY - button.height)
                            buttonY -= button.height + btnInterPaddingPx
                        }
                } else {
                    (negativeBtns + positiveBtns)
                        .reversed()
                        .forEach { button ->
                            buttonX -= button.width + btnInterPaddingPx
                            button.place(buttonX, buttonY - button.height)
                        }
                }
            }
        }
    }
}

object MaterialDialogConstants {
    val Elevation = 24.dp
    val MaxDimen = 560.dp
}
