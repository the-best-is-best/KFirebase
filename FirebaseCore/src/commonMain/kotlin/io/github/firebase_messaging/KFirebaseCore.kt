package io.github.firebase_messaging

import io.github.firebase_messaging.models.KFirebaseAppDetails

expect object KFirebaseCore {
    fun app(): KFirebaseAppDetails
}