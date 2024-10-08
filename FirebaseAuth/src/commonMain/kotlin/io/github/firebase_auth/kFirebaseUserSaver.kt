package io.github.firebase_auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class KFirebaseUserState {
    var user by mutableStateOf<KFirebaseUser?>(null)
        private set
    private val auth = KFirebaseAuth()

    fun getCurrentUser(callback: (Result<Boolean?>) -> Unit) {
        auth.currentUser { it ->
            it.onSuccess {
                user = it
                callback(Result.success(true))
            }
            it.onFailure {
                callback(Result.failure(it))
            }
        }
    }

    fun signInAnonymously(callback: (Result<Boolean?>) -> Unit) {
        auth.signInAnonymously { it ->
            it.onSuccess {
                print("user sign ${it!!.uid}")
                user = it
                callback(Result.success(true))
            }
            it.onFailure {
                println("error signInAnonymously $it")
                callback(Result.failure(it))
            }
        }
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        callback: (Result<Boolean?>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password) {
            it.onSuccess {
                user = it
                callback(Result.success(true))
            }
            it.onFailure {
                callback(Result.failure(it))
            }
        }
    }

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        callback: (Result<Boolean?>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password) {
            it.onSuccess {
                user = it
                callback(Result.success(true))
            }
            it.onFailure {
                callback(Result.failure(it))
            }
        }
    }

    fun setLanguageCode(locale: String) {
        auth.setLanguageCode(locale)
    }

    fun updateProfile(
        displayName: String?,
        photoUrl: String?,
        callback: (Result<Boolean?>) -> Unit
    ) {
        auth.kUpdateProfile(displayName, photoUrl) { it ->
            it.onSuccess {
                println("Profile updated successfully: $it") // Debugging line
                user = user?.copy(
                    displayName = displayName,
                    photoURL = photoUrl
                )
                callback(Result.success(true))
            }
            it.onFailure { exception ->
                println("Failed to update profile: ${exception.message}") // Debugging line
                callback(Result.failure(exception))
            }
        }
    }


    fun signInWithCredential(credential: AuthCredential, callback: (Result<Boolean?>) -> Unit) {
        auth.signInWithCredential(credential) { it ->
            it.onSuccess {
                user = it
                callback(Result.success(true))
            }
            it.onFailure {
                callback(Result.failure(it))
            }
        }
    }

    fun updateEmail(email: String, callback: (Result<Boolean?>) -> Unit) {
        user?.kUpdateEmail(email) { it ->
            it.onSuccess {
                user = user?.copy(email = email)
                callback(Result.success(true))
            }
            it.onFailure {
                callback(Result.failure(it))
            }
        }
    }

    fun sendEmailVerification(callback: (Result<Boolean?>) -> Unit) {
        user?.kSendEmailVerification(callback)
    }

    fun resetPassword(password: String, callback: (Result<Boolean?>) -> Unit) {
        user?.kResetPassword(password, callback)
    }

    fun delete(callback: (Result<Boolean?>) -> Unit) {
        user?.kDelete {
            it.onSuccess {
                user = null
                callback(Result.success(true))
            }
            it.onFailure {
                callback(Result.failure(it))
            }
        }
    }

    fun signOut(callback: (Result<Boolean?>) -> Unit) {
        user!!.kSignOut {
            it.onSuccess {
                user = null
                callback(Result.success(true))
            }
            it.onFailure {
                callback(Result.failure(it))
            }
        }
    }

    fun linkEmail(credential: AuthCredential ,callback: (Result<Boolean?>) -> Unit ){
        user!!.linkProvider(credential){
            it.onSuccess {
                user = it
                callback(Result.success(true))
            }
            it.onFailure {
                callback(Result.failure(it))
            }
        }
    }
    fun isEmailLinked(email: String ): Boolean{
       return auth.isLinkEmail(email)
    }

    fun confirmPasswordReset(
        code: String,
        newPassword: String,
        callback: (Result<Boolean?>) -> Unit
    ) {
        auth.confirmPasswordReset(code, newPassword, callback)
    }

    fun addListenerAuthStateChange(callback: (Result<Boolean?>) -> Unit) {
        auth.addListenerAuthStateChange {
            it.onSuccess {
                if (it != null) {
                    user = it
                    callback(Result.success(true))
                } else {
                    callback(Result.success(false))
                }
            }

        }
    }

    fun addListenerIdTokenChanged(callback: (Result<Boolean?>) -> Unit) {
        auth.addListenerIdTokenChanged {
            it.onSuccess {
                if (it != null) {
                    user = it
                    callback(Result.success(true))
                } else {
                    callback(Result.success(false))
                }
            }

        }
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
