package io.github.firebase_messaging

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PersistableBundle
import android.util.Log

class KFirebaseNotificationClickReceiver : BroadcastReceiver() {
    @SuppressLint("LongLogTag", "NewApi")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NotificationClickReceiver", "Notification clicked")

        val jobScheduler = context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val componentName = ComponentName(context, KFirebaseNotificationClickJobService::class.java)

        val extras = PersistableBundle()

        intent?.extras?.let { bundle ->
            for (key in bundle.keySet()) {
                when {
                    bundle.containsKey(key) && bundle.getString(key) != null -> extras.putString(
                        key,
                        bundle.getString(key)
                    )

                    bundle.containsKey(key) && bundle.getInt(key) != 0 -> extras.putInt(
                        key,
                        bundle.getInt(key)
                    )

                    bundle.containsKey(key) && bundle.getBoolean(key) -> extras.putBoolean(
                        key,
                        bundle.getBoolean(key)
                    )

                    bundle.containsKey(key) && bundle.getLong(key) != 0L -> extras.putLong(
                        key,
                        bundle.getLong(key)
                    )

                    bundle.containsKey(key) && bundle.getDouble(key) != 0.0 -> extras.putDouble(
                        key,
                        bundle.getDouble(key)
                    )
                    // Add more supported types as needed
                    else -> Log.w(
                        "NotificationClickReceiver",
                        "Unsupported or null bundle value: $key"
                    )
                }
            }
        }
        val jobInfo = JobInfo.Builder(1234, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .setExtras(extras)
            .build()

        jobScheduler.schedule(jobInfo)
    }

}

class KFirebaseNotificationClickJobService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("KFirebaseJobService", "Job started")

        // Handle your background task (e.g., processing the notification click)
        val dataJson = params?.extras?.getString("data")
        if (!dataJson.isNullOrEmpty()) {
            KFirebaseMessagingImpl.notifyNotificationClicked(dataJson)
        }

        return false // Return false if there's no work to do in the background thread
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("KFirebaseJobService", "Job stopped")
        return false
    }
}
