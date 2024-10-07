package io.github.firebase_crashlytics

expect class KFirebaseAnalytics() {
    fun logEvent(eventName: String, params: Map<String, Any>)

}