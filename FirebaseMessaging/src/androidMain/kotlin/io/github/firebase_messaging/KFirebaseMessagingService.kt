package io.github.firebase_messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class KFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here
        val data = remoteMessage.data
        val convertedData: Map<Any?, *> = data.mapKeys { it.key } // Convert keys
            .mapValues { it.value } // Convert values

        // Notify the listener from the KFirebaseMessagingImpl instance
        (getPlatformFirebaseMessaging() as? KFirebaseMessagingImpl)?.notificationListener?.invoke(
            Result.success(convertedData)
        )
    }

    override fun onNewToken(token: String) {
        // Notify the listener when the token is refreshed
        (getPlatformFirebaseMessaging() as? KFirebaseMessagingImpl)?.tokenListener?.invoke(
            Result.success(
                token
            )
        )
    }
}