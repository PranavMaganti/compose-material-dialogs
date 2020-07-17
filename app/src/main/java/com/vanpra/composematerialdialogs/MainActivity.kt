package com.vanpra.composematerialdialogs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import com.vanpra.composematerialdialogs.demos.BasicDialogDemo
import com.vanpra.composematerialdialogs.demos.BasicListDialogDemo
import com.vanpra.composematerialdialogs.demos.MultiSelectionDemo
import com.vanpra.composematerialdialogs.demos.SingleSelectionDemo
import com.vanpra.composematerialdialogs.ui.ComposeMaterialDialogsTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMaterialDialogsTheme {
                VerticalScroller {
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
                    }
                }
            }
        }
    }
}
