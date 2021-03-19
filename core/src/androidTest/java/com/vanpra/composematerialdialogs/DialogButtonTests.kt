package com.vanpra.composematerialdialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DialogButtonTests {
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

    @Test
    fun dialogDismissedOnNegativeButton() {
        composeTestRule.setContent { DialogWithContent { defaultButtons() } }
        composeTestRule.onNegativeButton().performClick()
        composeTestRule.assertDialogDoesNotExist()
    }

    @Test
    fun dialogNotDismissedOnPositiveButton() {
        composeTestRule.setContent { DialogWithContent(false) { defaultButtons() } }
        composeTestRule.onPositiveButton().performClick()
        composeTestRule.assertDialogExists()
    }

    @Test
    fun dialogNotDismissedOnNegativeButton() {
        composeTestRule.setContent { DialogWithContent(false) { defaultButtons() } }
        composeTestRule.onNegativeButton().performClick()
        composeTestRule.assertDialogExists()
    }
}
