package io.github.firebase_auth

import cocoapods.FirebaseAuth.FIRActionCodeOperationEmailLink
import cocoapods.FirebaseAuth.FIRActionCodeOperationRecoverEmail
import cocoapods.FirebaseAuth.FIRActionCodeOperationRevertSecondFactorAddition
import cocoapods.FirebaseAuth.FIRActionCodeOperationUnknown
import cocoapods.FirebaseAuth.FIRActionCodeOperationVerifyAndChangeEmail
import cocoapods.FirebaseAuth.FIRActionCodeOperationVerifyEmail
import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRAuthDataResult
import cocoapods.FirebaseAuth.FIRUser
import io.github.firebase_auth.KFirebaseAuth.Companion.currentUser
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.Foundation.NSURL
import kotlin.coroutines.resume

actual class KFirebaseAuth {
    companion object {
        internal var currentUser: FIRUser? = null
    }

    internal var ios: FIRAuth = FIRAuth.auth()

    init {
        currentUser = ios.currentUser()
    }

    actual suspend fun currentUser(): Result<KFirebaseUser?> {
        return try {
            val fireUser = ios.currentUser()
            if (fireUser != null) {
                currentUser = fireUser
                Result.success(fireUser.toModel())
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun signInAnonymously(): Result<KFirebaseUser?> {
        return suspendCancellableCoroutine { continuation ->
            ios.signInAnonymouslyWithCompletion { authResult, error ->
                if (error != null) {
                    continuation.resume(Result.failure(error.convertNSErrorToException()))
                } else {
                    val userData = authResult?.user()
                    currentUser = userData
                    continuation.resume(Result.success(userData?.toModel()))
                }
            }
        }
    }

    actual suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<KFirebaseUser?> {
        return suspendCancellableCoroutine { continuation ->
            ios.createUserWithEmail(email, password) { authResult, error ->
                if (error != null) {
                    continuation.resume(Result.failure(error.convertNSErrorToException()))
                } else {
                    val userData = authResult?.user()
                    currentUser = userData
                    continuation.resume(Result.success(userData?.toModel()))
                }
            }
        }
    }

    actual suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<KFirebaseUser?> {
        return suspendCancellableCoroutine { continuation ->
            ios.signInWithEmail(
                email = email,
                password = password,
                completion = { authResult: FIRAuthDataResult?, error: NSError? ->
                    if (error != null) {
                        continuation.resume(Result.failure(error.convertNSErrorToException()))
                    } else {
                        val userData = authResult?.user()
                        currentUser = userData
                        continuation.resume(Result.success(userData?.toModel()))
                    }
                })

        }
    }

    actual suspend fun addListenerAuthStateChange(): Result<KFirebaseUser?> {
        return suspendCancellableCoroutine { continuation ->
            ios.addAuthStateDidChangeListener { _, authUser ->
                if (authUser != null) {
                    continuation.resume(Result.success(authUser.toModel()))
                } else {
                    continuation.resume(Result.success(null))
                }
            }
        }
    }

    actual suspend fun addListenerIdTokenChanged(): Result<KFirebaseUser?> {
        return suspendCancellableCoroutine { continuation ->
            ios.addIDTokenDidChangeListener { _, authUser ->
                if (authUser != null) {
                    continuation.resume(Result.success(authUser.toModel()))
                } else {
                    continuation.resume(Result.success(null))
                }
            }
        }
    }

    actual suspend fun confirmPasswordReset(
        code: String,
        newPassword: String
    ): Result<Boolean?> {
        return suspendCancellableCoroutine { continuation ->
            ios.confirmPasswordResetWithCode(code, newPassword) { error ->
                if (error != null) {
                    continuation.resume(Result.failure(error.convertNSErrorToException()))
                } else {
                    continuation.resume(Result.success(true))
                }
            }
        }
    }

    actual fun setLanguageCodeLocale(locale: String) {
        ios.setLanguageCode(locale)
    }

    actual suspend fun kUpdateProfile(
        displayName: String?,
        photoUrl: String?
    ): Result<Boolean?> {
        return try {
            val user = currentUser ?: return Result.failure(Exception("No current user"))
            user.setDisplayName(displayName)
            if (photoUrl != null) {
                user.setPhotoURL(NSURL.URLWithString(photoUrl))
            }
            suspendCancellableCoroutine { continuation ->
                ios.updateCurrentUser(user) { error ->
                    if (error != null) {
                        continuation.resume(Result.failure(error.convertNSErrorToException()))
                    } else {
                        continuation.resume(Result.success(true))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun signInWithCredential(credential: AuthCredential): Result<KFirebaseUser?> {
        return suspendCancellableCoroutine { continuation ->
            ios.signInWithCredential(credential.ios) { authDataResult, error ->
                if (error != null) {
                    continuation.resume(Result.failure(error.convertNSErrorToException()))
                } else {
                    val user = authDataResult?.user()
                    continuation.resume(Result.success(user?.toModel()))
                }
            }
        }
    }

    actual fun isLinkEmail(email: String): Boolean {
        return ios.isSignInWithEmailLink(email)
    }

    actual var languageCode: String?
        get() = ios.languageCode()
        set(value) {
            setLanguageCodeLocale(value!!)
        }

    actual suspend fun applyActionWithCode(code: String): Result<Boolean?> {
        return suspendCancellableCoroutine { continuation ->
            ios.applyActionCode(code) { error ->
                if (error != null) {
                    continuation.resume(Result.failure(error.convertNSErrorToException()))
                } else {
                    continuation.resume(Result.success(true))
                }
            }
        }
    }

    actual suspend fun <T : ActionCodeResult> checkActionWithCode(code: String): Result<T> {
        return suspendCancellableCoroutine { continuation ->
            ios.checkActionCode(code) { result, error ->
                if (error != null) {
                    continuation.resume(Result.failure(error.convertNSErrorToException()))
                } else if (result != null) {
                    val resOptions = when (result.operation()) {
                        FIRActionCodeOperationEmailLink -> ActionCodeResult.SignInWithEmailLink
                        FIRActionCodeOperationVerifyEmail -> ActionCodeResult.VerifyEmail(result.email())
                        FIRActionCodeOperationRecoverEmail -> ActionCodeResult.RecoverEmail(
                            result.email(),
                            result.previousEmail()!!
                        )
                        FIRActionCodeOperationRevertSecondFactorAddition -> ActionCodeResult.RevertSecondFactorAddition(
                            result.email(),
                            null
                        )

                        FIRActionCodeOperationVerifyAndChangeEmail -> ActionCodeResult.VerifyBeforeChangeEmail(
                            result.email(),
                            result.previousEmail()!!
                        )
                        // FIRActionCodeOperationRevertSecondFactorAddition ->ActionCodeResult.RevertSecondFactorAddition(result.email(), null)
                        FIRActionCodeOperationUnknown -> throw UnsupportedOperationException(
                            result.operation().toString()
                        )

                        else -> throw UnsupportedOperationException(result.operation().toString())
                    } as T
                    continuation.resume(Result.success(resOptions))
                } else {
                    continuation.resume(Result.failure(Exception("operation is null")))
                }
            }
        }
    }

}

internal suspend inline fun <T, reified R> T.awaitResult(function: T.(callback: (R?, NSError?) -> Unit) -> Unit): R {
    val job = CompletableDeferred<R?>()
    function { result, error ->
        if (error == null) {
            job.complete(result)
        } else {
            job.completeExceptionally(error.convertNSErrorToException())
        }
    }
    return job.await() as R
}

actual suspend fun KFirebaseUser.kUpdateEmail(email: String): Result<Boolean?> {
    return suspendCancellableCoroutine { continuation ->
        currentUser?.updateEmail(email) { error ->
            if (error != null) {
                continuation.resume(Result.failure(error.convertNSErrorToException()))
            } else {
                continuation.resume(Result.success(true))
            }
        }
    }

}

actual suspend fun KFirebaseUser.kSendEmailVerification(): Result<Boolean?> {
    return suspendCancellableCoroutine { continuation ->
        currentUser?.sendEmailVerificationWithCompletion { error ->
            if (error != null) {
                continuation.resume(Result.failure(error.convertNSErrorToException()))
            } else {
                continuation.resume(Result.success(true))
            }
        }
    }
}

actual suspend fun KFirebaseUser.kResetPassword(password: String): Result<Boolean?> {
    return suspendCancellableCoroutine { continuation ->
        currentUser?.updatePassword(password) { error ->
            if (error != null) {
                continuation.resume(Result.failure(error.convertNSErrorToException()))
            } else {
                continuation.resume(Result.success(true))
            }
        }
    }
}

actual suspend fun KFirebaseUser.kDelete(): Result<Boolean?> {
    return suspendCancellableCoroutine { continuation ->
        currentUser?.deleteWithCompletion { error ->
            if (error != null) {
                continuation.resume(Result.failure(error.convertNSErrorToException()))
            } else {
                continuation.resume(Result.success(true))
            }
        }
    }

}

actual suspend fun KFirebaseUser.kSignOut(): Result<Boolean?> {
    memScoped {
        val error = alloc<ObjCObjectVar<NSError?>>()

        // Attempt to sign out
        val signOutSuccessful = FIRAuth.auth().signOut(error.ptr)

        if (signOutSuccessful) {
            currentUser = null
            return Result.success(true) // Sign out was successful
        } else {
            // Handle sign out error
            val errorMessage = error.value?.localizedDescription ?: "Unknown error"
            return Result.failure(Exception(errorMessage)) // Pass the error to the callback
        }

    }
}

actual suspend fun KFirebaseUser.linkProvider(credential: AuthCredential): Result<KFirebaseUser?> {
    return suspendCancellableCoroutine { continuation ->
        currentUser?.linkWithCredential(credential.ios) { authDataResult, error ->
            if (error != null) {
                continuation.resume(Result.failure(error.convertNSErrorToException()))
            } else {
                val user = authDataResult?.user()
                continuation.resume(Result.success(user?.toModel()))
            }
        }
    }


}

actual class MultiFactorInfo(private val ios: cocoapods.FirebaseAuth.FIRMultiFactorInfo) {
    actual val displayName: String?
        get() = ios.displayName()
    actual val enrollmentTime: Double?
        get() = ios.enrollmentDate().timeIntervalSinceReferenceDate
    actual val factorId: String
        get() = ios.factorID()
    actual val uid: String
        get() = ios.UID()
}