package io.github.firebase_auth

import cocoapods.FirebaseAuth.FIRAuthCredential
import cocoapods.FirebaseAuth.FIRAuthUIDelegateProtocol
import cocoapods.FirebaseAuth.FIREmailAuthProvider
import cocoapods.FirebaseAuth.FIRFacebookAuthProvider
import cocoapods.FirebaseAuth.FIRGitHubAuthProvider
import cocoapods.FirebaseAuth.FIRGoogleAuthProvider
import cocoapods.FirebaseAuth.FIROAuthCredential
import cocoapods.FirebaseAuth.FIROAuthProvider
import cocoapods.FirebaseAuth.FIRPhoneAuthCredential
import cocoapods.FirebaseAuth.FIRPhoneAuthProvider
import cocoapods.FirebaseAuth.FIRTwitterAuthProvider
import kotlinx.cinterop.ExperimentalForeignApi

actual open class AuthCredential @OptIn(ExperimentalForeignApi::class) constructor(open val ios: FIRAuthCredential) {
    @OptIn(ExperimentalForeignApi::class)
    actual val providerId: String
        get() = ios.provider()
}

@OptIn(ExperimentalForeignApi::class)
actual class PhoneAuthCredential @OptIn(ExperimentalForeignApi::class) constructor(override val ios: FIRPhoneAuthCredential) :
    AuthCredential(ios)

@ExperimentalForeignApi
actual class OAuthCredential(override val ios: FIROAuthCredential) : AuthCredential(ios)

actual object EmailAuthProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual fun credential(
        email: String,
        password: String,
    ): AuthCredential =
        AuthCredential(FIREmailAuthProvider.credentialWithEmail(email = email, password = password))

    @OptIn(ExperimentalForeignApi::class)
    actual fun credentialWithLink(
        email: String,
        emailLink: String,
    ): AuthCredential =
        AuthCredential(FIREmailAuthProvider.credentialWithEmail(email = email, link = emailLink))
}

actual object FacebookAuthProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual fun credential(accessToken: String): AuthCredential = AuthCredential(
        FIRFacebookAuthProvider.credentialWithAccessToken(accessToken)
    )
}

actual object GithubAuthProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual fun credential(token: String): AuthCredential = AuthCredential(
        FIRGitHubAuthProvider.credentialWithToken(token)
    )
}

actual object GoogleAuthProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual fun credential(idToken: String?, accessToken: String?): AuthCredential {
        requireNotNull(idToken) { "idToken must not be null" }
        requireNotNull(accessToken) { "accessToken must not be null" }
        return AuthCredential(FIRGoogleAuthProvider.credentialWithIDToken(idToken, accessToken))
    }
}

@OptIn(ExperimentalForeignApi::class)
val OAuthProvider.ios: FIROAuthProvider get() = ios

actual class OAuthProvider @OptIn(ExperimentalForeignApi::class) constructor(internal val ios: FIROAuthProvider) {

    @OptIn(ExperimentalForeignApi::class)
    actual constructor(
        provider: String,
        scopes: List<String>,
        customParameters: Map<String, String>,
        auth: KFirebaseAuth,
    ) : this(FIROAuthProvider.providerWithProviderID(provider, auth.ios)) {
        ios.setScopes(scopes)
        @Suppress("UNCHECKED_CAST")
        ios.setCustomParameters(customParameters as Map<Any?, *>)
    }

    actual companion object {
        @OptIn(ExperimentalForeignApi::class)
        actual fun credential(
            providerId: String,
            accessToken: String?,
            idToken: String?,
            rawNonce: String?
        ): OAuthCredential {
            val credential = when {
                idToken == null -> FIROAuthProvider.credentialWithProviderID(
                    providerID = providerId,
                    accessToken = accessToken!!
                )

                accessToken == null -> FIROAuthProvider.credentialWithProviderID(
                    providerID = providerId,
                    IDToken = idToken,
                    rawNonce = rawNonce!!
                )

                rawNonce == null -> FIROAuthProvider.credentialWithProviderID(
                    providerID = providerId,
                    IDToken = idToken,
                    accessToken = accessToken
                )

                else -> FIROAuthProvider.credentialWithProviderID(
                    providerID = providerId,
                    IDToken = idToken,
                    rawNonce = rawNonce,
                    accessToken = accessToken
                )
            }
            return OAuthCredential(credential)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
val PhoneAuthProvider.ios: FIRPhoneAuthProvider get() = ios

actual class PhoneAuthProvider @OptIn(ExperimentalForeignApi::class) constructor(internal val ios: FIRPhoneAuthProvider) {

    @OptIn(ExperimentalForeignApi::class)
    actual constructor(auth: KFirebaseAuth) : this(FIRPhoneAuthProvider.providerWithAuth(auth.ios))

    @OptIn(ExperimentalForeignApi::class)
    actual fun credential(verificationId: String, smsCode: String): PhoneAuthCredential =
        PhoneAuthCredential(ios.credentialWithVerificationID(verificationId, smsCode))

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun verifyPhoneNumber(
        phoneNumber: String,
        verificationProvider: PhoneVerificationProvider
    ): AuthCredential {
        val verificationId: String = ios.awaitResult {
            ios.verifyPhoneNumber(
                phoneNumber,
                verificationProvider.delegate,
                it
            )
        }
        val verificationCode = verificationProvider.getVerificationCode()
        return credential(verificationId, verificationCode)
    }
}

actual interface PhoneVerificationProvider {
    @OptIn(ExperimentalForeignApi::class)
    val delegate: FIRAuthUIDelegateProtocol?
    suspend fun getVerificationCode(): String
}

actual object TwitterAuthProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual fun credential(token: String, secret: String): AuthCredential = AuthCredential(
        FIRTwitterAuthProvider.credentialWithToken(token, secret)
    )
}
