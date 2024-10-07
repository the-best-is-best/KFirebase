package io.github.firebase_auth

expect open class AuthCredential {
    val providerId: String
}

expect class PhoneAuthCredential : AuthCredential

expect class OAuthCredential : AuthCredential

expect object EmailAuthProvider {
    fun credential(email: String, password: String): AuthCredential
    fun credentialWithLink(email: String, emailLink: String): AuthCredential
}

expect object FacebookAuthProvider {
    fun credential(accessToken: String): AuthCredential
}

expect object GithubAuthProvider {
    fun credential(token: String): AuthCredential
}

expect object GoogleAuthProvider {
    fun credential(idToken: String?, accessToken: String?): AuthCredential
}

expect class OAuthProvider(
    provider: String,
    scopes: List<String> = emptyList(),
    customParameters: Map<String, String> = emptyMap(),
    auth: KFirebaseAuth = KFirebaseAuth(),
) {
    companion object {
        fun credential(
            providerId: String,
            accessToken: String? = null,
            idToken: String? = null,
            rawNonce: String? = null
        ): OAuthCredential
    }
}

expect class PhoneAuthProvider(auth: KFirebaseAuth = KFirebaseAuth()) {
    fun credential(verificationId: String, smsCode: String): PhoneAuthCredential
    suspend fun verifyPhoneNumber(
        phoneNumber: String,
        verificationProvider: PhoneVerificationProvider
    ): AuthCredential
}

expect interface PhoneVerificationProvider

expect object TwitterAuthProvider {
    fun credential(token: String, secret: String): AuthCredential
}
