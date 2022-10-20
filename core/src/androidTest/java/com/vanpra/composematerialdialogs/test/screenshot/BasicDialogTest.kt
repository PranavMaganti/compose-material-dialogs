package com.vanpra.composematerialdialogs.test.screenshot

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karumi.shot.ScreenshotTest
import com.vanpra.composematerialdialogs.iconTitle
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.test.R
import com.vanpra.composematerialdialogs.test.utils.DialogWithContent
import com.vanpra.composematerialdialogs.test.utils.extensions.onDialog
import com.vanpra.composematerialdialogs.test.utils.extensions.setContentAndWaitForIdle
import com.vanpra.composematerialdialogs.title
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BasicDialogTest : ScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dialogWithoutButtons() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent {
                title(res = R.string.location_dialog_title)
                message(res = R.string.location_dialog_message)
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun dialogWithButtons() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent(
                buttons = {
                    negativeButton("Disagree")
                    positiveButton("Agree")
                }
            ) {
                title(res = R.string.location_dialog_title)
                message(res = R.string.location_dialog_message)
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun dialogWithButtonsAndIconTitle() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent(
                buttons = {
                    negativeButton("Disagree")
                    positiveButton("Agree")
                }
            ) {
                iconTitle(
                    icon = {
                        Image(
                            Icons.Default.LocationOn,
                            contentDescription = "Location Icon",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )
                    },
                    textRes = R.string.location_dialog_title
                )
                message(res = R.string.location_dialog_message)
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun dialogWithStackedButtons() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent(
                buttons = {
                    negativeButton("No Thanks")
                    positiveButton("Turn On Speed Boost")
                }
            ) {
                title(res = R.string.location_dialog_title)
                message(res = R.string.location_dialog_message)
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }
}
