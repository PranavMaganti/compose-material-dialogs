package com.vanpra.composematerialdialogs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.state
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.material.TextButton
import com.vanpra.composematerialdialogs.datetime.datepicker
import com.vanpra.composematerialdialogs.datetime.datetimepicker
import com.vanpra.composematerialdialogs.datetime.timepicker
import com.vanpra.composematerialdialogs.ui.ComposeMaterialDialogsTheme
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMaterialDialogsTheme {
                val exampleText = """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum hendrerit risus eu sem aliquam rhoncus. Aliquam ullamcorper tincidunt elit,in aliquam sapien."""
                val selectedDateTime = state { LocalDate.now() }

                val dialog = MaterialDialog()
                dialog.build {
//                    title("Use Google's Location")
//                    message(exampleText)
//                    buttons {
//                        negativeButton(text = "Disagree")
//                        positiveButton(text = "Agree")
//                    }


                    datepicker(selectedDateTime.value) { date ->
                        selectedDateTime.value = date
                    }
                }

                TextButton(onClick = { dialog.show() }) {
                    Text("SHOW")
                }
            }
        }
    }
}