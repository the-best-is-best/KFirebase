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

}

expect fun KFirebaseUser.kUpdateEmail(email: String, callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kSendEmailVerification(callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kResetPassword(password: String, callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kDelete(callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kSignOut(callback: (Result<Boolean?>) -> Unit)