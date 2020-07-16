package com.vanpra.composematerialdialogs

import android.graphics.Color.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.state
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.material.TextButton
import com.afollestad.materialdialogs.color.colorChooser
//import com.afollestad.materialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.ui.ComposeMaterialDialogsTheme
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMaterialDialogsTheme {
            }
        }
    }
}