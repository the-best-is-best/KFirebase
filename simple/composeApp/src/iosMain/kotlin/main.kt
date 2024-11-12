import androidx.compose.ui.window.ComposeUIViewController
import io.github.sample.AppFCM
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController { AppFCM() }
}
