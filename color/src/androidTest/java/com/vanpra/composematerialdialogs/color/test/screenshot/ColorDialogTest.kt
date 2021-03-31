package com.vanpra.composematerialdialogs.color.test.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.vanpra.composematerialdialogs.test.util.onDialog
import org.junit.Rule
import org.junit.Test

class ColorDialogTest : ScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dialogWithoutButtons() {
        composeTestRule.setContent {
        }
        compareScreenshot(composeTestRule.onDialog())
    }
}
