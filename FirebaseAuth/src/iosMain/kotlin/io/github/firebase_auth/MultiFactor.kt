//package io.github.firebase_auth
//
//import cocoapods.FirebaseAuth.*
//import kotlinx.cinterop.ExperimentalForeignApi
//import platform.Foundation.NSError
//
//import cocoapods.FirebaseAuth.*
//import kotlinx.coroutines.CompletableDeferred
//
//val MultiFactor.ios: FIRMultiFactor get() = ios
//
//actual class MultiFactor(internal val ios: FIRMultiFactor) {
//    actual val enrolledFactors: List<MultiFactorInfo>
//        get() = ios.enrolledFactors().mapNotNull { info -> (info as? FIRMultiFactorInfo)?.let { MultiFactorInfo(it) } }
//    actual suspend fun enroll(multiFactorAssertion: MultiFactorAssertion, displayName: String?): Unit = ios.await { enrollWithAssertion(multiFactorAssertion.ios, displayName, it) }
//    actual suspend fun getSession(): MultiFactorSession = MultiFactorSession(ios.awaitResult { getSessionWithCompletion(completion = it) })
//    actual suspend fun unenroll(multiFactorInfo: MultiFactorInfo): Unit = ios.await { unenrollWithInfo(multiFactorInfo.ios, it) }
//    actual suspend fun unenroll(factorUid: String): Unit = ios.await { unenrollWithFactorUID(factorUid, it) }
//}
//
//val MultiFactorInfo.ios: FIRMultiFactorInfo get() = ios
//
//actual class MultiFactorInfo(internal val ios: FIRMultiFactorInfo) {
//    actual val displayName: String?
//        get() = ios.displayName()
//    actual val enrollmentTime: Double
//        get() = ios.enrollmentDate().timeIntervalSinceReferenceDate
//    actual val factorId: String
//        get() = ios.factorID()
//    actual val uid: String
//        get() = ios.UID()
//}
//
//val MultiFactorAssertion.ios: FIRMultiFactorAssertion get() = ios
//
//actual class MultiFactorAssertion(internal val ios: FIRMultiFactorAssertion) {
//    actual val factorId: String
//        get() = ios.factorID()
//}
//
//val MultiFactorSession.ios: FIRMultiFactorSession get() = ios
//
//actual class MultiFactorSession(internal val ios: FIRMultiFactorSession)
//
//
//
//internal suspend inline fun <T> T.await(function: T.(callback: (NSError?) -> Unit) -> Unit) {
//    val job = CompletableDeferred<Unit>()
//    function { error ->
//        if (error == null) {
//            job.complete(Unit)
//        } else {
//            job.completeExceptionally(error.convertNSErrorToException())
//        }
//    }
//    job.await()
//}
//
