//package io.github.firebase_auth
//
//import com.google.firebase.auth.AuthResult
//import com.google.firebase.auth.MultiFactorResolver
//import kotlinx.coroutines.tasks.await
//
//val MultiFactor.android: com.google.firebase.auth.MultiFactor get() = android
//
//actual class MultiFactor(internal val android: com.google.firebase.auth.MultiFactor) {
//    actual val enrolledFactors: List<MultiFactorInfo>
//        get() = android.enrolledFactors.map { MultiFactorInfo(it) }
//    actual suspend fun enroll(multiFactorAssertion: MultiFactorAssertion, displayName: String?) {
//        android.enroll(multiFactorAssertion.android, displayName).await()
//    }
//    actual suspend fun getSession(): MultiFactorSession = MultiFactorSession(android.session.await())
//    actual suspend fun unenroll(multiFactorInfo: MultiFactorInfo) {
//        android.unenroll(multiFactorInfo.android).await()
//    }
//    actual suspend fun unenroll(factorUid: String) {
//        android.unenroll(factorUid).await()
//    }
//}
//
//val MultiFactorInfo.android: com.google.firebase.auth.MultiFactorInfo get() = android
//
//actual class MultiFactorInfo(internal val android: com.google.firebase.auth.MultiFactorInfo) {
//    actual val displayName: String?
//        get() = android.displayName
//    actual val enrollmentTime: Double
//        get() = android.enrollmentTimestamp.toDouble()
//    actual val factorId: String
//        get() = android.factorId
//    actual val uid: String
//        get() = android.uid
//}
//
//val MultiFactorAssertion.android: com.google.firebase.auth.MultiFactorAssertion get() = android
//
//actual class MultiFactorAssertion(internal val android: com.google.firebase.auth.MultiFactorAssertion) {
//    actual val factorId: String
//        get() = android.factorId
//}
//
//val MultiFactorSession.android: com.google.firebase.auth.MultiFactorSession get() = android
//
//actual class MultiFactorSession(internal val android: com.google.firebase.auth.MultiFactorSession)
//
//
