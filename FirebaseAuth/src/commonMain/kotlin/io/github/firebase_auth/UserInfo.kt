package io.github.firebase_auth

data class KFirebaseUser(
    val uid: String?,
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoURL: String?,
    val isAnonymous: Boolean?,
    val isEmailVerified: Boolean?,
    val metaData: KFirebaseUserMetaData?,

    )

data class KFirebaseUserMetaData(
    val creationTime: Double?,
    val lastSignInTime: Double?
)