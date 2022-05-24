package com.vanpra.composematerialdialogs.test.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karumi.shot.ScreenshotTest
import com.vanpra.composematerialdialogs.TextFieldStyle
import com.vanpra.composematerialdialogs.input
import com.vanpra.composematerialdialogs.test.R
import com.vanpra.composematerialdialogs.test.utils.DialogWithContent
import com.vanpra.composematerialdialogs.test.utils.extensions.onDialog
import com.vanpra.composematerialdialogs.test.utils.extensions.setContentAndWaitForIdle
import com.vanpra.composematerialdialogs.title
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InputDialogTest : ScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dialogWithFilledInput() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent(
                buttons = {
                    negativeButton("Cancel")
                    positiveButton("Ok")
                }
            ) {
                title(res = R.string.input_dialog_title)
                input(label = "Name", placeholder = "Jon Smith")
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun dialogWithOutlinedInput() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent(
                buttons = {
                    negativeButton("Cancel")
                    positiveButton("Ok")
                }
            ) {
                title(res = R.string.input_dialog_title)
                input(
                    label = "Name",
                    placeholder = "Jon Smith",
                    textFieldStyle = TextFieldStyle.Outlined
                )
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }
}
