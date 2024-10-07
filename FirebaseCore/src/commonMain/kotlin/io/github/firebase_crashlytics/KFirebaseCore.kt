package io.github.firebase_crashlytics

import io.github.firebase_crashlytics.models.KFirebaseAppDetails

expect object KFirebaseCore {
    fun app(): KFirebaseAppDetails
}