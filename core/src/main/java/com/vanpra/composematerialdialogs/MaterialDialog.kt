package com.vanpra.composematerialdialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
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
 * @param properties properties of the compose dialog
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
    properties: DialogProperties = DialogProperties(),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(28.dp),
    autoDismiss: Boolean = true,
    onCloseRequest: (MaterialDialogState) -> Unit = { it.hide() },
    buttons: @Composable (MaterialDialogButtons.() -> Unit)?,
    content: @Composable MaterialDialogScope.() -> Unit
) {
    val dialogScope = remember { MaterialDialogScopeImpl(dialogState, autoDismiss) }
    DisposableEffect(dialogState.showing) {
        if (!dialogState.showing) dialogScope.reset()
        onDispose { }
    }

    if (dialogState.showing) {
        Dialog(properties = properties, onDismissRequest = { onCloseRequest(dialogState) }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
                    .testTag("dialog"),
                shape = shape,
                color = backgroundColor,
                tonalElevation = DialogElevation
            ) {
                MaterialDialogLayout(
                    dialogScope = dialogScope,
                    buttons = buttons,
                    content = content
                )
            }
        }
    }
}

@Composable
fun MaterialDialogLayout(
    dialogScope: MaterialDialogScope,
    buttons: @Composable (MaterialDialogButtons.() -> Unit)?,
    content: @Composable MaterialDialogScope.() -> Unit
) {
    val maxHeightPx = with(LocalDensity.current) { MaxDialogHeight.toPx().toInt() }
    val btnInterPaddingPx = with(LocalDensity.current) { DialogButtonInterPadding.toPx().toInt() }
    val btnOuterPaddingPx = with(LocalDensity.current) { DialogButtonOuterPadding.toPx().toInt() }
    val btnMaxHeight = with(LocalDensity.current) { DialogButtonMaxHeight.toPx().toInt() }

    Layout(content = {
        buttons?.invoke(dialogScope.dialogButtons)
        Column {
            content(dialogScope)
        }
    }) { measurables, constraints ->
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

        val totalInterButtonPadding = (buttonPlaceables.size - 1) * btnInterPaddingPx
        val totalBtnWidth = buttonPlaceables.sumOf { it.second.width } + totalInterButtonPadding
        val btnColumn = totalBtnWidth > DialogButtonColumnRatio * constraints.maxWidth

        val btnHeight = if (buttonPlaceables.isEmpty()) {
            0
        } else if (btnColumn) {
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
                    maxHeight = maxHeightPx - btnHeight
                )
            )
        val height = min(contentPlaceable.height + btnHeight, maxHeightPx)

        val positiveBtns = buttonPlaceables.filterButtons(MaterialDialogButtonId.Positive)
        val negativeBtns = buttonPlaceables.filterButtons(MaterialDialogButtonId.Negative)

        layout(constraints.maxWidth, height) {
            contentPlaceable.place(0, 0)

            var buttonX = constraints.maxWidth - btnOuterPaddingPx
            var buttonY = height - btnOuterPaddingPx

            if (btnColumn) {
                (negativeBtns.reversed() + positiveBtns.reversed())
                    .forEach { button ->
                        button.place(buttonX - button.width, buttonY - button.height)
                        buttonY -= button.height + btnInterPaddingPx
                    }
            } else {
                (positiveBtns.reversed() + negativeBtns.reversed())
                    .forEach { button ->
                        buttonX -= button.width + btnInterPaddingPx
                        button.place(buttonX, buttonY - button.height)
                    }
            }
        }
    }
}

private val DialogElevation = 24.dp
private val MaxDialogHeight = 560.dp
val DialogButtonOuterPadding = 24.dp
val DialogButtonInterPadding = 8.dp
val DialogButtonMaxHeight = 34.dp
const val DialogButtonColumnRatio = 0.8