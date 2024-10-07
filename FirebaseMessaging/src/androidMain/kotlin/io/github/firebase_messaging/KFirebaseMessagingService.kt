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
import io.github.firebase_core.AndroidKFirebaseCore
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
                print("has data $hasData")
            }
        }
        if (remoteMessage.notification != null && remoteMessage.notification!!.title != null) {

            handleNotificationMessage(remoteMessage)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val convertedData:
                Map<Any?, *> = data.mapKeys { it.key }.mapValues { it.value }

        // Optionally notify that a data message was received
        KFirebaseMessagingImpl.notifyNotificationReceived(convertedData)
    }

    @SuppressLint("LaunchActivityFromNotification")
    private fun handleNotificationMessage(notificationDetails: RemoteMessage) {
        // Create an Intent for handling notification clicks
        val intent = Intent(this, KFirebaseNotificationClickReceiver::class.java).apply {
            action = "com.KFirebaseMessaging.ACTION_NOTIFICATION_CLICKED" // Your custom action
            putExtra("notification_clicked", true)
            putExtra("data", Gson().toJson(notificationDetails.data)) // Send notification details
        }

        // Create a PendingIntent to be triggered when the notification is clicked
        val pendingIntent = PendingIntent.getBroadcast(
            AndroidKFirebaseCore.context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(this, AndroidKFirebaseMessagingChannel.id!!)
            .setContentTitle(notificationDetails.notification?.title)
            .setContentText(notificationDetails.notification?.body)
            .setSmallIcon(AndroidKFirebaseMessagingChannel.icon!!)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Use the PendingIntent for handling clicks
            .setAutoCancel(true)
            .build()

        // Notify the user with the notification
        notificationManager.notify(Random.nextInt(), notification)
    }


    override fun onNewToken(token: String) {
        KFirebaseMessagingImpl.notifyTokenRefreshed(token)
    }
}
