package com.vanpra.composematerialdialogdemos

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.vanpra.composematerialdialogdemos.demos.BasicDialogDemo
import com.vanpra.composematerialdialogdemos.demos.BasicListDialogDemo
import com.vanpra.composematerialdialogdemos.demos.ColorDialogDemo
import com.vanpra.composematerialdialogdemos.demos.DateTimeDialogDemo
import com.vanpra.composematerialdialogdemos.demos.MultiSelectionDemo
import com.vanpra.composematerialdialogdemos.demos.SingleSelectionDemo
import com.vanpra.composematerialdialogdemos.ui.ComposeMaterialDialogsTheme

/**
 * @brief MainActivity with material dialog samples
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMaterialDialogsTheme {
                DialogDemos()
            }
        }
    }
}

data class DialogSectionData(val title: String, val content: @Composable () -> Unit)

/**
 * @brief Collection of Material Dialog Demos
 */
@Composable
fun DialogDemos() {
    val sections = listOf(
        DialogSectionData("Basic Dialogs") { BasicDialogDemo() },
        DialogSectionData("Basic List Dialogs") { BasicListDialogDemo() },
        DialogSectionData("Single Selection List Dialogs") { SingleSelectionDemo() },
        DialogSectionData("Multi-Selection List Dialogs") { MultiSelectionDemo() },
        DialogSectionData("Date and Time Picker Dialogs") { DateTimeDialogDemo() },
        DialogSectionData("Color Picker Dialogs") { ColorDialogDemo() }
    )

    LazyColumn {
        items(sections) {
            DialogSection(title = it.title) { it.content() }
        }
    }
}
