package com.vanpra.composematerialdialogs.demos

import androidx.annotation.DrawableRes
import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.imageFromResource
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.size
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Person
import androidx.ui.unit.dp
import com.vanpra.composematerialdialogs.*

private val ringtones =
    listOf("None", "Callisto", "Ganymede", "Luna", "Rrrring", "Beats", "Dance Party", "Zen Too")
private val labels = listOf("None", "Forums", "Social", "Updates", "Promotions", "Spam", "Bin")

private data class EmailIcon(
    val email: String,
    val icon: VectorAsset
)

private val emailItems = listOf(
    EmailIcon("joe@material-dialog.com", Icons.Default.Person),
    EmailIcon("jane@material-dialog.com", Icons.Default.Person),
    EmailIcon("dan@material-dialog.com", Icons.Default.Person),
    EmailIcon("helen@material-dialog.com", Icons.Default.Person),
    EmailIcon("karen@material-dialog.com", Icons.Default.Person)
)

@Composable
private fun MaterialDialogButtons.defaultButtons() {
    negativeButton("Cancel")
    positiveButton("Ok")
}

@Composable
fun BasicListDialogDemo() {
    DialogAndShowButton(buttonText = "Simple List Dialog") {
        val emails = emailItems.map { it.email }
        title(res = R.string.backup_dialog_title)
        listItems(emails)
    }

    DialogAndShowButton(buttonText = "Custom List Dialog") {
        title(res = R.string.backup_dialog_title)
        listItems(emailItems) { index, emailIcon ->
            Row(Modifier.fillMaxWidth()) {
                Image(
                    emailIcon.icon,
                    modifier = Modifier.size(45.dp).padding(vertical = 8.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
                )
                Text(
                    emailIcon.email,
                    modifier = Modifier.padding(start = 8.dp).gravity(Alignment.CenterVertically),
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Composable
fun MultiSelectionDemo() {
    var initialSelection by state { listOf(3, 5) }

    DialogAndShowButton(buttonText = "Multi-Selection Dialog") {
        title(res = R.string.labels_dialog_title)
        listItemsMultiChoice(labels)
        buttons { defaultButtons() }
    }

    DialogAndShowButton(buttonText = "Multi-Selection Dialog with disabled items") {
        val disabledLabels = listOf(1, 3, 4)

        title(res = R.string.labels_dialog_title)
        listItemsMultiChoice(labels, disabledIndices = disabledLabels)
        buttons { defaultButtons() }
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
        buttons { defaultButtons() }
    }
}

@Composable
fun SingleSelectionDemo() {
    var initialSingleSelection by state { 4 }

    DialogAndShowButton(buttonText = "Single Selection Dialog") {
        title(res = R.string.ringtone_dialog_title)
        listItemsSingleChoice(ringtones)
        buttons { defaultButtons() }
    }

    DialogAndShowButton(buttonText = "Single Selection Dialog with disabled items") {
        val disabledRingtones = listOf(2, 4, 5)

        title(res = R.string.ringtone_dialog_title)
        listItemsSingleChoice(ringtones, disabledIndices = disabledRingtones)
        buttons { defaultButtons() }
    }

    DialogAndShowButton(buttonText = "Single Selection Dialog with initial selection") {
        title(res = R.string.ringtone_dialog_title)
        listItemsSingleChoice(
            ringtones,
            initialSelection = initialSingleSelection,
            onChoiceChange = { initialSingleSelection = it },
            waitForPositiveButton = true
        )
        buttons { defaultButtons() }
    }
}

