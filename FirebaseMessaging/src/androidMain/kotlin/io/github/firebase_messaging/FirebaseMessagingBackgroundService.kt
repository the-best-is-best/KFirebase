//package io.github.firebase_messaging
//
//import android.annotation.SuppressLint
//import android.app.job.JobParameters
//import android.app.job.JobService
//import android.util.Log
//
//class KFirebaseMessagingBackgroundService : JobService() {
//
//    private val TAG = "FirebaseMessagingService"
//
//    override fun onStartJob(params: JobParameters?): Boolean {
//        // This will be executed on a background thread
//        handleIncomingMessage(params)
//        return true // Return true if work is ongoing
//    }
//
//    override fun onStopJob(params: JobParameters?): Boolean {
//        // Cleanup any ongoing work if needed
//        return true // Return true to reschedule the job
//    }
//
//    @SuppressLint("NewApi")
//    private fun handleIncomingMessage(params: JobParameters?) {
//        // Handle the incoming message
//        params?.extras?.let { extras ->
//            Log.d("FCM", "Received data in background: ${extras.getString("data")}")
//
//            val dataJson = extras.getString("data")
//            if (!dataJson.isNullOrEmpty()) {
//                // Deserialize the data and handle it
//                KFirebaseMessagingImpl.notifyNotificationClicked(dataJson) // Your method to handle clicked notification
//            } else {
//                Log.d("FCM", "Data JSON is null or empty.")
//            }
//        }
//
//        // Notify that the work is done
//        params?.let {
//            jobFinished(it, false) // Use the provided JobParameters
//        } ?: Log.w(TAG, "JobParameters is null, cannot call jobFinished")
//    }
//}
