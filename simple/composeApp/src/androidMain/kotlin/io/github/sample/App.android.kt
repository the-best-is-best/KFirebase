package io.github.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.firebase_core.AndroidKFirebaseCore
import io.github.firebase_messaging.AndroidKFirebaseMessagingChannel
import io.github.firebase_messaging.KFirebaseMessagingImpl
import io.github.vinceglb.filekit.core.FileKit

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FileKit.init(this)
        AndroidKFirebaseCore.initialize(this)
        AndroidKFirebaseMessagingChannel(this).initChannel(
            "fcm",
            "fcm notification",
            R.drawable.ic_notification
        )
        val dataBundle = intent.extras
        if (dataBundle != null) {
            KFirebaseMessagingImpl.notifyNotificationBackgroundClicked(dataBundle)
        }
        setContent { App() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val dataBundle = intent.extras
        if (dataBundle != null) {
            // Now pass the JSON string to the notifyNotificationClicked method
            KFirebaseMessagingImpl.notifyNotificationBackgroundClicked(dataBundle)

            // Logging for debugging purposes
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}
