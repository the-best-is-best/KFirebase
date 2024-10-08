package io.github.firebase_analytics

import cocoapods.FirebaseAnalytics.FIRAnalytics


actual class KFirebaseAnalytics actual constructor() {

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