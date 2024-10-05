// iosMain/src/io/github/firebase_messaging/KFirebaseMessagingImpl.kt

package io.github.firebase_messaging

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import cocoapods.KFirebaseMessaging.KFirebaseMessaging as FCM

@OptIn(ExperimentalForeignApi::class)
class KFirebaseMessagingImpl : KFirebaseMessaging {
    private var tokenListenerValue by mutableStateOf("")
    private var onNotificationListenerValue by mutableStateOf<Map<Any?, Any?>?>(null)
    private var onNotificationClickedListenerValue by mutableStateOf<Map<Any?, Any?>?>(null)


    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun setTokenListener(callback: (Result<String?>) -> Unit) {
        FCM.shared().setOnTokenReceived { token ->
            scope.launch {
                if (token != null) {
                    if (token != tokenListenerValue) {
                        tokenListenerValue = token
                        callback(Result.success(token))
                    }
                } else {
                    callback(Result.failure(Exception("Token is null")))
                }
            }
        }

    }

    override fun setNotificationListener(callback: (Result<Map<Any?, *>?>) -> Unit) {
        FCM.shared().setOnNotificationReceived { notificationData ->
            scope.launch {
                if (notificationData != onNotificationListenerValue) {
                    onNotificationListenerValue = notificationData
                    callback(Result.success(notificationData))
                }
            }
        }
    }

    override fun setNotificationClickedListener(callback: (Result<Map<Any?, *>?>) -> Unit) {
        FCM.shared().setOnNotificationClicked { notificationData ->
            scope.launch {
                if (onNotificationClickedListenerValue != notificationData) {
                    onNotificationClickedListenerValue = notificationData
                    callback(Result.success(notificationData))
                }
            }
        }
    }

    override fun requestAuthorization() {
        scope.launch {
            FCM.shared().requestAuthorization()
        }
    }

    override fun getToken(callback: (Result<String?>) -> Unit) {
        scope.launch {


            try {


                FCM.shared().getTokenWithCompletion {
                    callback(Result.success(it))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }

    }

    override fun subscribeTopic(name: String) {
        FCM.shared().subscribeTopicWithName(name)
    }

    override fun unsubscribeTopic(name: String) {
        FCM.shared().unsubscribeWithName(name)

    }
}

// Provide the platform-specific implementation
actual fun getPlatformFirebaseMessaging(): KFirebaseMessaging = KFirebaseMessagingImpl()
