package io.github.firebase_auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseUserMetadata

fun FirebaseUser.toModel(): KFirebaseUser =
    KFirebaseUser(
        uid = this.uid,
        displayName = this.displayName,
        email = this.email,
        phoneNumber = this.phoneNumber,
        photoURL = this.phoneNumber,
        isAnonymous = this.isAnonymous,
        isEmailVerified = this.isEmailVerified,
        metaData = this.metadata?.toModel()
    )

fun FirebaseUserMetadata.toModel(): KFirebaseUserMetaData =
    KFirebaseUserMetaData(
        creationTime = this.creationTimestamp.toDouble(),
        lastSignInTime = this.lastSignInTimestamp.toDouble()
    )