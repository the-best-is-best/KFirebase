//package io.github.firebase_auth
//
//expect class MultiFactor {
//    val enrolledFactors: List<MultiFactorInfo>
//    suspend fun enroll(multiFactorAssertion: MultiFactorAssertion, displayName: String?)
//    suspend fun getSession(): MultiFactorSession
//    suspend fun unenroll(multiFactorInfo: MultiFactorInfo)
//    suspend fun unenroll(factorUid: String)
//}
//
//expect class MultiFactorInfo {
//    val displayName: String?
//    val enrollmentTime: Double
//    val factorId: String
//    val uid: String
//}
//
//expect class MultiFactorAssertion {
//    val factorId: String
//}
//
//expect class MultiFactorSession
//
//
//public expect class MultiFactorResolver {
//    public val auth: KFirebaseAuth
//    public val hints: List<MultiFactorInfo>
//    public val session: MultiFactorSession
//
//    public suspend fun resolveSignIn(assertion: MultiFactorAssertion): AuthResult
//}
