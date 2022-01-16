import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.vanpra.common.DialogDemos


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            DialogDemos()
        }
    }
}