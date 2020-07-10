package com.vanpra.composematerialdialogs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.state
import androidx.ui.core.setContent
import com.vanpra.composematerialdialogs.ui.ComposeMaterialDialogsTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMaterialDialogsTheme {
                val dialogShowing = state { false }

                MaterialDialog(dialogShowing).draw {
                    title("This is a simple dialog")
                    message("""Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum
                    hendrerit risus eu sem aliquam rhoncus. Aliquam ullamcorper tincidunt elit,
                    in aliquam sapien. Nunc a porttitor nulla, at semper orci.""")
                    positiveButton("Ok")
                    negativeButton("Cancel")
                }
//                val showing = state { true }
//
//                MaterialDialog(showing).draw {
//                    title("Use Google's Location")
//                    message(
//                        """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum
//                    hendrerit risus eu sem aliquam rhoncus. Aliquam ullamcorper tincidunt elit,
//                    in aliquam sapien. Nunc a porttitor nulla, at semper orci. Etiam at orci in ante
//                    sagittis facilisis quis eget nisi. Praesent volutpat sem ac quam rutrum,
//                    sit amet hendrerit ligula tempor."""
//                    )

//                    negativeButton(text = "Disagree", onClick = { showing.value = false })
//                    positiveButton(text = "Agree", onClick = {})
//                }
            }
        }
    }
}