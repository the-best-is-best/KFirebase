package io.github.firebase_firestore

import platform.Foundation.NSError

fun NSError.convertNSErrorToException(): Exception {
    return this.let {
        FirebaseFirestoreException(
            message = it.localizedDescription,
            cause = Throwable(it.localizedFailureReason) // You can customize this as needed
        )
    }
}

class FirebaseFirestoreException(message: String?, cause: Throwable? = null) :
    Exception(message, cause)
