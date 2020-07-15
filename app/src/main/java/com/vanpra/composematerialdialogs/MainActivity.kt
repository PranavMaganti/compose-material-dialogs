package com.vanpra.composematerialdialogs

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
                    colorpicker(colors)
                    buttons {
                        negativeButton(text = "Cancel")
                        positiveButton(text = "Ok")
                    }

                }

                TextButton(onClick = { dialog.show() }) {
                    Text("SHOW")
                }
            }
        }
    }
}