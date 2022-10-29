package com.vanpra.composematerialdialogdemos.demos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogdemos.DialogAndShowButton
import com.vanpra.composematerialdialogdemos.R
import com.vanpra.composematerialdialogs.MaterialDialogButtons
import com.vanpra.composematerialdialogs.listItems
import com.vanpra.composematerialdialogs.listItemsMultiChoice
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.title

private val ringtones = listOf(
    "None", "Callisto", "Ganymede", "Luna", "Rrrring", "Beats", "Dance Party", "Zen Too"
)
private val labels = listOf("None", "Forums", "Social", "Updates", "Promotions", "Spam", "Bin")
private val emails = mutableStateListOf(
    "joe@material-dialog.com",
    "jane@material-dialog.com",
    "dan@material-dialog.com",
    "karen@material-dialog.com"
)

@Composable
private fun MaterialDialogButtons.defaultListDialogButtons() {
    negativeButton("Cancel")
    positiveButton("Ok")
}

/**
 * @brief Basic List Dialog Demos
 */
@Composable
fun BasicListDialogDemo() {
    DialogAndShowButton(buttonText = "Simple List Dialog") {
        title(res = R.string.backup_dialog_title)
        listItems(emails)
    }

    DialogAndShowButton(buttonText = "Custom List Dialog") {
        title(res = R.string.backup_dialog_title)
        listItems(list = emails, item = { _, email ->
            Row(Modifier.fillMaxWidth()) {
                Image(
                    Icons.Default.AccountCircle,
                    contentDescription = "Account icon",
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(30.dp),
                    contentScale = ContentScale.FillHeight,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
                Text(
                    email,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        })
    }
}

/**
 * @brief Multi Selection List Dialog Demos
 */
@Composable
fun MultiSelectionDemo() {
    var initialSelection by remember { mutableStateOf(setOf(3, 5)) }

    DialogAndShowButton(buttonText = "Multi-Selection Dialog",
        buttons = { defaultListDialogButtons() }) {
        title(res = R.string.labels_dialog_title)
        listItemsMultiChoice(labels) {
            println(it)
        }
    }

    DialogAndShowButton(buttonText = "Multi-Selection Dialog with disabled items",
        buttons = { defaultListDialogButtons() }) {
        val disabledLabels = setOf(1, 3, 4)

        title(res = R.string.labels_dialog_title)
        listItemsMultiChoice(labels, disabledIndices = disabledLabels) {
            println(it)
        }
    }

    DialogAndShowButton(buttonText = "Multi-Selection Dialog with initial selection",
        buttons = { defaultListDialogButtons() }) {
        title(res = R.string.labels_dialog_title)
        listItemsMultiChoice(
            labels, initialSelection = initialSelection, waitForPositiveButton = true
        ) {
            initialSelection = it
        }
    }
}

/**
 * @brief Single Selection List Dialog Demos
 */
@Composable
fun SingleSelectionDemo() {
    var initialSingleSelection by remember { mutableStateOf(4) }

    DialogAndShowButton(buttonText = "Single Selection Dialog",
        buttons = { defaultListDialogButtons() }) {
        title(res = R.string.ringtone_dialog_title)
        listItemsSingleChoice(ringtones) {
            println(it)
        }
    }

    DialogAndShowButton(buttonText = "Single Selection Dialog with disabled items",
        buttons = { defaultListDialogButtons() }) {
        val disabledRingtones = setOf(2, 4, 5)

        title(res = R.string.ringtone_dialog_title)
        listItemsSingleChoice(ringtones, disabledIndices = disabledRingtones) {
            println(it)
        }
    }

    DialogAndShowButton(buttonText = "Single Selection Dialog with initial selection",
        buttons = { defaultListDialogButtons() }) {
        title(res = R.string.ringtone_dialog_title)
        listItemsSingleChoice(
            ringtones, initialSelection = initialSingleSelection
        ) { initialSingleSelection = it }
    }
}
