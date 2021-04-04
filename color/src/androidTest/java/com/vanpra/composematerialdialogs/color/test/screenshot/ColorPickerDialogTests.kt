package com.vanpra.composematerialdialogs.color.test.screenshot

import androidx.compose.foundation.Image
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.vanpra.composematerialdialogs.test.R
import com.vanpra.composematerialdialogs.test.utils.DialogWithContent
import com.vanpra.composematerialdialogs.test.utils.onDialog
import org.junit.Rule
import org.junit.Test

class ColorPickerDialogTests : ScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dialogWithoutButtons() {
        composeTestRule.setContent {
            DialogWithContent {
                title(res = R.string.location_dialog_title)
                message(res = R.string.location_dialog_message)
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun dialogWithButtons() {
        composeTestRule.setContent {
            DialogWithContent {
                title(res = R.string.location_dialog_title)
                message(res = R.string.location_dialog_message)
                buttons {
                    negativeButton("Disagree")
                    positiveButton("Agree")
                }
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun dialogWithButtonsAndIconTitle() {
        composeTestRule.setContent {
            DialogWithContent {
                iconTitle(
                    icon = {
                        Image(
                            Icons.Default.LocationOn,
                            contentDescription = "Location Icon",
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
                        )
                    },
                    textRes = R.string.location_dialog_title
                )
                message(res = R.string.location_dialog_message)
                buttons {
                    negativeButton("Disagree")
                    positiveButton("Agree")
                }
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun dialogWithStackedButtons() {
        composeTestRule.setContent {
            DialogWithContent {
                title(res = R.string.location_dialog_title)
                message(res = R.string.location_dialog_message)
                buttons {
                    negativeButton("No Thanks")
                    positiveButton("Turn On Speed Boost")
                }
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun dialogWithInput() {
        composeTestRule.setContent {
            DialogWithContent {
                title(res = R.string.input_dialog_title)
                input(label = "Name", hint = "Jon Smith")
                buttons {
                    negativeButton("Cancel")
                    positiveButton("Ok")
                }
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }
}
