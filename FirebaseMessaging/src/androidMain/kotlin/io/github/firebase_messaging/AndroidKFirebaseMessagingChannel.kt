package io.github.firebase_messaging

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class AndroidKFirebaseMessagingChannel(private val context: Activity) {


    companion object {
        var icon: Int? = null
        var id: String? = null
    }

    // Initialize Notification Channel
    fun initChannel(id: String, name: String, icon: Int) {
        AndroidKFirebaseMessagingChannel.icon = icon
        AndroidKFirebaseMessagingChannel.id = id
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                id,
                name,
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


}
