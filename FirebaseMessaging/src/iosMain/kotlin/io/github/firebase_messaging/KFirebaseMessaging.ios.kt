// iosMain/src/io/github/firebase_messaging/KFirebaseMessagingImpl.kt

package io.github.firebase_messaging

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import cocoapods.KFirebaseMessaging.KFirebaseMessaging as FCM


class KFirebaseMessagingImpl : KFirebaseMessaging {
    private var tokenListenerValue by mutableStateOf("")
    private var onNotificationListenerValue by mutableStateOf<Map<Any?, Any?>?>(null)
    private var onNotificationClickedListenerValue by mutableStateOf<Map<Any?, Any?>?>(null)



    override fun setTokenListener(callback: (Result<String?>) -> Unit) {
        FCM.shared().setOnTokenReceived { token ->
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

    override fun setNotificationListener(callback: (Result<Map<Any?, *>?>) -> Unit) {
        FCM.shared().setOnNotificationReceived { notificationData ->
                if (notificationData != onNotificationListenerValue) {
                    onNotificationListenerValue = notificationData
                    callback(Result.success(notificationData))
                }
            }

    }

    override fun setNotificationClickedListener(callback: (Result<Map<Any?, *>?>) -> Unit) {
        FCM.shared().setOnNotificationClicked { notificationData ->
                if (onNotificationClickedListenerValue != notificationData) {
                    onNotificationClickedListenerValue = notificationData
                    callback(Result.success(notificationData))
                }
            }

    }

    override suspend fun requestAuthorization(): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            try {
                FCM.shared().requestAuthorization()
                cont.resume(Result.success(true))
            } catch (e: Exception) {
                cont.resume(Result.failure(Exception(e)))
            }
        }

    }

    override suspend fun getToken(): Result<String?> {
        return suspendCancellableCoroutine { cont ->


            try {


                FCM.shared().getTokenWithCompletion {
                    cont.resume(Result.success(it))
                }
            } catch (e: Exception) {
                cont.resume(Result.failure(e))
            }
        }
    }


    override suspend fun subscribeTopic(name: String): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            try {
                FCM.shared().subscribeTopicWithName(name)
                cont.resume(Result.success(true))
            } catch (e: Exception) {
                cont.resume(Result.failure(Exception(e)))
            }
        }
    }

    override suspend fun unsubscribeTopic(name: String): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            try {
                FCM.shared().unsubscribeWithName(name)
                cont.resume(Result.success(true))
            } catch (e: Exception) {
                cont.resume(Result.failure(Exception(e)))
            }
        }
    }
}

// Provide the platform-specific implementation
actual fun getPlatformFirebaseMessaging(): KFirebaseMessaging = KFirebaseMessagingImpl()
