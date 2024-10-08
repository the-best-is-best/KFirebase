package io.github.firebase_auth

expect class KFirebaseAuth() {
    fun currentUser(callback: (Result<KFirebaseUser?>) -> Unit)
    fun signInAnonymously(callback: (Result<KFirebaseUser?>) -> Unit)
    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        callback: (Result<KFirebaseUser?>) -> Unit
    )

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        callback: (Result<KFirebaseUser?>) -> Unit
    )
    fun addListenerAuthStateChange(callback: (Result<KFirebaseUser?>) -> Unit)
    fun addListenerIdTokenChanged(callback: (Result<KFirebaseUser?>) -> Unit)
    fun confirmPasswordReset(
        code: String,
        newPassword: String,
        callback: (Result<Boolean?>) -> Unit
    )
    fun setLanguageCode(locale: String)
    fun kUpdateProfile(
        displayName: String?,
        photoUrl: String?,
        callback: (Result<Boolean?>) -> Unit
    )

    fun signInWithCredential(
        credential: AuthCredential,
        callback: (Result<KFirebaseUser?>) -> Unit
    )

    fun isLinkEmail(email: String): Boolean
    var languageCode: String?

    fun applyActionWithCode(code: String, callback: (Result<Boolean?>) -> Unit)
    fun <T : ActionCodeResult> checkActionWithCode(code: String, callback: (Result<T>) -> Unit)
}

expect fun KFirebaseUser.kUpdateEmail(email: String, callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kSendEmailVerification(callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kResetPassword(password: String, callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kDelete(callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kSignOut(callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.linkProvider(credential: AuthCredential , callback: (Result<KFirebaseUser?>) -> Unit)


sealed class ActionCodeResult {
    data object SignInWithEmailLink : ActionCodeResult()
    class PasswordReset internal constructor(val email: String) : ActionCodeResult()
    class VerifyEmail internal constructor(val email: String) : ActionCodeResult()
    class RecoverEmail internal constructor(val email: String, val previousEmail: String) :
        ActionCodeResult()

    class VerifyBeforeChangeEmail internal constructor(
        val email: String,
        val previousEmail: String
    ) : ActionCodeResult()
    //  class RevertSecondFactorAddition internal constructor(val email: String, val multiFactorInfo: MultiFactorInfo?) : ActionCodeResult()
}