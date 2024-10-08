package io.github.firebase_auth

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import io.github.firebase_auth.KFirebaseAuth.Companion.currentUser

actual class KFirebaseAuth {
    internal var android = FirebaseAuth.getInstance()

    companion object {
        internal var currentUser: FirebaseUser? = null
    }

    init {

        currentUser = android.currentUser
    }

    actual fun currentUser(callback: (Result<KFirebaseUser?>) -> Unit) {

        if (currentUser == null) {
            currentUser = android.currentUser
        }
        callback(Result.success(currentUser?.toModel()))

    }

    actual fun signInAnonymously(callback: (Result<KFirebaseUser?>) -> Unit) {
        android.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    currentUser = task.result.user
                    val userData = task.result?.user?.toModel() // Ensure task.result is not null
                    callback(Result.success(userData))
                } else {
                    // Sign-in failed
                    val exception = task.exception
                    callback(Result.failure(exception ?: Exception("Unknown error occurred.")))
                }
            }
    }

    actual fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        callback: (Result<KFirebaseUser?>) -> Unit
    ) {
        android.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    currentUser = task.result.user
                    val userData = task.result?.user?.toModel() // Ensure task.result is not null
                    callback(Result.success(userData))
                } else {
                    // Sign-in failed
                    val exception = task.exception
                    callback(Result.failure(exception ?: Exception("Unknown error occurred.")))
                }
            }
    }

    actual fun signInWithEmailAndPassword(
        email: String,
        password: String,
        callback: (Result<KFirebaseUser?>) -> Unit
    ) {
        android.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    currentUser = task.result.user
                    val userData = task.result?.user?.toModel() // Ensure task.result is not null
                    callback(Result.success(userData))
                } else {
                    // Sign-in failed
                    val exception = task.exception
                    callback(Result.failure(exception ?: Exception("Unknown error occurred.")))
                }
            }
    }

    actual fun setLanguageCode(locale: String) {
        android.setLanguageCode(locale)
    }

    actual fun kUpdateProfile(
        displayName: String?,
        photoUrl: String?,
        callback: (Result<Boolean?>) -> Unit
    ) {
        if (currentUser != null) {
            // Create a profile update request
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName ?: currentUser!!.displayName)
                .apply {
                    // Conditionally set the photo URI if it's not null
                    photoUrl?.let { photoUri = Uri.parse(it) }
                }
                .build()

            // Update the profile
            currentUser!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(Result.success(true)) // Successful update
                    } else {
                        callback(
                            Result.failure(
                                task.exception ?: Exception("Unknown error occurred.")
                            )
                        ) // Handle failure
                    }
                }
        } else {
            callback(Result.failure(Exception("No user is currently signed in."))) // No signed-in user
        }
    }

    actual fun signInWithCredential(
        credential: AuthCredential,
        callback: (Result<KFirebaseUser?>) -> Unit
    ) {
        android.signInWithCredential(credential.android).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign-in successful
                currentUser = task.result.user
                val userData = task.result?.user?.toModel() // Ensure task.result is not null
                callback(Result.success(userData))
            } else {
                // Sign-in failed
                val exception = task.exception
                callback(Result.failure(exception ?: Exception("Unknown error occurred.")))
            }
        }


    }

    actual fun isLinkEmail(email: String): Boolean {
       return  android.isSignInWithEmailLink(email)
    }

    actual fun confirmPasswordReset(
        code: String,
        newPassword: String,
        callback: (Result<Boolean?>) -> Unit
    ) {
        android.confirmPasswordReset(code, newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in successful

                    callback(Result.success(task.isSuccessful))
                } else {
                    // Sign-in failed
                    val exception = task.exception
                    callback(Result.failure(exception ?: Exception("Unknown error occurred.")))
                }

            }
    }

    actual fun addListenerAuthStateChange(callback: (Result<KFirebaseUser?>) -> Unit) {
        android.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                callback(Result.success(currentUser.toModel()))
            } else {
                callback(Result.success(null))
            }

        }

    }

    actual fun addListenerIdTokenChanged(callback: (Result<KFirebaseUser?>) -> Unit) {
        android.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                callback(Result.success(currentUser.toModel()))
            } else {
                callback(Result.success(null))
            }

        }
    }
}

actual fun KFirebaseUser.kUpdateEmail(
    email: String,
    callback: (Result<Boolean?>) -> Unit
) {
    currentUser?.updateEmail(email)?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Sign-in successful
            callback(Result.success(true))
        } else {
            // Sign-in failed
            val exception = task.exception
            callback(Result.failure(exception ?: Exception("Unknown error occurred.")))
        }
    }

}

actual fun KFirebaseUser.kSendEmailVerification(callback: (Result<Boolean?>) -> Unit) {
    currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Sign-in successful

            callback(Result.success(true))
        } else {
            // Sign-in failed
            val exception = task.exception
            callback(Result.failure(exception ?: Exception("Unknown error occurred.")))
        }
    }

}

actual fun KFirebaseUser.kResetPassword(
    password: String,
    callback: (Result<Boolean?>) -> Unit
) {
    currentUser?.updatePassword(password)?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Sign-in successful

            callback(Result.success(true))
        } else {
            // Sign-in failed
            val exception = task.exception
            callback(Result.failure(exception ?: Exception("Unknown error occurred.")))
        }

    }
}

actual fun KFirebaseUser.kDelete(callback: (Result<Boolean?>) -> Unit) {
    currentUser?.delete()?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Sign-in successful
            currentUser = null
            callback(Result.success(true))
        } else {
            // Sign-in failed
            val exception = task.exception
            callback(Result.failure(exception ?: Exception("Unknown error occurred.")))
        }

    }
}

actual fun KFirebaseUser.kSignOut(callback: (Result<Boolean?>) -> Unit) {
    FirebaseAuth.getInstance().signOut()
    currentUser = null

}

actual fun KFirebaseUser.linkProvider(
    credential: AuthCredential,
    callback: (Result<KFirebaseUser?>) -> Unit
) {
    currentUser?.linkWithCredential(credential.android)
        ?.addOnCompleteListener { task->
            if (task.isSuccessful) {
                // Sign-in successful
                currentUser = task.result.user
                if(currentUser != null) {
                    callback(Result.success(currentUser!!.toModel()))
                }else{
                    callback(Result.success(null))
                }
            } else {
                // Sign-in failed
                val exception = task.exception
                callback(Result.failure(exception ?: Exception("Unknown error occurred.")))
            }
        }
}