package com.vanpra.common.demos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.vanpra.common.DialogAndShowButton
import com.vanpra.composematerialdialogs.*

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
        title(text ="Set backup account")
        listItems(emails)
    }

    DialogAndShowButton(buttonText = "Custom List Dialog") {
        title(text ="Set backup account")
        listItems(
            modifier = Modifier.padding(bottom = 8.dp).padding(horizontal = 24.dp),
            list = emails,
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

/**
 * @brief Multi Selection List Dialog Demos
 */
@Composable
fun MultiSelectionDemo() {
    var initialSelection by remember { mutableStateOf(setOf(3, 5)) }

    DialogAndShowButton(
        buttonText = "Multi-Selection Dialog",
        buttons = { defaultListDialogButtons() }
    ) {
        title(text = "Label as:")
        listItemsMultiChoice(labels) {
            println(it)
        }
    }

    DialogAndShowButton(
        buttonText = "Multi-Selection Dialog with disabled items",
        buttons = { defaultListDialogButtons() }
    ) {
        val disabledLabels = setOf(1, 3, 4)

        title(text = "Label as:")
        listItemsMultiChoice(labels, disabledIndices = disabledLabels) {
            println(it)
        }
    }

    DialogAndShowButton(
        buttonText = "Multi-Selection Dialog with initial selection",
        buttons = { defaultListDialogButtons() }
    ) {
        title(text = "Label as:")
        listItemsMultiChoice(
            labels,
            initialSelection = initialSelection,
            waitForPositiveButton = true
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

    DialogAndShowButton(
        buttonText = "Single Selection Dialog",
        buttons = { defaultListDialogButtons() }
    ) {
        title(text = "Phone Ringtone")
        listItemsSingleChoice(ringtones) {
            println(it)
        }
    }

    DialogAndShowButton(
        buttonText = "Single Selection Dialog with disabled items",
        buttons = { defaultListDialogButtons() }
    ) {
        val disabledRingtones = setOf(2, 4, 5)

        title(text = "Phone Ringtone")
        listItemsSingleChoice(ringtones, disabledIndices = disabledRingtones) {
            println(it)
        }
    }

    DialogAndShowButton(
        buttonText = "Single Selection Dialog with initial selection",
        buttons = { defaultListDialogButtons() }
    ) {
        title(text = "Phone Ringtone")
        listItemsSingleChoice(
            ringtones,
            initialSelection = initialSingleSelection
        ) { initialSingleSelection = it }
    }
}
