package io.github.firebase_analytics

import com.google.firebase.analytics.FirebaseAnalytics
import io.github.firebase_core.AndroidKFirebaseCore

actual class KFirebaseAnalytics actual constructor() {
    actual fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(AndroidKFirebaseCore.context)

        val bundle = android.os.Bundle()
        params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)

    }

}