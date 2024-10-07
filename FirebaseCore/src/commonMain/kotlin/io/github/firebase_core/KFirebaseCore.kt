package io.github.firebase_core

import io.github.firebase_core.models.KFirebaseAppDetails

expect object KFirebaseCore {
    fun app(): KFirebaseAppDetails
}