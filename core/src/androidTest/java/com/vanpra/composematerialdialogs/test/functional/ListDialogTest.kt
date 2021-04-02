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
import com.vanpra.composematerialdialogs.test.util.onDialogListItem
import com.vanpra.composematerialdialogs.test.util.onPositiveButton
import com.vanpra.composematerialdialogs.test.util.powerSet
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
        "Copycat",
        "Lollipop",
        "Crackle",
        "Mash-Up",
        "Lost & Found"
    )
private val labels = listOf("None", "Social", "Updates", "Promotions", "Spam", "Bin")
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
            composeTestRule.onDialogListItem(index).performClick()
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
                    onClick = { index, item -> selectedItem = Pair(index, item) }
                ) { _, email ->
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
            }
        }

        emails.forEachIndexed { index, email ->
            composeTestRule.onDialogListItem(index).performClick()
            /* Need this line or else tests don't wait for dialog to close */
            composeTestRule.assertDialogDoesNotExist()
            assertThat(selectedItem, equalTo(Pair(index, email)))
            dialog.show()
        }
    }

    @Test
    fun multiSelectionDialogWaitForPositiveButton() {
        val selectedItem = mutableStateOf<Set<Int>?>(null)
        val dialog = MaterialDialog()
        setupMultiSelectionDialog(dialog, selectedItem)

        labels.forEachIndexed { index, _ ->
            composeTestRule.onDialogListItem(index).performClick()
            assertEquals(null, selectedItem.value)
            composeTestRule.onPositiveButton().performClick()
            /* Need this line or else tests don't wait for dialog to close */
            composeTestRule.assertDialogDoesNotExist()
            assertEquals(setOf(index), selectedItem.value)
            selectedItem.value = null
            dialog.show()
        }
    }

    @Test
    fun multiSelectionDialogDontWaitForPositiveButton() {
        val selectedItem = mutableStateOf<Set<Int>?>(null)
        val dialog = MaterialDialog()
        setupMultiSelectionDialog(dialog, selectedItem, waitForPositiveButton = false)

        labels.forEachIndexed { index, _ ->
            composeTestRule.onDialogListItem(index).performClick()
            assertEquals(setOf(index), selectedItem.value)
            selectedItem.value = null
            composeTestRule.onPositiveButton().performClick()
            /* Need this line or else tests don't wait for dialog to close */
            composeTestRule.assertDialogDoesNotExist()
            assertEquals(null, selectedItem.value)
            dialog.show()
        }
    }

    /* Exhaustive Stress test of all possible inputs for labels */
    @Test
    fun multiSelectionDialogItems() {
        val selectedItem = mutableStateOf<Set<Int>?>(null)
        val dialog = MaterialDialog()
        setupMultiSelectionDialog(dialog, selectedItem)

        IntRange(0, labels.size - 1).toList().powerSet().forEachIndexed { _, indexes ->
            /* Tests all combinations of of input items */
            indexes.forEach { index ->
                composeTestRule.onDialogListItem(index).performClick()
            }
            composeTestRule.onPositiveButton().performClick()
            /* Need this line or else tests don't wait for dialog to close */
            composeTestRule.assertDialogDoesNotExist()
            assertEquals(indexes, selectedItem.value)
            dialog.show()
        }
    }

    @Test
    fun multiSelectionDialogItemRemovedWhenClickedDeselected() {
        val selectedItem = mutableStateOf<Set<Int>?>(null)
        val dialog = MaterialDialog()
        setupMultiSelectionDialog(dialog, selectedItem, waitForPositiveButton = false)

        labels.forEachIndexed { index, _ ->
            composeTestRule.onDialogListItem(index).performClick()
            composeTestRule.onDialogListItem(index).performClick()
            composeTestRule.onPositiveButton().performClick()
            /* Need this line or else tests don't wait for dialog to close */
            composeTestRule.assertDialogDoesNotExist()
            assertEquals(setOf<Int>(), selectedItem.value)
            dialog.show()
        }
    }

    @Test
    fun singleSelectionDialog() {
        val selectedItem = mutableStateOf<Int?>(null)
        val dialog = MaterialDialog()

        setupSingleSelectionDialog(dialog, selectedItem)

        ringtones.forEachIndexed { index, _ ->
            composeTestRule.onDialogListItem(index).performClick()
            composeTestRule.onPositiveButton().performClick()

            /* Need this line or else tests don't wait for dialog to close */
            composeTestRule.assertDialogDoesNotExist()
            assertEquals(index, selectedItem.value)
            dialog.show()
        }
    }

    private fun setupMultiSelectionDialog(
        dialog: MaterialDialog,
        selectedItem: MutableState<Set<Int>?>,
        waitForPositiveButton: Boolean = true
    ) {
        composeTestRule.setContent {
            DialogWithContent(dialog = dialog) {
                title(res = R.string.labels_dialog_title)
                listItemsMultiChoice(labels, waitForPositiveButton = waitForPositiveButton) {
                    selectedItem.value = it
                }
                defaultListButtons()
            }
        }
    }

    private fun setupSingleSelectionDialog(
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
