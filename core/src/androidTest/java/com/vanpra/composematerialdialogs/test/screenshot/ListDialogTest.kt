package com.vanpra.composematerialdialogs.test.screenshot

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.karumi.shot.ScreenshotTest
import com.vanpra.composematerialdialogs.listItems
import com.vanpra.composematerialdialogs.listItemsMultiChoice
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.test.R
import com.vanpra.composematerialdialogs.test.utils.DialogWithContent
import com.vanpra.composematerialdialogs.test.utils.defaultButtons
import com.vanpra.composematerialdialogs.test.utils.onDialog
import com.vanpra.composematerialdialogs.test.utils.setContentAndWaitForIdle
import org.junit.Rule
import org.junit.Test

private val ringtones =
    listOf(
        "None",
        "Callisto",
        "Ganymede",
        "Luna",
        "Rrrring",
        "Beats",
        "Dance Party",
        "Zen Too",
        "None",
        "Callisto",
        "Ganymede",
        "Luna",
        "Rrrring",
        "Beats",
        "Dance Party",
        "Zen Too"
    )
private val labels = listOf("None", "Forums", "Social", "Updates", "Promotions", "Spam", "Bin")
private val emails = listOf(
    "joe@material-dialog.com",
    "jane@material-dialog.com",
    "dan@material-dialog.com",
    "karen@material-dialog.com"
)

class ListDialog : ScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun simpleListSelectionDialog() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent {
                title(res = R.string.backup_dialog_title)
                listItems(emails)
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun customListSelectionDialog() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent {
                title(res = R.string.backup_dialog_title)
                listItems(
                    emails,
                    item = { _, email ->
                        Row(Modifier.fillMaxWidth()) {
                            Image(
                                Icons.Default.AccountCircle,
                                contentDescription = "Account icon",
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .size(30.dp),
                                contentScale = ContentScale.FillHeight,
                                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
                            )
                            Text(
                                email,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .align(Alignment.CenterVertically),
                                color = MaterialTheme.colors.onBackground,
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                )
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun multiSelectionDialog() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent {
                title(res = R.string.labels_dialog_title)
                listItemsMultiChoice(labels)
                defaultButtons()
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }

    @Test
    fun singleSelectionDialog() {
        composeTestRule.setContentAndWaitForIdle {
            DialogWithContent {
                title(res = R.string.ringtone_dialog_title)
                listItemsSingleChoice(ringtones)
                defaultButtons()
            }
        }
        compareScreenshot(composeTestRule.onDialog())
    }
}
