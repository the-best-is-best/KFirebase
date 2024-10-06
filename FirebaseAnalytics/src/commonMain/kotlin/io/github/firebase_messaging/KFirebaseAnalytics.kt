package io.github.firebase_messaging

expect class KFirebaseAnalytics() {
    fun logEvent(eventName: String, params: Map<String, Any>)

}