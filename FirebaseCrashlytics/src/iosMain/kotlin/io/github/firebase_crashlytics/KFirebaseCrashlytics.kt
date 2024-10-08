package io.github.firebase_crashlytics

import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import platform.Foundation.NSError
import platform.Foundation.NSLocalizedDescriptionKey


actual class KFirebaseCrashlytics {


    actual fun setCrashlyticsCollectionEnabled(enable: Boolean) {
        FIRCrashlytics().setCrashlyticsCollectionEnabled(enable)

    }

    actual fun log(msg: String) {
        FIRCrashlytics().log(msg)

    }

    actual fun recordError(exception: Throwable) {
        FIRCrashlytics().recordError(exception.asNSError())

    }

    private fun Throwable.asNSError(): NSError {
        val userInfo = mutableMapOf<Any?, Any>()
        userInfo["KotlinException"] = this
        val message = message
        if (message != null) {
            userInfo[NSLocalizedDescriptionKey] = message
        }
        return NSError.errorWithDomain(this::class.qualifiedName, 0, userInfo)
    }
}