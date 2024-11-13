package io.github.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import io.github.firebase_analytics.AndroidKFirebaseAnalytics
import io.github.firebase_core.AndroidKFirebaseCore
import io.github.firebase_messaging.AndroidKFirebaseMessagingChannel
import io.github.firebase_messaging.KFirebaseMessaging
import io.github.vinceglb.filekit.core.FileKit
import io.tbib.klocal_notification.AndroidKMessagingChannel
import io.tbib.klocal_notification.LocalNotification
import kotlinx.coroutines.launch

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FileKit.init(this)
        AndroidKFirebaseCore.initialize(this)
        AndroidKFirebaseAnalytics.initialization(this)

        AndroidKMessagingChannel.initialization(this)
        AndroidKFirebaseMessagingChannel.initialization(this)
        AndroidKFirebaseMessagingChannel().initChannel(
            "fcm",
            "fcm notification",
            "ic_notification"
        )
        val data = intent.getStringExtra("data")
        if (data != null) {
            LocalNotification.notifyNotificationClickedListener(data)
        }
        setContent { AppFCM() }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val dataBundle = intent.extras
        if (dataBundle != null) {
                KFirebaseMessaging.notifyNotificationBackgroundClicked(dataBundle)

        }
        val data = intent.getStringExtra("data")
        if (data != null) {
            LocalNotification.notifyNotificationClickedListener(data)
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    AppDatabase()
}
