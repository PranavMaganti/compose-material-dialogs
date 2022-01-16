package com.vanpra.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vanpra.common.demos.*

data class DialogSectionData(val title: String, val content: @Composable () -> Unit)

val sections = listOf(
    DialogSectionData("Basic Dialogs") { BasicDialogDemo() },
    DialogSectionData("Basic List Dialogs") { BasicListDialogDemo() },
    DialogSectionData("Single Selection List Dialogs") { SingleSelectionDemo() },
    DialogSectionData("Multi-Selection List Dialogs") { MultiSelectionDemo() },
    DialogSectionData("Date and Time Picker Dialogs") { DateTimeDialogDemo() },
    DialogSectionData("Color Picker Dialogs") { ColorDialogDemo() }
)

/**
 * @brief Collection of Material Dialog Demos
 */
@Composable
fun DialogDemos() {

    var currentDestination by remember { mutableStateOf("Home") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentDestination != "Home") {
                            currentDestination = "Home"
                        } else {
                            // finish
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(text = "Material Dialogs Demo")
                }
            )
        }
    ) {
        when (currentDestination) {
            "Home" -> {
                LazyColumn {
                    items(sections) {
                        TextButton(
                            onClick = { currentDestination = it.title },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(MaterialTheme.colors.primary),
                        ) {
                            Text(
                                it.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize(Alignment.Center),
                                color = MaterialTheme.colors.onPrimary
                            )
                        }
                    }
                }
            }
            else -> {
                Column {
                    val dialogSection = sections.firstOrNull { it.title == currentDestination }
                    dialogSection?.content?.let { it() }
                }
            }
        }
    }
}
