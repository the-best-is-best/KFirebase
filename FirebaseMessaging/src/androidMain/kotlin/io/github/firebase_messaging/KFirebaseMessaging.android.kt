// androidMain/src/io/github/firebase_messaging/KFirebaseMessagingImpl.kt

package io.github.firebase_messaging

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.kpermissions.enum.EnumAppPermission
import io.github.kpermissions.handler.PermissionHandler

object KFirebaseMessagingImpl : KFirebaseMessaging {
    private var tokenListener: ((Result<String?>) -> Unit)? = null
    private var notificationListener: ((Result<Map<Any?, *>?>) -> Unit)? = null
    private var notificationClickedListener: ((Result<Map<Any?, *>?>) -> Unit)? = null

    override fun setTokenListener(callback: (Result<String?>) -> Unit) {
        tokenListener = callback
    }

    override fun setNotificationListener(callback: (Result<Map<Any?, *>?>) -> Unit) {
        notificationListener = callback
    }

    override fun setNotificationClickedListener(callback: (Result<Map<Any?, *>?>) -> Unit) {
        notificationClickedListener = callback
    }

    override fun requestAuthorization(callback: (Result<Boolean>) -> Unit) {
        val permission = PermissionHandler()
        permission.requestPermission(EnumAppPermission.NOTIFICATION) { granted ->
            if (!granted) {
                permission.openAppSettings()
            }
            callback(Result.success(granted))
        }
    }

    override fun getToken(callback: (Result<String?>) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(Result.success(task.result))
            } else {
                callback(Result.failure(task.exception ?: Exception("Failed to get token")))
            }
        }
    }

    override fun subscribeTopic(name: String, callback: (Result<Boolean>) -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(name)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(Result.success(true))
                } else {
                    callback(Result.failure(Exception(task.exception)))
                }
            }
    }

    override fun unsubscribeTopic(name: String, callback: (Result<Boolean>) -> Unit) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(name)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(Result.success(true))
                } else {
                    callback(Result.failure(Exception(task.exception)))
                }
            }
    }

    internal fun notifyTokenRefreshed(newToken: String) {
        tokenListener?.invoke(Result.success(newToken))
    }

    internal fun notifyNotificationReceived(data: Map<Any?, *>) {
        notificationListener?.invoke(Result.success(data))
    }

    internal fun notifyNotificationClicked(dataJson: String) {
        Handler(Looper.getMainLooper()).postDelayed({

            val type =
                object :
                    TypeToken<Map<String, String>>() {}.type // Define the type for deserialization
            val yourDataMap: Map<Any?, *> =
                Gson().fromJson(dataJson, type) // Deserialize back to a map
            if (yourDataMap.isNotEmpty()) {
                notificationClickedListener?.invoke(Result.success(yourDataMap))
            }
        }, 500)
    }

    fun notifyNotificationBackgroundClicked(dataBundle: Bundle) {
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
            notifyNotificationClicked(jsonString)

        }
    }
}

actual fun getPlatformFirebaseMessaging(): KFirebaseMessaging {
    return KFirebaseMessagingImpl
}
