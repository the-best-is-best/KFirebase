package io.github.firebase_messaging

expect object KFirebaseMessaging {
    // Set a listener for the token
    fun setTokenListener(callback: (String?) -> Unit)
//    fun setNotificationListener(callback: (Map<Any?, *>?) -> Unit)
//    fun setNotificationClickedListener(callback: (Map<Any?, *>?) -> Unit)


    // Get the current token
    suspend fun getToken(): Result<String?>

    // Subscribe to a topic
    suspend fun subscribeTopic(name: String): Result<Boolean>

    // Unsubscribe from a topic
    suspend fun unsubscribeTopic(name: String): Result<Boolean>


}
