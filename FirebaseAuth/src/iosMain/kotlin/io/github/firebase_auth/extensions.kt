package io.github.firebase_auth

import cocoapods.FirebaseAuth.FIRUser
import cocoapods.FirebaseAuth.FIRUserMetadata
import platform.Foundation.NSDate
import platform.Foundation.NSError


fun FIRUserMetadata.toModel(): KFirebaseUserMetaData = KFirebaseUserMetaData(
    creationTime = this.creationDate()?.convertNSDateToDouble(),
    lastSignInTime = this.lastSignInDate()?.convertNSDateToDouble()
)


fun NSDate.convertNSDateToDouble(): Double {
    return this.timeIntervalSinceReferenceDate
}


fun FIRUser.toModel(): KFirebaseUser {
    return KFirebaseUser(
        uid = this.uid(),
        displayName = this.displayName(),
        email = this.email(),
        phoneNumber = this.phoneNumber(),
        photoURL = this.photoURL()?.absoluteString(),
        isAnonymous = this.isAnonymous(),
        isEmailVerified = this.isEmailVerified(),
        metaData = this.metadata().toModel(),
    )
}


fun NSError.convertNSErrorToException(): Exception {
    return this.let {
        FirebaseAuthException(
            message = it.localizedDescription,
            cause = Throwable(it.localizedFailureReason) // You can customize this as needed
        )
    }
}

class FirebaseAuthException(message: String?, cause: Throwable? = null) : Exception(message, cause)


