package com.vanpra.composematerialdialogs

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.tag
import androidx.ui.foundation.Box
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.imageFromResource
import androidx.ui.input.VisualTransformation
import androidx.ui.layout.*
import androidx.ui.material.FilledTextField
import androidx.ui.material.MaterialTheme
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import androidx.ui.util.fastForEachIndexed
import androidx.ui.util.fastMap

/**
 * The MaterialDialog class is used to build and display a dialog using both pre-made and
 * custom views
 *
 * @param autoDismiss when true the dialog will be automatically dismissed when a positive or
 * negative button is pressed
 */
class MaterialDialog(private val autoDismiss: Boolean = true) {
    private val showing: MutableState<Boolean> = mutableStateOf(false)
    private val buttons = MaterialDialogButtons(this)
    val callbacks = mutableListOf<() -> Unit>()
    val positiveEnabled = mutableStateOf(mutableListOf<Boolean>())

    /**
     * Shows the dialog
     */
    fun show() {
        showing.value = true
    }

    /**
     * Hides the dialog
     */
    fun hide() {
        showing.value = false
    }

    fun isAutoDismiss() = autoDismiss

    /**
     * Builds a dialog with the given content
     * @param content the body of the dialog
     */
    @Composable
    fun build(content: @Composable() MaterialDialog.() -> Unit) {
        if (showing.value) {
            ThemedDialog(onCloseRequest = { hide() }) {
                Column(Modifier.fillMaxWidth().drawBackground(MaterialTheme.colors.background)) {
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
     */
    @Composable
    fun MaterialDialog.iconTitle(
        text: String? = null,
        @StringRes textRes: Int? = null,
        @DrawableRes iconRes: Int
    ) {
        val titleText = ContextAmbient.current.getString(textRes, text)
        val icon = imageFromResource(ContextAmbient.current.resources, iconRes)

        Row(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp).preferredHeight(64.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            Image(asset = icon, modifier = Modifier.size(34.dp))
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
                .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
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

        val constraints = ConstraintSet2 {
            val buttonBox = createRefFor("buttons")

            constrain(buttonBox) {
                linkTo(parent.start, parent.end, 24.dp)
                bottom.linkTo(parent.bottom)

                width = Dimension.fillToConstraints
                height = Dimension.preferredValue(52.dp).atLeast(52.dp)
            }
        }

        val buttonConstraints = ConstraintSet2 {
            val buttonRefs = buttons.buttonsTagOrder.fastMap { createRefFor("button_$it") }

            buttonRefs.fastForEachIndexed { index, item ->
                constrain(item) {
                    centerVerticallyTo(parent)
                    if (index == 0) {
                        end.linkTo(parent.end, 8.dp)
                    } else {
                        end.linkTo(buttonRefs[index - 1].start, 8.dp)
                    }
                }
            }
        }

        ConstraintLayout(constraints, Modifier.fillMaxWidth()) {
            ConstraintLayout(buttonConstraints, modifier = Modifier.tag("buttons")) {
                content(buttons)
            }

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

        remember {
            positiveEnabled.value.add(index, allowEmpty)
        }

        if (waitForPositiveButton) {
            callbacks.add {
                onInput(text)
            }
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
        Box(modifier = Modifier.padding(bottom = 28.dp)) {
            children()
        }
    }
}

