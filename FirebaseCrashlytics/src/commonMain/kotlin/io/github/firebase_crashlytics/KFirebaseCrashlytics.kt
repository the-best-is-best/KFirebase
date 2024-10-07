package io.github.firebase_crashlytics

expect class KFirebaseCrashlytics() {
    fun setCrashlyticsCollectionEnabled(enable: Boolean)
    fun log(msg: String)
    fun recordError(exception: Throwable)
}