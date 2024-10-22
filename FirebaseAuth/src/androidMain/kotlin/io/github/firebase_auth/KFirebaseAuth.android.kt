package io.github.firebase_auth

import android.net.Uri
import com.google.firebase.auth.ActionCodeEmailInfo
import com.google.firebase.auth.ActionCodeMultiFactorInfo
import com.google.firebase.auth.ActionCodeResult.ERROR
import com.google.firebase.auth.ActionCodeResult.PASSWORD_RESET
import com.google.firebase.auth.ActionCodeResult.RECOVER_EMAIL
import com.google.firebase.auth.ActionCodeResult.REVERT_SECOND_FACTOR_ADDITION
import com.google.firebase.auth.ActionCodeResult.SIGN_IN_WITH_EMAIL_LINK
import com.google.firebase.auth.ActionCodeResult.VERIFY_BEFORE_CHANGE_EMAIL
import com.google.firebase.auth.ActionCodeResult.VERIFY_EMAIL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import io.github.firebase_auth.KFirebaseAuth.Companion.currentUser
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class KFirebaseAuth {
    internal var android = FirebaseAuth.getInstance()

    companion object {
        internal var currentUser: FirebaseUser? = null
    }

    init {
        currentUser = android.currentUser
    }

    actual suspend fun currentUser(): Result<KFirebaseUser?> = suspendCancellableCoroutine { cont ->
        if (currentUser == null) {
            currentUser = android.currentUser
        }
        cont.resume(Result.success(currentUser?.toModel()))
    }

    actual suspend fun signInAnonymously(): Result<KFirebaseUser?> =
        suspendCancellableCoroutine { cont ->
            android.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        currentUser = task.result.user
                        val userData = task.result?.user?.toModel()
                        cont.resume(Result.success(userData))
                    } else {
                        val exception = task.exception ?: Exception("Unknown error occurred.")
                        cont.resumeWithException(exception)
                    }
                }
        }

    actual suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<KFirebaseUser?> = suspendCancellableCoroutine { cont ->
        android.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser = task.result.user
                    val userData = task.result?.user?.toModel()
                    cont.resume(Result.success(userData))
                } else {
                    val exception = task.exception ?: Exception("Unknown error occurred.")
                    cont.resumeWithException(exception)
                }
            }
    }

    actual suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<KFirebaseUser?> = suspendCancellableCoroutine { cont ->
        android.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser = task.result.user
                    val userData = task.result?.user?.toModel()
                    cont.resume(Result.success(userData))
                } else {
                    val exception = task.exception ?: Exception("Unknown error occurred.")
                    cont.resumeWithException(exception)
                }
            }
    }

    actual fun setLanguageCodeLocale(locale: String) {
        android.setLanguageCode(locale)
    }

    actual suspend fun kUpdateProfile(displayName: String?, photoUrl: String?): Result<Boolean?> =
        suspendCancellableCoroutine { cont ->
            if (currentUser != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName ?: currentUser!!.displayName)
                    .apply {
                        photoUrl?.let { photoUri = Uri.parse(it) }
                    }
                    .build()

                currentUser!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            cont.resume(Result.success(true))
                        } else {
                            val exception = task.exception ?: Exception("Unknown error occurred.")
                            cont.resumeWithException(exception)
                        }
                    }
            } else {
                cont.resumeWithException(Exception("No user is currently signed in."))
            }
        }

    actual suspend fun signInWithCredential(credential: AuthCredential): Result<KFirebaseUser?> =
        suspendCancellableCoroutine { cont ->
            android.signInWithCredential(credential.android)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        currentUser = task.result.user
                        val userData = task.result?.user?.toModel()
                        cont.resume(Result.success(userData))
                    } else {
                        val exception = task.exception ?: Exception("Unknown error occurred.")
                        cont.resumeWithException(exception)
                    }
                }
        }

    actual fun isLinkEmail(email: String): Boolean {
        return android.isSignInWithEmailLink(email)
    }

    actual suspend fun confirmPasswordReset(code: String, newPassword: String): Result<Boolean?> =
        suspendCancellableCoroutine { cont ->
            android.confirmPasswordReset(code, newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(Result.success(task.isSuccessful))
                    } else {
                        val exception = task.exception ?: Exception("Unknown error occurred.")
                        cont.resumeWithException(exception)
                    }
                }
        }

    actual suspend fun addListenerAuthStateChange(): Result<KFirebaseUser?> =
        suspendCancellableCoroutine { cont ->
            android.addAuthStateListener { firebaseAuth ->
                val currentUser = firebaseAuth.currentUser
                cont.resume(Result.success(currentUser?.toModel()))
            }
        }

    actual suspend fun addListenerIdTokenChanged(): Result<KFirebaseUser?> =
        suspendCancellableCoroutine { cont ->
            android.addAuthStateListener { firebaseAuth ->
                val currentUser = firebaseAuth.currentUser
                cont.resume(Result.success(currentUser?.toModel()))
            }
        }

    actual var languageCode: String?
        get() = android.languageCode
        set(value) {
            setLanguageCodeLocale(value!!)
        }

    actual suspend fun applyActionWithCode(code: String): Result<Boolean?> =
        suspendCancellableCoroutine { cont ->
            android.applyActionCode(code)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(Result.success(true))
                    } else {
                        val exception = task.exception ?: Exception("Unknown error occurred.")
                        cont.resumeWithException(exception)
                    }
                }
        }

    actual suspend fun <T : ActionCodeResult> checkActionWithCode(code: String): Result<T> {
        return suspendCancellableCoroutine { cont ->
            android.checkActionCode(code)
                .addOnCompleteListener { task ->
                    if (task.exception != null) {
                        cont.resumeWithException(Exception(task.exception))
                        return@addOnCompleteListener
                    }
                    val result = task.result
                    val operation = task.result.operation

                    val resOperation = when (operation) {
                        SIGN_IN_WITH_EMAIL_LINK -> ActionCodeResult.SignInWithEmailLink
                        VERIFY_EMAIL -> ActionCodeResult.VerifyEmail(result.info!!.email)
                        PASSWORD_RESET -> ActionCodeResult.PasswordReset(
                            result.info!!.email
                        )

                        RECOVER_EMAIL -> (result.info as ActionCodeEmailInfo).run {
                            ActionCodeResult.RecoverEmail(
                                email,
                                previousEmail
                            )
                        }

                        VERIFY_BEFORE_CHANGE_EMAIL -> (result.info as ActionCodeEmailInfo).run {
                            ActionCodeResult.VerifyBeforeChangeEmail(
                                email,
                                previousEmail
                            )
                        }

                        REVERT_SECOND_FACTOR_ADDITION -> (result.info as ActionCodeMultiFactorInfo).run {
                            ActionCodeResult.RevertSecondFactorAddition(
                                email,
                                MultiFactorInfo(multiFactorInfo)
                            )
                        }

                        ERROR -> throw UnsupportedOperationException(result.operation.toString())
                        else -> throw UnsupportedOperationException(result.operation.toString())
                    } as T

                    cont.resume(Result.success(resOperation))
                }
        }
    }
}

actual suspend fun KFirebaseUser.kUpdateEmail(email: String): Result<Boolean?> =
    suspendCancellableCoroutine { cont ->
    currentUser?.updateEmail(email)?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            cont.resume(Result.success(true))
        } else {
            val exception = task.exception ?: Exception("Unknown error occurred.")
            cont.resumeWithException(exception)
        }
    }
}

actual suspend fun KFirebaseUser.kSendEmailVerification(): Result<Boolean?> =
    suspendCancellableCoroutine { cont ->
    currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            cont.resume(Result.success(true))
        } else {
            val exception = task.exception ?: Exception("Unknown error occurred.")
            cont.resumeWithException(exception)
        }
    }
}

actual suspend fun KFirebaseUser.kResetPassword(password: String): Result<Boolean?> =
    suspendCancellableCoroutine { cont ->
    currentUser?.updatePassword(password)?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            cont.resume(Result.success(true))
        } else {
            val exception = task.exception ?: Exception("Unknown error occurred.")
            cont.resumeWithException(exception)
        }
    }
}

actual suspend fun KFirebaseUser.kDelete(): Result<Boolean?> = suspendCancellableCoroutine { cont ->
    currentUser?.delete()?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            currentUser = null
            cont.resume(Result.success(true))
        } else {
            val exception = task.exception ?: Exception("Unknown error occurred.")
            cont.resumeWithException(exception)
        }
    }
}

actual suspend fun KFirebaseUser.kSignOut(): Result<Boolean?> =
    suspendCancellableCoroutine { cont ->
        KFirebaseAuth().android.signOut()
    currentUser = null
        cont.resume(Result.success(true))
}


actual class MultiFactorInfo(private val android: com.google.firebase.auth.MultiFactorInfo) {
    actual val displayName: String?
        get() = android.displayName
    actual val enrollmentTime: Double?
        get() = android.enrollmentTimestamp.toDouble()
    actual val factorId: String
        get() = android.factorId
    actual val uid: String
        get() = android.uid
}