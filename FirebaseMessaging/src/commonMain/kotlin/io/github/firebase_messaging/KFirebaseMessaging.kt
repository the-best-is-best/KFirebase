// commonMain/src/io/github/firebase_messaging/KFirebaseMessaging.kt

package io.github.firebase_messaging

interface KFirebaseMessaging {
    fun setTokenListener(callback: (Result<String?>) -> Unit)
    fun setNotificationListener(callback: (Result<Map<Any?, *>?>) -> Unit)
    fun setNotificationClickedListener(callback: (Result<Map<Any?, *>?>) -> Unit)
    fun requestAuthorization()
    fun getToken(callback: (Result<String?>) -> Unit)
    fun subscribeTopic(name: String)
    fun unsubscribeTopic(name: String)


    companion object {
        // Factory function to initialize platform-specific implementation
        fun create(): KFirebaseMessaging = getPlatformFirebaseMessaging()
    }
}

// Use `expect` to get platform-specific implementations
expect fun getPlatformFirebaseMessaging(): KFirebaseMessaging
