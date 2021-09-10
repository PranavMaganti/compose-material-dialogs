package com.vanpra.composematerialdialogs.datetime.test.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.test.utils.DialogWithContent
import com.vanpra.composematerialdialogs.test.utils.extensions.onDialog
import com.vanpra.composematerialdialogs.test.utils.extensions.setContentAndWaitForIdle
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class DateTimePickerTest : ScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val testTitle = "Custom Title"

    @Test
    fun datePickerBasic() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent {
                datepicker(initialDate = LocalDate.of(2021, 1, 1))
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun timePickerBasic() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent {
                timepicker(initialTime = LocalTime.of(12, 0))
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun timePicker24Hour() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent {
                timepicker(
                    initialTime = LocalTime.of(12, 0),
                    is24HourClock = true
                )
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun datePickerWithCustomTitle() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent {
                datepicker(title = testTitle, initialDate = LocalDate.of(2021, 7, 27))
            }
        }

        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun timePickerWithCustomTitle() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent {
                timepicker(title = testTitle, initialTime = LocalTime.of(19, 0))
            }
        }

        compareScreenshot(composeTestRule.onDialog())
    }
}
