package com.vanpra.composematerialdialogs

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun MyTest() {
        composeTestRule.setContent {
            MaterialTheme {
                Text("Hello")
            }
        }

        composeTestRule.onNodeWithTag("Hello").assertIsDisplayed()
    }
}
