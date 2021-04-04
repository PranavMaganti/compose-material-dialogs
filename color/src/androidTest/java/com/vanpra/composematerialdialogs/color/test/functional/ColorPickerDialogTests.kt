package com.vanpra.composematerialdialogs.color.test.functional

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.test.utils.DialogWithContent
import com.vanpra.composematerialdialogs.test.utils.assertDialogDoesNotExist
import com.vanpra.composematerialdialogs.test.utils.onPositiveButton
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColorPickerDialogTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    private fun MaterialDialog.defaultButtons() {
        buttons {
            negativeButton("Disagree")
            positiveButton("Agree")
        }
    }

    @Test
    fun dialogDismissedOnPositiveButton() {
        composeTestRule.setContent { DialogWithContent { defaultButtons() } }
        composeTestRule.onPositiveButton().performClick()
        composeTestRule.assertDialogDoesNotExist()
    }
}
