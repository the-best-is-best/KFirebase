// androidMain/src/io/github/firebase_messaging/KFirebaseMessagingImpl.kt

package io.github.firebase_messaging

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging


class KFirebaseMessagingImpl : KFirebaseMessaging {
    internal var tokenListener: ((Result<String?>) -> Unit)? = null
    internal var notificationListener: ((Result<Map<Any?, *>?>) -> Unit)? = null
    private var notificationClickedListener: ((Result<Map<Any?, *>?>) -> Unit)? = null


    override fun setTokenListener(callback: (Result<String?>) -> Unit) {
        this.tokenListener = callback
        // Get the current token
        getToken(callback)
    }

    override fun setNotificationListener(callback: (Result<Map<Any?, *>?>) -> Unit) {
        this.notificationListener = callback
    }

    override fun setNotificationClickedListener(callback: (Result<Map<Any?, *>?>) -> Unit) {
        this.notificationClickedListener = callback
    }

    override fun requestAuthorization() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                KAndroidFirebaseCore.context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    KAndroidFirebaseCore.context,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )

            }
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

    override fun subscribeTopic(name: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(name)
            .addOnCompleteListener { task ->

            }
    }

    override fun unsubscribeTopic(name: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(name)
            .addOnCompleteListener { task ->
                // Handle completion
            }
    }


}

actual fun getPlatformFirebaseMessaging(): KFirebaseMessaging {
    return KFirebaseMessagingImpl()
}
