// androidMain/src/io/github/firebase_messaging/KFirebaseMessagingImpl.kt

package io.github.firebase_messaging

import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import io.tbib.klocal_notification.LocalNotification
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual object KFirebaseMessaging {
    private var tokenListener: ((String?) -> Unit)? = null


    actual fun setTokenListener(callback: (String?) -> Unit) {
        tokenListener = callback
    }



    actual suspend fun getToken(): Result<String?> {
        return suspendCancellableCoroutine { cont ->

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(Result.success(task.result))
                } else {
                    cont.resume(
                        Result.failure(
                            task.exception ?: Exception("Failed to get token")
                        )
                    )
                }
            }
        }
    }

    actual suspend fun subscribeTopic(name: String): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            FirebaseMessaging.getInstance().subscribeToTopic(name)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(Result.success(true))
                    } else {
                        cont.resume(Result.failure(Exception(task.exception)))
                    }
                }
        }
    }

    actual suspend fun unsubscribeTopic(name: String): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            FirebaseMessaging.getInstance().unsubscribeFromTopic(name)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(Result.success(true))
                    } else {
                        cont.resume(Result.failure(Exception(task.exception)))
                    }
                }
        }
    }

    internal fun notifyTokenRefreshed(newToken: String) {
        tokenListener?.invoke(newToken)
    }


    fun notifyNotificationClicked(dataBundle: Bundle) {
        if (!dataBundle.isEmpty()) {
            // Create a map to store the key-value pairs
            val dataMap = mutableMapOf<String, String>()

            // Iterate over the keys in the Bundle (extras)
            for (key in dataBundle.keySet()) {
                // Get the value associated with the key
                val value = dataBundle.getString(key)
                // Add to the map if the value is not null
                if (value != null) {
                    dataMap[key] = value
                }
            }

            // Convert the map to a JSON string using Gson
            val jsonString = Gson().toJson(dataMap)
            LocalNotification.notifyNotificationListener(jsonString)

        }
    }

    actual fun deleteToken() {
        FirebaseMessaging.getInstance().deleteToken()
    }
}

