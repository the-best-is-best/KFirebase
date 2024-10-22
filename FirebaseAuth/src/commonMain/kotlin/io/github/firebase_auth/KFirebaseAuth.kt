package io.github.firebase_auth

expect class KFirebaseAuth() {
    suspend fun currentUser(): Result<KFirebaseUser?>
    suspend fun signInAnonymously(): Result<KFirebaseUser?>
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<KFirebaseUser?>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<KFirebaseUser?>

    suspend fun addListenerAuthStateChange(): Result<KFirebaseUser?>
    suspend fun addListenerIdTokenChanged(): Result<KFirebaseUser?>
    suspend fun confirmPasswordReset(
        code: String,
        newPassword: String
    ): Result<Boolean?>

    fun setLanguageCodeLocale(locale: String)
    suspend fun kUpdateProfile(
        displayName: String?,
        photoUrl: String?
    ): Result<Boolean?>

    suspend fun signInWithCredential(
        credential: AuthCredential
    ): Result<KFirebaseUser?>

    fun isLinkEmail(email: String): Boolean
    var languageCode: String?

    suspend fun applyActionWithCode(code: String): Result<Boolean?>
    suspend fun <T : ActionCodeResult> checkActionWithCode(code: String): Result<T>
}

expect suspend fun KFirebaseUser.kUpdateEmail(email: String): Result<Boolean?>
expect suspend fun KFirebaseUser.kSendEmailVerification(): Result<Boolean?>
expect suspend fun KFirebaseUser.kResetPassword(password: String): Result<Boolean?>
expect suspend fun KFirebaseUser.kDelete(): Result<Boolean?>
expect suspend fun KFirebaseUser.kSignOut(): Result<Boolean?>
expect suspend fun KFirebaseUser.linkProvider(credential: AuthCredential): Result<KFirebaseUser?>

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

    class RevertSecondFactorAddition internal constructor(
        val email: String,
        val multiFactorInfo: MultiFactorInfo?
    ) : ActionCodeResult()
}

expect class MultiFactorInfo {
    val displayName: String?
    val enrollmentTime: Double?
    val factorId: String
    val uid: String
}
