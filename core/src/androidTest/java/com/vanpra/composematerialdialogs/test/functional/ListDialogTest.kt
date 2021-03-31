package com.vanpra.composematerialdialogs.test.functional

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.listItems
import com.vanpra.composematerialdialogs.listItemsMultiChoice
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.test.R
import com.vanpra.composematerialdialogs.test.util.DialogWithContent
import com.vanpra.composematerialdialogs.test.util.assertDialogDoesNotExist
import com.vanpra.composematerialdialogs.test.util.onListItem
import com.vanpra.composematerialdialogs.test.util.onPositiveButton
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Composable
private fun MaterialDialog.defaultListButtons() {
    buttons {
        negativeButton("Cancel")
        positiveButton("Ok")
    }
}

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

@RunWith(AndroidJUnit4::class)
class ListDialog {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun simpleListSelectionDialog() {
        val dialog = MaterialDialog()
        var selectedItem: Pair<Int, String>? = null

        composeTestRule.setContent {
            DialogWithContent(dialog = dialog) {
                title(res = R.string.backup_dialog_title)
                listItems(emails) { index, item ->
                    selectedItem = Pair(index, item)
                }
            }
        }

        emails.forEachIndexed { index, email ->
            composeTestRule.onListItem(index).performClick()
            /* Need this line or else tests don't wait for dialog to close */
            composeTestRule.assertDialogDoesNotExist()
            assertThat(selectedItem, equalTo(Pair(index, email)))
            dialog.show()
        }
    }

    @Test
    fun customListSelectionDialog() {
        var selectedItem: Pair<Int, String>? = null
        val dialog = MaterialDialog()

        composeTestRule.setContent {
            DialogWithContent(dialog = dialog) {
                title(res = R.string.backup_dialog_title)
                listItems(
                    emails,
                    onClick = { index, item ->
                        selectedItem = Pair(index, item)
                    }
                ) { index, email ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .testTag("list_$index")
                    ) {
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
            }
        }

        emails.forEachIndexed { index, email ->
            composeTestRule.onListItem(index).performClick()
            /* Need this line or else tests don't wait for dialog to close */
            composeTestRule.assertDialogDoesNotExist()
            assertThat(selectedItem, equalTo(Pair(index, email)))
            dialog.show()
        }
    }

    @Test
    fun multiSelectionDialogOneItem() {
        val selectedItem = mutableStateOf<List<Int>?>(null)
        val dialog = MaterialDialog()
        composeTestRule.setupMultiSelectionDialog(dialog, selectedItem)

        labels.forEachIndexed { index, _ ->
            composeTestRule.onListItem(index).performClick()
            composeTestRule.onPositiveButton().performClick()
            /* Need this line or else tests don't wait for dialog to close */
            composeTestRule.assertDialogDoesNotExist()
            assertEquals(listOf(index), selectedItem.value)
            dialog.show()
        }
    }


    @Test
    fun multiSelectionDialogMultipleItems() {
        val selectedItem = mutableStateOf<List<Int>?>(null)
        val dialog = MaterialDialog()
        composeTestRule.setupMultiSelectionDialog(dialog, selectedItem)

        labels.forEachIndexed { index, _ ->
            composeTestRule.onListItem(index).performClick()
            composeTestRule.onPositiveButton().performClick()
            /* Need this line or else tests don't wait for dialog to close */
            composeTestRule.assertDialogDoesNotExist()
            assertEquals(listOf(index), selectedItem.value)
            dialog.show()
        }
    }


    @Test
    fun singleSelectionDialog() {
        val selectedItem = mutableStateOf<Int?>(null)
        val dialog = MaterialDialog()

        composeTestRule.setupSingleSelectionDialog(dialog, selectedItem)

        ringtones.forEachIndexed { index, _ ->
            composeTestRule.onListItem(index).performClick()
            composeTestRule.onPositiveButton().performClick()
            /* Need this line or else tests don't wait for dialog to close */
            composeTestRule.assertDialogDoesNotExist()
            assertEquals(listOf(index), selectedItem.value)
            dialog.show()
        }
    }

    private fun ComposeTestRule.setupMultiSelectionDialog(
        dialog: MaterialDialog,
        selectedItem: MutableState<List<Int>?>
    ) {
        composeTestRule.setContent {
            DialogWithContent(dialog = dialog) {
                title(res = R.string.labels_dialog_title)
                listItemsMultiChoice(labels) {
                    selectedItem.value = it
                }
                defaultListButtons()
            }
        }
    }

    private fun ComposeTestRule.setupSingleSelectionDialog(
        dialog: MaterialDialog,
        selectedItem: MutableState<Int?>
    ) {
        composeTestRule.setContent {
            DialogWithContent(dialog = dialog) {
                title(res = R.string.ringtone_dialog_title)
                listItemsSingleChoice(ringtones) {
                    selectedItem.value = it
                }
                defaultListButtons()
            }
        }
    }
}
