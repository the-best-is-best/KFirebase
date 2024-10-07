package io.github.firebase_crashlytics.models

data class KFirebaseAppDetails(
    val name: String?,
    val options: KFirebaseOptions
)

data class KFirebaseOptions(
    val apiKey: String?,
    val projectId: String?,
    val databaseUrl: String?,
    val gcmSenderId: String?,
    val storageBucket: String?,
    val trackingId: String?,


    )