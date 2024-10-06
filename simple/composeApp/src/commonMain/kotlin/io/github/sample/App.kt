package io.github.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.firebase_messaging.KFirebaseCore
import io.github.firebase_messaging.KFirebaseMessaging
import io.github.sample.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    val fcm = KFirebaseMessaging.create()
    var notificationValue by remember { mutableStateOf("") }
        val app = KFirebaseCore.app()
    println(app.options) // Check this log

    // Log when setting listeners
    println("Setting notification clicked listener")
        fcm.setNotificationClickedListener { it ->
            it.onSuccess { data ->
                println("Notification clicked data: $data")
                notificationValue = "Notification clicked data: ${data?.get("token").toString()}"
            }
        }

    println("Setting notification listener")
        fcm.setNotificationListener { it ->
            it.onSuccess { data ->
                println("Notification received data: $data")
                notificationValue = "Notification received data: ${data?.get("token").toString()}"
            }
        }

    println("Setting token listener")
        fcm.setTokenListener { it ->
            it.onSuccess { token ->
                println("User token: $token")
            }
        }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ElevatedButton(onClick = {
            fcm.requestAuthorization(callback = {
                println("per state $it")
            })
        }) {
            Text("Request permissions")
        }
        Spacer(Modifier.height(30.dp))
        ElevatedButton(onClick = {
            fcm.getToken {
                it.onSuccess {
                    println("token $it")
                }
                it.onFailure {
                    println("error token $it")
                }
            }
        }) {
            Text("Get token")
        }
        Spacer(Modifier.height(30.dp))
        ElevatedButton(onClick = {
            fcm.subscribeTopic("topic_test", callback = {
                it.onSuccess {
                    println("sub to topic correctly")
                }
                it.onFailure {
                    println("sub to topic ${it.message}")
                }
            })
        }) {
            Text("subscribe topic")
        }

        Spacer(Modifier.height(30.dp))
        ElevatedButton(onClick = {
            fcm.unsubscribeTopic("topic_test", callback = {
                it.onSuccess {
                    println("un sub to topic correctly")
                }
                it.onFailure {
                    println("un sub to topic ${it.message}")
                }
            })
        }) {
            Text("un subscribe topic")
        }
        Spacer(Modifier.height(30.dp))

        Text(notificationValue)
    }
}
