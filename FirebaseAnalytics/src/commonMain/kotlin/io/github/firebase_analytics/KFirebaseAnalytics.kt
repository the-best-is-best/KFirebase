package io.github.firebase_analytics

expect class KFirebaseAnalytics() {
    fun logEvent(eventName: String, params: Map<String, Any>)

}