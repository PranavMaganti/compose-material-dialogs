package com.vanpra.composematerialdialogdemos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
class MainActivity : ComponentActivity() {
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

val sections = listOf(
    DialogSectionData("Basic Dialogs") { BasicDialogDemo() },
    DialogSectionData("List Dialogs") {
        BasicListDialogDemo()
        SingleSelectionDemo()
        MultiSelectionDemo()
    },
    DialogSectionData("Date and Time Picker Dialogs") { DateTimeDialogDemo() },
    DialogSectionData("Color Picker Dialogs") { ColorDialogDemo() }
)

/**
 * @brief Collection of Material Dialog Demos
 */
@Composable
fun DialogDemos() {
    val navController = rememberNavController()

    NavHost(navController = navController, "Home") {
        composable("Home") {
            LazyColumn {
                items(sections) {
                    TextButton(
                        onClick = { navController.navigate(it.title) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            it.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        sections.forEach { dialogSection ->
            composable(dialogSection.title) {
                Column {
                    dialogSection.content()
                }
            }
        }
    }
}
