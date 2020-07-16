package com.vanpra.composematerialdialogs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import com.vanpra.composematerialdialogs.ui.ComposeMaterialDialogsTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMaterialDialogsTheme {
                DialogAndShowButton(buttonText = "Basic Dialog With Buttons") {
                    title("Use Location Services?")
                    message("Let us help apps determine location. This means sending anonymous location data to us, even when no apps are running")
                    buttons {
                        negativeButton("Disagree")
                        positiveButton("Agree")
                    }
                }
            }
        }
    }
}