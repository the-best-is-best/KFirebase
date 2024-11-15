package io.github.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.firebase_analytics.AndroidKFirebaseAnalytics
import io.github.firebase_core.AndroidKFirebaseCore
import io.github.firebase_messaging.AndroidKFirebaseMessagingChannel
import io.github.firebase_messaging.KFirebaseMessaging
import io.github.vinceglb.filekit.core.FileKit
import io.tbib.klocal_notification.AndroidKMessagingChannel

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


        setContent { AppFCM() }

        val dataBundle = intent.extras
        if (dataBundle != null) {
            KFirebaseMessaging.notifyNotificationClicked(dataBundle)

        }


    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val dataBundle = intent.extras
        if (dataBundle != null) {
            KFirebaseMessaging.notifyNotificationClicked(dataBundle)

        }
    }
}

@Preview
@Composable
fun AppPreview() {
    AppDatabase()
}
