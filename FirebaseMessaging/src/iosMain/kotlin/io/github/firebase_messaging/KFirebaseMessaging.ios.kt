package io.github.firebase_messaging

import cocoapods.FirebaseMessaging.FIRMessaging
import cocoapods.FirebaseMessaging.FIRMessagingDelegateProtocol
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import platform.UIKit.UIApplicationDelegateProtocol
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject
import cocoapods.KFirebaseMessaging.KFirebaseMessaging as FCM


@OptIn(ExperimentalForeignApi::class)
actual class KFirebaseMessaging actual constructor() : NSObject(), UIApplicationDelegateProtocol,
    FIRMessagingDelegateProtocol, UNUserNotificationCenterDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    actual fun tokenListener(callback: (Result<String?>) -> Unit) {
        CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {

            try {
                FCM.shared().setOnTokenReceived {

                    callback(Result.success(it))

                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun onNotificationReceived(callback: (Result<((Map<Any?, *>) -> Unit)?>) -> Unit) {
        CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {

            try {
                val result = FCM.shared().onNotificationReceived()
                callback(Result.success(result))

            } catch (e: Exception) {
                callback(Result.failure(e))

            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun requestAuthorization() {
        CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
            FCM.shared().requestAuthorization()
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun getToken(callback: (Result<String?>) -> Unit) {
        CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {

            try {
                callback(Result.success(FIRMessaging.messaging().FCMToken))
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }
}


