package io.github.firebase_messaging

interface KFirebaseMessaging {
    // Set a listener for the token
    fun setTokenListener(callback: (Result<String?>) -> Unit)
    fun setNotificationListener(callback: (Result<Map<Any?, *>?>) -> Unit)
    fun setNotificationClickedListener(callback: (Result<Map<Any?, *>?>) -> Unit)


    // Request authorization for notifications
    suspend fun requestAuthorization(): Result<Boolean>

    // Get the current token
    suspend fun getToken(): Result<String?>

    // Subscribe to a topic
    suspend fun subscribeTopic(name: String): Result<Boolean>

    // Unsubscribe from a topic
    suspend fun unsubscribeTopic(name: String): Result<Boolean>

    companion object {
        // Factory function to initialize platform-specific implementation
        fun create(): KFirebaseMessaging = getPlatformFirebaseMessaging()
    }
}

// Use `expect` to get platform-specific implementations
expect fun getPlatformFirebaseMessaging(): KFirebaseMessaging
