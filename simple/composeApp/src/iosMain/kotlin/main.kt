import androidx.compose.ui.window.ComposeUIViewController
import io.github.sample.AppFirestore
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController { AppFirestore() }
}
