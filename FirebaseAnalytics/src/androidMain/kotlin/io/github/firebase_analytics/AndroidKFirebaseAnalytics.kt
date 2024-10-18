package io.github.firebase_analytics

import android.app.Activity
import java.lang.ref.WeakReference

object AndroidKFirebaseAnalytics {
    private var activity: WeakReference<Activity?> = WeakReference(null)

    internal fun getActivity(): Activity {
        return activity.get()!!
    }

    fun initialization(activity: Activity) {
        this.activity = WeakReference(activity)
    }

}