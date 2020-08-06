package com.vanpra.composematerialdialogsdemos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import com.vanpra.composematerialdialogsdemos.demos.BasicDialogDemo
import com.vanpra.composematerialdialogsdemos.demos.BasicListDialogDemo
import com.vanpra.composematerialdialogsdemos.demos.ColorDialogDemo
import com.vanpra.composematerialdialogsdemos.demos.DateTimeDialogDemo
import com.vanpra.composematerialdialogsdemos.demos.MultiSelectionDemo
import com.vanpra.composematerialdialogsdemos.demos.SingleSelectionDemo
import com.vanpra.composematerialdialogsdemos.ui.ComposeMaterialDialogsTheme

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

/**
 * @brief Collection of Material Dialog Demos
 */
@Composable
fun DialogDemos() {
    ScrollableColumn {
        Column {
            DialogSection(title = "Basic Dialogs") {
                BasicDialogDemo()
            }

            DialogSection(title = "Basic List Dialogs") {
                BasicListDialogDemo()
            }

            DialogSection(title = "Single Selection List Dialogs") {
                SingleSelectionDemo()
            }

            DialogSection("Multi-Selection List Dialogs") {
                MultiSelectionDemo()
            }

            DialogSection("Date and Time Picker Dialogs") {
                DateTimeDialogDemo()
            }

            DialogSection("Color Picker Dialogs") {
                ColorDialogDemo()
            }
        }
    }
}
