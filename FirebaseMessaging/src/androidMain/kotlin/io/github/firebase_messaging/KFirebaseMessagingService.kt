package io.github.firebase_messaging

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlin.random.Random

class KFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

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
        val convertedData: Map<Any?, *> = data.mapKeys { it.key }.mapValues { it.value }

        // Optionally notify that a data message was received
        KFirebaseMessagingImpl.notifyNotificationReceived(convertedData)
    }

    @SuppressLint("LaunchActivityFromNotification")
    private fun handleNotificationMessage(notificationDetails: RemoteMessage) {
        // Create an Intent for handling notification clicks
        val intent = Intent(this, KFirebaseNotificationClickReceiver::class.java).apply {
            action = "com.KFirebaseMessaging.ACTION_NOTIFICATION_CLICKED"
            putExtra("notification_clicked", true)
            putExtra("data", Gson().toJson(notificationDetails.data))
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        // Create a PendingIntent to be triggered when the notification is clicked
        val pendingIntent = PendingIntent.getBroadcast(
            AndroidKFirebaseMessagingChannel.getActivity(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Ensure notification details are available
        val notificationDetail = notificationDetails.notification
        if (notificationDetail != null) {
            // Ensure icon and channel ID are initialized (otherwise throw an error)
            val icon = AndroidKFirebaseMessagingChannel.icon
                ?: throw IllegalStateException("Notification icon not initialized")
            val channelId = AndroidKFirebaseMessagingChannel.id
                ?: throw IllegalStateException("Notification channel ID not initialized")

            // Build the notification
            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle(notificationDetail.title)  // Assuming title is always provided
                .setContentText(notificationDetail.body)    // Assuming body is always provided
                .setSmallIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .build()

            // Notify the user with the notification
            notificationManager.notify(Random.nextInt(), notification)
        } else {
            throw IllegalArgumentException("Notification details are null")
        }
    }

    override fun onNewToken(token: String) {
        KFirebaseMessagingImpl.notifyTokenRefreshed(token)
    }
}
