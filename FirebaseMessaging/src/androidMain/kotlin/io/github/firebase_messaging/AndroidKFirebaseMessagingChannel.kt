package io.github.firebase_messaging

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import java.lang.ref.WeakReference

class AndroidKFirebaseMessagingChannel {


    companion object {
        private var activity: WeakReference<Activity?> = WeakReference(null)

        internal var icon: String? = null
        internal var id: String? = null

        internal fun getActivity(): Activity {
            return activity.get()!!
        }

        fun initialization(activity: Activity) {
            this.activity = WeakReference(activity)
        }
    }


    // Initialize Notification Channel
    fun initChannel(id: String, name: String, icon: String, channelDesc: String? = null) {
        Companion.icon = icon
        Companion.id = id
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                id,
                name,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = channelDesc ?: "Default channel description"
            }



            val notificationManager =
                getActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


}
