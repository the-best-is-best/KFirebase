package io.github.firebase_auth

import cocoapods.FirebaseAuth.FIRActionCodeOperationEmailLink
import cocoapods.FirebaseAuth.FIRActionCodeOperationRecoverEmail
import cocoapods.FirebaseAuth.FIRActionCodeOperationUnknown
import cocoapods.FirebaseAuth.FIRActionCodeOperationVerifyAndChangeEmail
import cocoapods.FirebaseAuth.FIRActionCodeOperationVerifyEmail
import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRAuthDataResult
import cocoapods.FirebaseAuth.FIRUser
import io.github.firebase_auth.KFirebaseAuth.Companion.currentUser
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.CompletableDeferred
import platform.Foundation.NSError
import platform.Foundation.NSURL

actual class KFirebaseAuth {
    companion object {
        internal var currentUser: FIRUser? = null
    }

    internal var ios: FIRAuth = FIRAuth.auth()

    init {
        currentUser = FIRAuth.auth().currentUser()
        println("user data is $currentUser")
    }

    actual fun currentUser(callback: (Result<KFirebaseUser?>) -> Unit) {

        if (currentUser == null) {
            try {
                val fireUser = ios.currentUser()
                if (fireUser != null) {
                    currentUser = fireUser
                    callback(Result.success(fireUser.toModel()))

                } else {
                    callback(Result.success(null))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        } else {
            callback(Result.success(currentUser!!.toModel()))
        }
        println("user data is $currentUser")


    }

    actual fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        callback: (Result<KFirebaseUser?>) -> Unit
    ) {
        ios.createUserWithEmail(email, password) { authResult, error ->
            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
                return@createUserWithEmail
            }
            val userData = authResult?.user()
            if (userData != null) {
                currentUser = userData

               callback( Result.success(userData.toModel()))
            } else {
                callback(    Result.success(null))
            }

        }
    }

    actual fun signInAnonymously(callback: (Result<KFirebaseUser?>) -> Unit) {
        ios.signInAnonymouslyWithCompletion { authResult, nsError ->
            if (nsError != null) {
                println("auth signInAnonymously error ${nsError}")
                callback(Result.failure(nsError.convertNSErrorToException()))
                return@signInAnonymouslyWithCompletion
            }
            val userData = authResult?.user()
            if (userData != null) {
                currentUser = userData
                callback(Result.success(userData.toModel()))

            } else {

                callback(Result.success(null))
            }
        }
    }

    actual fun signInWithEmailAndPassword(
        email: String,
        password: String,
        callback: (Result<KFirebaseUser?>) -> Unit
    ) {

        ios.signInWithEmail(
            email = email,
            password = password,
            completion = { authResult: FIRAuthDataResult?, error: NSError? ->
                if (error != null) {
                    println("auth error $error")
                    callback(Result.failure(error.convertNSErrorToException()))
                    return@signInWithEmail
                }
                val userData = authResult?.user()
                if (userData != null) {
                    currentUser = userData
                    callback(Result.success(userData.toModel()))
                } else {
                    callback(Result.success(null))
                }
            })
    }

    actual fun setLanguageCodeLocale(locale: String) {
        ios.setLanguageCode(locale)
    }

    actual fun kUpdateProfile(
        displayName: String?,
        photoUrl: String?,
        callback: (Result<Boolean?>) -> Unit
    ) {
        try {
            // Create a profile change request
            currentUser = FIRAuth.auth().currentUser()
            currentUser!!.apply {
                setDisplayName(displayName ?: currentUser!!.displayName())
                if (photoUrl != null) {
                    setPhotoURL(NSURL.URLWithString(photoUrl))
                }
            }
            ios.updateCurrentUser(currentUser) { error ->
                if (error != null) {
                    callback(Result.failure(error.convertNSErrorToException()))
                    return@updateCurrentUser
                }
                callback(Result.success(true))
            }


        } catch (e: Exception) {
            callback(Result.failure(e))

        }

    }

    actual fun signInWithCredential(
        credential: AuthCredential,
        callback: (Result<KFirebaseUser?>) -> Unit
    ) {
        ios.signInWithCredential(credential.ios) { authDataResult, error ->
            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
            } else {
                // Handle successful sign-in
                val user = authDataResult?.user()
                callback(Result.success(user?.toModel()))
            }
        }
    }

    actual fun isLinkEmail(email: String):Boolean{
       return ios.isSignInWithEmailLink(email)
    }

    actual fun confirmPasswordReset(
        code: String,
        newPassword: String,
        callback: (Result<Boolean?>) -> Unit
    ) {
        ios.confirmPasswordResetWithCode(code, newPassword) { error ->
            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
                return@confirmPasswordResetWithCode
            }
            callback(Result.success(true))
        }
    }

    actual fun addListenerAuthStateChange(callback: (Result<KFirebaseUser?>) -> Unit) {
        ios.addAuthStateDidChangeListener(listener = { _: FIRAuth?, authUser: FIRUser? ->
            if (authUser != null) {
                callback(Result.success(authUser.toModel()))
                return@addAuthStateDidChangeListener
            }
            callback(Result.success(null))
        })
    }

    actual fun addListenerIdTokenChanged(callback: (Result<KFirebaseUser?>) -> Unit) {
        ios.addIDTokenDidChangeListener(listener = { _: FIRAuth?, authUser: FIRUser? ->
            if (authUser != null) {
                callback(Result.success(authUser.toModel()))
                return@addIDTokenDidChangeListener
            }
            callback(Result.success(null))
        })
    }

    actual var languageCode: String?
        get() = ios.languageCode()
        set(value) {
            setLanguageCodeLocale(value!!)
        }

    actual fun applyActionWithCode(
        code: String,
        callback: (Result<Boolean?>) -> Unit
    ) {
        ios.applyActionCode(code) { error ->
            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
                return@applyActionCode
            }
            callback(Result.success(true))

        }
    }

    actual fun <T : ActionCodeResult> checkActionWithCode(
        code: String,
        callback: (Result<T>) -> Unit
    ) {
        ios.checkActionCode(code, { result, error ->
            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
                return@checkActionCode
            }
            if (result == null) {
                callback(Result.failure(Exception("operation is null")))
                return@checkActionCode
            }
            val resOptions = when (result.operation()) {
                FIRActionCodeOperationEmailLink -> ActionCodeResult.SignInWithEmailLink
                FIRActionCodeOperationVerifyEmail -> ActionCodeResult.VerifyEmail(result.email())
                FIRActionCodeOperationRecoverEmail -> ActionCodeResult.RecoverEmail(
                    result.email(),
                    result.previousEmail()!!
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

            callback(Result.success(resOptions))

        })


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


actual fun KFirebaseUser.kUpdateEmail(email: String, callback: (Result<Boolean?>) -> Unit) {
    currentUser?.updateEmail(email) { error ->
        try {
            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
                return@updateEmail
            }
            callback(Result.success(true))

        } catch (e: Exception) {
            callback(Result.failure(e))

        }
    }
}


actual fun KFirebaseUser.kSendEmailVerification(callback: (Result<Boolean?>) -> Unit) {
    val sent = currentUser?.emailVerified()
    callback(Result.success(sent))
}


actual fun KFirebaseUser.kResetPassword(password: String, callback: (Result<Boolean?>) -> Unit) {
    currentUser?.updatePassword(password) { error ->
        try {
            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
                return@updatePassword
            }
            callback(Result.success(true))

        } catch (e: Exception) {
            callback(Result.failure(e))

        }
    }
}


actual fun KFirebaseUser.linkProvider(credential: AuthCredential,callback: (Result<KFirebaseUser?>) -> Unit
){
    currentUser?.linkWithCredential(credential.ios){authResult , error ->
        if(error != null){
            callback(Result.failure(error.convertNSErrorToException()))
            return@linkWithCredential
        }
        if(authResult != null) {
            currentUser = authResult.user()
            callback(Result.success(currentUser!!.toModel()))
        }else{
            callback(Result.success(null))
        }


    }
}


actual fun KFirebaseUser.kDelete(callback: (Result<Boolean?>) -> Unit) {

    currentUser?.deleteWithCompletion { error ->
        try {
            if (error != null) {

                callback(Result.failure(error.convertNSErrorToException()))
                return@deleteWithCompletion
            }
            currentUser = null
            callback(Result.success(true))

        } catch (e: Exception) {
            callback(Result.failure(e))

        }
    }

}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun KFirebaseUser.kSignOut(callback: (Result<Boolean?>) -> Unit) {
    memScoped {
        val error = alloc<ObjCObjectVar<NSError?>>()

        // Attempt to sign out
        val signOutSuccessful = FIRAuth.auth().signOut(error.ptr)

        if (signOutSuccessful) {
            currentUser = null
            callback(Result.success(true)) // Sign out was successful
        } else {
            // Handle sign out error
            val errorMessage = error.value?.localizedDescription ?: "Unknown error"
            callback(Result.failure(Exception(errorMessage))) // Pass the error to the callback
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