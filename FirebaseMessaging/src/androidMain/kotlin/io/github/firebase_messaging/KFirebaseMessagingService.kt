package io.github.firebase_messaging

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.tbib.klocal_notification.LocalNotification
import io.tbib.klocal_notification.NotificationConfig
import kotlin.math.absoluteValue
import kotlin.random.Random

class KFirebaseMessagingService : FirebaseMessagingService() {



    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle both notification and data messages
        remoteMessage.data.isNotEmpty().let { hasData ->
            if (hasData) {
                handleDataMessage(remoteMessage.data)

            }
        }

        // If notification part is included
        if (remoteMessage.notification != null) {
            handleNotificationMessage(remoteMessage)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val convertedData: Map<Any?, *> = data.map { it.key to it.value }.toMap()

        // Optionally notify that a data message was received
        KFirebaseMessagingImpl.notifyNotificationReceived(convertedData)
    }

    @SuppressLint("LaunchActivityFromNotification")
    private fun handleNotificationMessage(notificationDetails: RemoteMessage) {
        // Ensure notification details are available
        val notificationDetail = notificationDetails.notification
        if (notificationDetail != null) {
            val data: Map<Any?, *> =
                notificationDetails.data.entries.associate { it.key to it.value }

            if (notificationDetail.title != null && notificationDetail.body != null) {
                LocalNotification.showNotification(
                    NotificationConfig(
                        Random.nextInt().absoluteValue,
                        AndroidKFirebaseMessagingChannel.id!!,
                        notificationDetails.notification!!.title!!,
                        notificationDetails.notification!!.body!!,
                        data = data,
                        smallIcon = AndroidKFirebaseMessagingChannel.icon!!
                    )

                )
            }
        } else {
            throw IllegalArgumentException("Notification details are null")
        }
    }

    override fun onNewToken(token: String) {
        KFirebaseMessagingImpl.notifyTokenRefreshed(token)
    }
}
