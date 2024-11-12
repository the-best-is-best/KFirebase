package io.github.sample

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.firebase_core.KFirebaseCore
import io.github.firebase_messaging.KFirebaseMessaging
import io.github.sample.theme.AppTheme
import io.tbib.klocal_notification.LocalNotification
import kotlinx.coroutines.launch

@Composable
internal fun AppFCM() = AppTheme {
    val fcm = KFirebaseMessaging
    var notificationValue by remember { mutableStateOf("") }
    val app = KFirebaseCore.app()
    println(app.options) // Check this log
    val scope = rememberCoroutineScope()
    // Log when setting listeners
    LocalNotification.setNotificationClickedListener {
        println("Notification clicked data: $it")
        notificationValue = "Notification clicked data: ${it.get("token").toString()}"

    }
    LocalNotification.setNotificationReceivedListener {
        println("Notification received data: $it")
        notificationValue = "Notification received data: ${it.get("token").toString()}"

        }

    fcm.setTokenListener {
        println("User token: $it")

    }




    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ElevatedButton(onClick = {
                scope.launch {
                    val res = LocalNotification.requestAuthorization()
                    println("per state $res")
                    }

            }) {
                Text("Request permissions")
            }
            Spacer(Modifier.height(30.dp))


            ElevatedButton(onClick = {
                scope.launch {
                    val res = fcm.getToken()

                    println("token $res")
                }
            }) {
                Text("Get token")
            }
            Spacer(Modifier.height(30.dp))
            ElevatedButton(onClick = {
                scope.launch {
                    val res = fcm.subscribeTopic("topic_test")
                    res.onSuccess {
                        println("sub to topic correctly")
                    }
                    res.onFailure {
                        println("sub to topic ${it.message}")
                    }
                }
            }) {
                Text("subscribe topic")
            }

            Spacer(Modifier.height(30.dp))
            ElevatedButton(onClick = {
                scope.launch {
                    val res = fcm.unsubscribeTopic("topic_test")
                    res.onSuccess {
                        println("un sub to topic correctly")
                    }
                    res.onFailure {
                        println("un sub to topic ${it.message}")
                    }
                }

            }) {
                Text("un subscribe topic")
            }
            Spacer(Modifier.height(30.dp))

            Text(notificationValue)

            Spacer(Modifier.height(30.dp))
            ElevatedButton(onClick = {
                io.github.firebase_analytics.KFirebaseAnalytics()
                    .logEvent("haha", mapOf("test1" to 1, "test2" to 2))
            }) {
                Text("Sent analytics")
            }
        }
    }
}
