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
import kotlinx.coroutines.launch

@Composable
internal fun AppFCM() = AppTheme {
    val fcm = KFirebaseMessaging.create()
    var notificationValue by remember { mutableStateOf("") }
    val app = KFirebaseCore.app()
    println(app.options) // Check this log
    val scope = rememberCoroutineScope()
    // Log when setting listeners
    fcm.setNotificationClickedListener { it ->
        it.onSuccess { data ->
            println("Notification clicked data: $data")
            notificationValue = "Notification clicked data: ${data?.get("token").toString()}"
        }
    }

    fcm.setNotificationListener { it ->
        it.onSuccess { data ->
            println("Notification received data: $data")
            notificationValue = "Notification received data: ${data?.get("token").toString()}"
        }
    }

    fcm.setTokenListener { it ->
        it.onSuccess { token ->
            println("User token: $token")
        }
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
                    val res = fcm.requestAuthorization()
                    res.onSuccess {
                        println("per state $it")
                    }
                }
            }) {
                Text("Request permissions")
            }
            Spacer(Modifier.height(30.dp))


            ElevatedButton(onClick = {
                scope.launch {
                    val res = fcm.getToken()


                    res.onSuccess {
                        println("token $it")
                    }
                    res.onFailure {
                        println("error token $it")
                    }
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
