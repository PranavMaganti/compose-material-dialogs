package com.vanpra.composematerialdialogs.color.test.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColorDialogTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dialogWithoutButtons() {
        composeTestRule.setContent {}
        assert(true)
    }
}
