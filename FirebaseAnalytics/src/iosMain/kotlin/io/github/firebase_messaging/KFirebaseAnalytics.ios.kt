package io.github.firebase_messaging

import cocoapods.FirebaseAnalytics.FIRAnalytics
import kotlinx.cinterop.ExperimentalForeignApi


actual class KFirebaseAnalytics actual constructor() {
    @OptIn(ExperimentalForeignApi::class)
    actual fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        val paramsMapAny = convertMap(params)

        FIRAnalytics.logEventWithName(eventName, parameters = paramsMapAny)

    }


}

fun convertMap(params: Map<String, Any>): Map<Any?, *> {
    return params.map { (key, value) ->
        key to value
    }.toMap()
}