package com.vanpra.composematerialdialogs

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.id
import androidx.ui.core.layoutId
import androidx.ui.foundation.Box
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.input.VisualTransformation
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.size
import androidx.ui.layout.width
import androidx.ui.layout.wrapContentHeight
import androidx.ui.layout.wrapContentWidth
import androidx.ui.material.FilledTextField
import androidx.ui.material.MaterialTheme
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import androidx.ui.util.fastFirstOrNull
import dev.chrisbanes.accompanist.coil.CoilImage

/**
 * @brief The MaterialDialog class is used to build and display a dialog using both pre-made and
 * custom views
 *
 * @param autoDismiss when true the dialog will be automatically dismissed when a positive or
 * negative button is pressed
 */
class MaterialDialog(private val autoDismiss: Boolean = true) {
    private val showing: MutableState<Boolean> = mutableStateOf(false)

    val buttons = MaterialDialogButtons(this)
    val callbacks = mutableListOf<() -> Unit>()
    val positiveEnabled = mutableStateOf(mutableListOf<Boolean>())

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
        content: @Composable() MaterialDialog.() -> Unit
    ) {
        if (showing.value) {
            ThemedDialog(onCloseRequest = { hide() }) {
                Box(
                    Modifier.fillMaxWidth(),
                    backgroundColor = backgroundColor,
                    shape = MaterialTheme.shapes.medium
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
        val titleText = ContextAmbient.current.getString(res, text)
        var modifier = Modifier.fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp)
            .preferredHeight(64.dp)
            .wrapContentHeight(Alignment.CenterVertically)

        if (center) {
            modifier = modifier.plus(Modifier.wrapContentWidth(Alignment.CenterHorizontally))
        }

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
        iconAsset: VectorAsset? = null,
        assetTint: Color = MaterialTheme.colors.onBackground
    ) {
        if (iconAsset == null && iconRes == null) {
            throw IllegalArgumentException("One of iconRes or iconAsset must not be null")
        }
        val titleText = ContextAmbient.current.getString(textRes, text)
        Row(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp).preferredHeight(64.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            if (iconAsset != null) {
                Image(
                    asset = iconAsset,
                    colorFilter = ColorFilter.tint(assetTint)
                )
            } else {
                CoilImage(
                    data = iconRes!!,
                    modifier = Modifier.size(36.dp)
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
        val messageText = ContextAmbient.current.getString(res, text)

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
    fun MaterialDialog.buttons(content: @Composable() MaterialDialogButtons.() -> Unit) {
        buttons.buttonsTagOrder.clear()

        val interButtonPadding = with(DensityAmbient.current) { 12.dp.toIntPx() }
        val defaultBoxHeight = with(DensityAmbient.current) { 36.dp.toIntPx() }

        Box(
            Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                .layoutId("buttons")
        ) {
            Layout({ content(buttons) }, Modifier,
                { measurables, constraints ->
                    val placeables = measurables.map { it.id to it.measure(constraints) }
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
                            val button =
                                placeables.fastFirstOrNull { it.first == "button_$tagNum" }!!.second

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
                })
        }
    }

    /**
     * @brief Adds an input field with the given parameters to the dialog
     * @param label string to be shown in the input field before selection eg. Username
     * @param hint hint to be shown in the input field when it is selected but empty eg. Joe
     * @param prefill string to be input into the text field by default
     * @param waitForPositiveButton if true the [onInput] callback will only be called when the
     * positive button is pressed, otherwise it will be called when the input value is changed
     * @param allowEmpty if true then an empty string will be a valid input
     * @param visualTransformation a visual transformation of the content of the text field
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
        allowEmpty: Boolean = false,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        errorMessage: (String) -> String = { "" },
        isTextValid: (String) -> Boolean = { true },
        onInput: (String) -> Unit = {}
    ) {
        var text by savedInstanceState { prefill }
        val index by state { positiveEnabled.value.size }
        var valid by state { allowEmpty }

        remember { positiveEnabled.value.add(index, allowEmpty) }

        if (waitForPositiveButton) {
            callbacks.add { onInput(text) }
        }

        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)) {
            FilledTextField(
                value = text,
                visualTransformation = visualTransformation,
                onValueChange = {
                    text = it
                    if (!waitForPositiveButton) {
                        onInput(text)
                    }

                    // Have to make temp list in order for state to register change
                    val tempList = positiveEnabled.value.toMutableList()
                    valid = if (text == "" && allowEmpty) {
                        true
                    } else if (text == "") {
                        false
                    } else {
                        isTextValid(text)
                    }
                    tempList[index] = valid
                    positiveEnabled.value = tempList
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(label) },
                placeholder = { Text(hint) },
                isErrorValue = !valid
            )

            if (!valid) {
                val message = errorMessage(text)
                if (message != "") {
                    Text(
                        message,
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.error,
                        modifier = Modifier.gravity(Alignment.End)
                    )
                }
            }
        }
    }

    /**
     * Create an view in the dialog with the given content and appropriate padding
     * @param children the content of the custom view
     */
    @Composable
    fun MaterialDialog.customView(children: @Composable() () -> Unit) {
        Box(modifier = Modifier.padding(bottom = 28.dp, start = 24.dp, end = 24.dp)) {
            children()
        }
    }
}
