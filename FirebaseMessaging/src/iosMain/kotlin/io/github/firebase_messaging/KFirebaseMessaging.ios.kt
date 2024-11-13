// iosMain/src/io/github/firebase_messaging/KFirebaseMessagingImpl.kt

package io.github.firebase_messaging

import cocoapods.FirebaseMessaging.FIRMessaging
import cocoapods.FirebaseMessaging.FIRMessagingDelegateProtocol
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import kotlin.coroutines.resume


actual object KFirebaseMessaging {
    private var tokenListener: ((String?) -> Unit)? = null


    actual fun setTokenListener(callback: (String?) -> Unit) {
        tokenListener = callback
        }

    fun init(messagingDelegate: FIRMessagingDelegateProtocol){
        FIRMessaging.messaging().delegate = messagingDelegate
        FIRMessaging.messaging().autoInitEnabled = true
        UIApplication.sharedApplication.registerForRemoteNotifications()

    }

    fun notifyTokenListener(token: String?) {
        tokenListener?.invoke(token)
    }




    actual suspend fun getToken(): Result<String?> {
        return suspendCancellableCoroutine { cont ->
            try {
                cont.resume(Result.success(FIRMessaging.messaging().FCMToken))

            } catch (e: Exception) {
                cont.resume(Result.failure(e))
            }
        }
    }


    actual suspend fun subscribeTopic(name: String): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            try {
                FIRMessaging.messaging().subscribeToTopic(name)

                cont.resume(Result.success(true))
            } catch (e: Exception) {
                cont.resume(Result.failure(Exception(e)))
            }
        }
    }

    actual suspend fun unsubscribeTopic(name: String): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            try {
                FIRMessaging.messaging().unsubscribeFromTopic(name)
                cont.resume(Result.success(true))
            } catch (e: Exception) {
                cont.resume(Result.failure(Exception(e)))
            }
        }
    }

    actual fun deleteToken() {
        FIRMessaging.messaging().deleteTokenWithCompletion { }
    }
}

