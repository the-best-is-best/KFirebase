package io.github.firebase_messaging

expect class KFirebaseMessaging() {
    fun tokenListener(callback: (Result<String?>) -> Unit)
    fun onNotificationReceived(callback: (Result<((Map<Any?, *>) -> Unit)?>) -> Unit)
    fun requestAuthorization()
    fun getToken(callback: (Result<String?>) -> Unit)

}
