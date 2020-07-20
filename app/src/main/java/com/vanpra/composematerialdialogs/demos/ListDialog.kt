package com.vanpra.composematerialdialogs.demos

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.size
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Person
import androidx.ui.unit.dp
import com.vanpra.composematerialdialogs.DialogAndShowButton
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.R
import com.vanpra.composematerialdialogs.listItems
import com.vanpra.composematerialdialogs.listItemsMultiChoice
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import dev.chrisbanes.accompanist.coil.CoilImage

private val ringtones =
    listOf("None", "Callisto", "Ganymede", "Luna", "Rrrring", "Beats", "Dance Party", "Zen Too","None", "Callisto", "Ganymede", "Luna", "Rrrring", "Beats", "Dance Party", "Zen Too")
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
                CoilImage(
                    emailIcon.icon,
                    modifier = Modifier.padding(vertical = 8.dp).size(30.dp),
                    contentScale = ContentScale.FillHeight
                )
                Text(
                    emailIcon.email,
                    modifier = Modifier.padding(start = 16.dp).gravity(Alignment.CenterVertically),
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
    var initialSelection by state { listOf(3, 5) }

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
    var initialSingleSelection by state { 4 }

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
            initialSelection = initialSingleSelection,
            onChoiceChange = { initialSingleSelection = it },
            waitForPositiveButton = true
        )
        defaultListButtons()
    }
}
