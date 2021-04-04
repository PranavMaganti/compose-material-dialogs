package com.vanpra.composematerialdialogs.color.test.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.swipeLeft
import com.karumi.shot.ScreenshotTest
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.test.utils.DialogWithContent
import com.vanpra.composematerialdialogs.test.utils.onDialog
import com.vanpra.composematerialdialogs.test.utils.onDialogColorPicker
import com.vanpra.composematerialdialogs.test.utils.onDialogColorSelector
import org.junit.Rule
import org.junit.Test

class ColorPickerDialogTests : ScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainColorPicker() {
        composeTestRule.setContent {
            DialogWithContent {
                colorChooser(colors = ColorPalette.Primary)
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun subColorPicker() {
        composeTestRule.setContent {
            DialogWithContent {
                colorChooser(colors = ColorPalette.Primary, subColors = ColorPalette.PrimarySub)
            }
        }

        composeTestRule.onDialogColorSelector(0).performClick()
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun argbColorPicker() {
        composeTestRule.setContent {
            DialogWithContent {
                colorChooser(colors = ColorPalette.Primary, subColors = ColorPalette.PrimarySub)
            }
        }

        composeTestRule.onDialogColorPicker().performGesture { swipeLeft() }
        compareScreenshot(composeTestRule.onDialog())
    }
}
