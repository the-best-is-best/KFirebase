package io.github.firebase_auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class KFirebaseUserState {
    var user by mutableStateOf<KFirebaseUser?>(null)
        private set
    private val auth = KFirebaseAuth()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getCurrentUser()
        }
}

    suspend fun getCurrentUser(): Result<Boolean?> {
        val res = auth.currentUser()

        if (res.isSuccess) {
            res.onSuccess {
                user = it

            }
            return Result.success(true)

        } else {
            res.onFailure {
                return Result.failure(it)
            }

            return Result.failure(res.exceptionOrNull() ?: Exception())
        }

    }


    suspend fun signInAnonymously(): Result<Boolean?> {
        val res = auth.signInAnonymously()

        if (res.isSuccess) {
            res.onSuccess {
                user = it

            }
            return Result.success(true)

        } else {
            res.onFailure {
                return Result.failure(it)
            }

            return Result.failure(res.exceptionOrNull() ?: Exception())
        }

    }

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Boolean?> {
        val res = auth.createUserWithEmailAndPassword(email, password)

        if (res.isSuccess) {
            res.onSuccess {
                user = it

            }
            return Result.success(true)

        } else {
            res.onFailure {
                return Result.failure(it)
            }

            return Result.failure(res.exceptionOrNull() ?: Exception())
        }

       
    }

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Boolean?> {
        val res = auth.signInWithEmailAndPassword(email, password)

        res.onSuccess {
            user = it
            return Result.success(true)

        }

        res.onFailure {
            return Result.failure(res.exceptionOrNull() ?: Exception())

        }
        return Result.success(false)

    }

    fun setLanguageCodeLocale(locale: String) {
        auth.setLanguageCodeLocale(locale)
    }

    suspend fun updateProfile(
        displayName: String?,
        photoUrl: String?,
    ): Result<Boolean?> {
        val res = auth.kUpdateProfile(displayName, photoUrl)

        if (res.isSuccess) {
            res.onSuccess {
                user = user!!.copy(displayName = displayName, photoURL = photoUrl)

            }
            return Result.success(true)

        } else {
            res.onFailure {
                return Result.failure(it)
            }

            return Result.failure(res.exceptionOrNull() ?: Exception())
        }

    

    }


    suspend fun signInWithCredential(credential: AuthCredential): Result<Boolean?> {
        val res = auth.signInWithCredential(credential)

        return if (res.isSuccess) {
            Result.success(true)

        } else {
            Result.failure(res.exceptionOrNull() ?: Exception())
        }

    }

    suspend fun updateEmail(email: String): Result<Boolean?> {
        val res = user!!.kUpdateEmail(email)

        return if (res.isSuccess) {
            user = user!!.copy(email)
            Result.success(true)

        } else {
            Result.failure(res.exceptionOrNull() ?: Exception())
        }

    }

    suspend fun sendEmailVerification(): Result<Boolean?> {
        val res = user?.kSendEmailVerification()

        return if (res?.isSuccess == true) {
            Result.success(true)

        } else {
            Result.failure(res?.exceptionOrNull() ?: Exception())
        }

    }

    suspend fun resetPassword(password: String): Result<Boolean?> {
        val res = user?.kResetPassword(password)

        return if (res?.isSuccess == true) {
            Result.success(true)

        } else {
            res?.onFailure {
                return Result.failure(it)
            }

            Result.failure(res?.exceptionOrNull() ?: Exception())
        }

    }

    suspend fun delete(): Result<Boolean?> {
        val res = user?.kDelete()

        return if (res?.isSuccess == true) {
            user = null
            Result.success(true)

        } else {
            res?.onFailure {
                return Result.failure(it)
            }

            Result.failure(res?.exceptionOrNull() ?: Exception())
        }

    }

    suspend fun signOut(): Result<Boolean?> {
        val res = user!!.kSignOut()

        return if (res.isSuccess) {
            user = null
            Result.success(true)


        } else {
            Result.failure(res.exceptionOrNull() ?: Exception())
        }


    }

    suspend fun linkEmail(credential: AuthCredential): Result<Boolean?> {
        val res = user!!.linkProvider(credential)

        if (res.isSuccess) {
            res.onSuccess {
                user = it
                return Result.success(true)
            }
            res.onFailure {
                return Result.failure(it)
            }
        }
        return Result.failure(res.exceptionOrNull() ?: Exception())

    }


    fun isEmailLinked(email: String ): Boolean{
       return auth.isLinkEmail(email)
    }

    suspend fun confirmPasswordReset(
        code: String,
        newPassword: String,
    ): Result<Boolean?> {

        return auth.confirmPasswordReset(code, newPassword)
    }

    suspend fun addListenerAuthStateChange(): Result<Boolean?> {
        val res = auth.addListenerAuthStateChange()

        if (res.isSuccess) {
            res.onSuccess {
                user = it
                return Result.success(true)
            }
            res.onFailure {
                return Result.failure(it)
            }


        }
        return Result.failure(res.exceptionOrNull() ?: Exception())

    }

    suspend fun addListenerIdTokenChanged(): Result<Boolean?> {
        val res = auth.addListenerIdTokenChanged()

        return if (res.isSuccess) {
            Result.success(true)

        } else {
            res.onFailure {
                return Result.failure(it)
            }

            Result.failure(res.exceptionOrNull() ?: Exception())
        }

    }

    var languageCode: String? = auth.languageCode

    suspend fun applyActionCode(code: String): Result<Boolean?> {
        return auth.applyActionWithCode(code)
    }

    suspend fun <T : ActionCodeResult> checkActionWithCode(code: String): Result<T> {
        return auth.checkActionWithCode<T>(code)
    }


    companion object {
        val Saver: Saver<KFirebaseUserState, *> = listSaver(
            save = { data ->
                val user = data.user
                if (user == null) {
                    listOf(null)
                } else {
                    listOf(
                        user.uid,
                        user.displayName,
                        user.email,
                        user.phoneNumber,
                        user.photoURL,
                        user.isAnonymous,
                        user.isEmailVerified,
                        user.metaData?.creationTime,
                        user.metaData?.lastSignInTime
                    )
                }
            },
            restore = {
                if (it[0] == null) {
                    KFirebaseUserState()
                } else {
                    val userState = KFirebaseUserState()
                    userState.user = KFirebaseUser(
                        uid = it[0] as String,
                        displayName = it[1] as String?,
                        email = it[2] as String?,
                        phoneNumber = it[3] as String?,
                        photoURL = it[4] as String?,
                        isAnonymous = it[5] as Boolean?,
                        isEmailVerified = it[6] as Boolean?,
                        metaData = KFirebaseUserMetaData(
                            creationTime = it[7] as? Double,
                            lastSignInTime = it[8] as? Double
                        )
                    )
                    userState
                }
            }
        )
    }
}

@Composable
fun rememberKFirebaseUserStates(): KFirebaseUserState {
    return rememberSaveable(saver = KFirebaseUserState.Saver) {
        KFirebaseUserState()
    }
}
