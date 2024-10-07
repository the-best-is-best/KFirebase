package io.github.firebase_crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual class KFirebaseCrashlytics actual constructor() {
    val firebaseCrashlytics = FirebaseCrashlytics.getInstance()

    actual fun setCrashlyticsCollectionEnabled(enable: Boolean) {
        firebaseCrashlytics.isCrashlyticsCollectionEnabled = enable
    }

    actual fun log(msg: String) {
        firebaseCrashlytics.log(msg)
    }

    actual fun recordError(exception: Throwable) {
        firebaseCrashlytics.recordException(exception)
    }
}