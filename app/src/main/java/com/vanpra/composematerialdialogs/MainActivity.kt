package com.vanpra.composematerialdialogs

import android.graphics.Color.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.state
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.material.TextButton
import com.vanpra.composematerialdialogs.ui.ComposeMaterialDialogsTheme
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMaterialDialogsTheme {
                val exampleText = """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum hendrerit risus eu sem aliquam rhoncus. Aliquam ullamcorper tincidunt elit,in aliquam sapien."""
                val selectedDateTime = state { LocalDate.now() }
                val colors = listOf(Color.Red, Color.Green, Color.Blue)
                val subColors = listOf(
                    listOf(Color(0xFFEF5350), Color(0xFFF44336), Color(0xFFE53935)),
                    listOf(Color(0xFF66BB6A), Color(0xFF4CAF50), Color(0xFF43A047)),
                    listOf(Color(0xFF42A5F5), Color(0xFF2196F3), Color(0xFF1E88E5))
                )


                val dialog = MaterialDialog()
                dialog.build {
//                    title("Use Google's Location")
//                    message(exampleText)
//                    buttons {
//                        negativeButton(text = "Disagree")
//                        positiveButton(text = "Agree")
//                    }
//                    datepicker(selectedDateTime.value) { date ->
//                        selectedDateTime.value = date
//                    }
                    title("Select a primary color")
                    colorChooser(colors = ColorPalette.Primary, subColors = ColorPalette.PrimarySub)
                    buttons {
                        negativeButton(text = "Cancel")
                        positiveButton(text = "Ok")
                    }

                }

                TextButton(onClick = {

//                    com.afollestad.materialdialogs.MaterialDialog(this).show {
//                        title(text = "Colors")
//                        colorChooser(ColorPalette.Primary, subColors = ColorPalette.PrimarySub, allowCustomArgb = true) { dialog, color ->
//                            // Use color integer
//                        }
//                        positiveButton(text = "Select")
//                    }
                    dialog.show()
                }) {
                    Text("SHOW")
                }
            }
        }
    }
}