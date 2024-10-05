import androidx.compose.ui.window.ComposeUIViewController
import io.github.sample.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
//    KIosFirebaseCore.configure()
    return ComposeUIViewController { App() }
}
