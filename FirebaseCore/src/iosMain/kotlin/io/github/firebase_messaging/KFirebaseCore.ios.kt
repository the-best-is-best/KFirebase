package io.github.firebase_messaging

import cocoapods.FirebaseCore.FIRApp
import io.github.firebase_messaging.models.KFirebaseAppDetails
import io.github.firebase_messaging.models.KFirebaseOptions
import kotlinx.cinterop.ExperimentalForeignApi

actual object KFirebaseCore {
    @OptIn(ExperimentalForeignApi::class)
    actual fun app(): KFirebaseAppDetails {
        val appDetails = FIRApp.defaultApp()

        return KFirebaseAppDetails(
            name = appDetails?.name,
            options = KFirebaseOptions(
                apiKey = appDetails?.options?.APIKey,
                projectId = appDetails?.options?.projectID,
                databaseUrl = appDetails?.options?.databaseURL,
                gcmSenderId = appDetails?.options?.GCMSenderID,
                storageBucket = appDetails?.options?.storageBucket,
                trackingId = appDetails?.options?.trackingID
            )
        )
    }
}