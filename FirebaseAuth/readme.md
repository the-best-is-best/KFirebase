<h1 align="center">KFirebaseAuth</h1><br>
<div align="center">
<a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
<a href="https://android-arsenal.com/api?level=21" rel="nofollow">
    <img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" style="max-width: 100%;">
</a>
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android" alt="Badge Android" />
  <img src="https://img.shields.io/badge/iOS-13%2B-blue.svg?logo=apple" alt="iOS 13+ Badge" />

<a href="https://github.com/the-best-is-best/"><img alt="Profile" src="https://img.shields.io/badge/github-%23181717.svg?&style=for-the-badge&logo=github&logoColor=white" height="20"/></a>
</div>

<br>

### KFirebaseAuth is a Kotlin Multiplatform library designed to streamline the integration of Firebase services in your mobile applications. With this library, developers can effortlessly initialize Firebase for both Android and iOS, enabling a unified and efficient development experience

<hr>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/kfirebase-auth)](https://central.sonatype.com/artifact/io.github.the-best-is-best/kfirebase-auth)

KFirebaseAuth is available on `mavenCentral()`.

## Installation

```kotlin
implementation("io.github.the-best-is-best:kfirebase-auth:1.0.0")
```

### androidMain

```kotlin
KAndroidFirebaseCore.initialize(this)
```

## Need add this in pod file if not exist run ` pod init `

```pod
 pod 'FirebaseAuth' , '11.3.0'
```

### iosApp

```ios
 FirebaseApp.configure()
```

### How use it

### Note we have two ways for use this package

- way one

<hr>

```kotlin
val currentUserState = rememberKFirebaseUserStates() // will update state auto
currentUserState.user?.uid // or any thing 

```

#### User data can be access

```kotlin

    val uid: String?,
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoURL: String?,
    val isAnonymous: Boolean?,
    val isEmailVerified: Boolean?,
    val metaData: KFirebaseUserMetaData?,
```

#### Methods in currentState

```kotlin

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

    fun setLanguageCodeLocale(locale: String) {
        auth.setLanguageCodeLocale(locale)
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

    var languageCode: String? = auth.languageCode

    fun applyActionCode(code: String, callback: (Result<Boolean?>) -> Unit) {
        auth.applyActionWithCode(code, callback)
    }

    fun <T : ActionCodeResult> checkActionWithCode(code: String, callback: (Result<T>) -> Unit) {
        auth.checkActionWithCode<T>(code, callback)
    }

```

- way two

#### In viewModel

```kotlin
 var user by mutableStateOf<KFirebaseUser?>(null)
        private set
    private val auth = KFirebaseAuth()
```

#### KFirebaseAuth mehods access

```kotlin
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
        callback: (Result<Boolean?>) -> Unit,
    )
    fun setLanguageCodeLocale(locale: String)
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
```

#### current user methods access

```kotlin
expect fun KFirebaseUser.kUpdateEmail(email: String, callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kSendEmailVerification(callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kResetPassword(password: String, callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kDelete(callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.kSignOut(callback: (Result<Boolean?>) -> Unit)
expect fun KFirebaseUser.linkProvider(credential: AuthCredential , callback: (Result<KFirebaseUser?>) -> Unit)
```
