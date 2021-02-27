package com.vanpra.composematerialdialogdemos

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

/**
 * @brief Collection of Material Dialog Demos
 */
@Composable
fun DialogDemos() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
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
