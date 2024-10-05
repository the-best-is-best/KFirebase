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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.firebase_messaging.KFirebaseMessaging
import io.github.sample.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    val fcm = KFirebaseMessaging.create()
    LaunchedEffect(Unit) {

        fcm.setNotificationClickedListener { it ->
            it.onSuccess {
                println("key 1 clicked ${it?.get("token")}")
            }
        }

        fcm.setNotificationListener { it ->
            it.onSuccess {
                println("key 1 received ${it?.get("email")}")
            }
        }
        fcm.setTokenListener { it ->
            it.onSuccess {
                println("user token $it")
            }
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
            fcm.requestAuthorization()
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
            fcm.subscribeTopic("topic_test")
        }) {
            Text("subscribe topic")
        }

        Spacer(Modifier.height(30.dp))
        ElevatedButton(onClick = {
            fcm.unsubscribeTopic("topic_test")
        }) {
            Text("un subscribe topic")
        }
    }
}
