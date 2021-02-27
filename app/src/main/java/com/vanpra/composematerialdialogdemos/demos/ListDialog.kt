package com.vanpra.composematerialdialogdemos.demos

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogdemos.DialogAndShowButton
import com.vanpra.composematerialdialogdemos.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.listItems
import com.vanpra.composematerialdialogs.listItemsMultiChoice
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import dev.chrisbanes.accompanist.coil.CoilImage

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

private data class EmailIcon(
    val email: String,
    @DrawableRes val icon: Int
)

private val emailItems = listOf(
    EmailIcon("joe@material-dialog.com", R.drawable.tick),
    EmailIcon("jane@material-dialog.com", R.drawable.tick),
    EmailIcon("dan@material-dialog.com", R.drawable.tick),
    EmailIcon("helen@material-dialog.com", R.drawable.tick),
    EmailIcon("karen@material-dialog.com", R.drawable.tick)
)

@Composable
private fun MaterialDialog.defaultListButtons() {
    buttons {
        negativeButton("Cancel")
        positiveButton("Ok")
    }
}

/**
 * @brief Basic List Dialog Demos
 */
@Composable
fun BasicListDialogDemo() {
    DialogAndShowButton(buttonText = "Simple List Dialog") {
        val emails = emailItems.map { it.email }
        title(res = R.string.backup_dialog_title)
        listItems(emails)
    }

    DialogAndShowButton(buttonText = "Custom List Dialog") {
        title(res = R.string.backup_dialog_title)
        listItems(emailItems) { _, emailIcon ->
            Row(Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(emailIcon.icon),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(30.dp),
                    contentScale = ContentScale.FillHeight,
                    contentDescription = null
                )
                Text(
                    emailIcon.email,
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

/**
 * @brief Multi Selection List Dialog Demos
 */
@Composable
fun MultiSelectionDemo() {
    var initialSelection by remember { mutableStateOf(listOf(3, 5)) }

    DialogAndShowButton(buttonText = "Multi-Selection Dialog") {
        title(res = R.string.labels_dialog_title)
        listItemsMultiChoice(labels)
        defaultListButtons()
    }

    DialogAndShowButton(buttonText = "Multi-Selection Dialog with disabled items") {
        val disabledLabels = listOf(1, 3, 4)

        title(res = R.string.labels_dialog_title)
        listItemsMultiChoice(labels, disabledIndices = disabledLabels)
        defaultListButtons()
    }

    DialogAndShowButton(buttonText = "Multi-Selection Dialog with initial selection") {
        title(res = R.string.labels_dialog_title)
        listItemsMultiChoice(
            labels,
            initialSelection = initialSelection,
            waitForPositiveButton = true
        ) {
            initialSelection = it
        }
        defaultListButtons()
    }
}

/**
 * @brief Single Selection List Dialog Demos
 */
@Composable
fun SingleSelectionDemo() {
    var initialSingleSelection by remember { mutableStateOf(4) }

    DialogAndShowButton(buttonText = "Single Selection Dialog") {
        title(res = R.string.ringtone_dialog_title)
        listItemsSingleChoice(ringtones)
        defaultListButtons()
    }

    DialogAndShowButton(buttonText = "Single Selection Dialog with disabled items") {
        val disabledRingtones = listOf(2, 4, 5)

        title(res = R.string.ringtone_dialog_title)
        listItemsSingleChoice(ringtones, disabledIndices = disabledRingtones)
        defaultListButtons()
    }

    DialogAndShowButton(buttonText = "Single Selection Dialog with initial selection") {
        title(res = R.string.ringtone_dialog_title)
        listItemsSingleChoice(
            ringtones,
            initialSelection = initialSingleSelection
        ) { initialSingleSelection = it }

        defaultListButtons()
    }
}
