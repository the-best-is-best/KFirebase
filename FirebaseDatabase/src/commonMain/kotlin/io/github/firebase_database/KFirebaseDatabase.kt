package io.github.firebase_database

expect class KFirebaseDatabase() {
    suspend fun write(path: String, data: Map<String, Any>): Result<Boolean?>
    suspend fun read(path: String): Result<Any?>
    suspend fun writeList(path: String, dataList: List<Map<String, Any>>): Result<Boolean>
    suspend fun readList(path: String): Result<List<Any?>>
    suspend fun delete(path: String): Result<Boolean?>
    suspend fun update(path: String, data: Map<String, Any>): Result<Boolean?>

    // Listen for real-time changes at the specified path
    suspend fun addObserveListener(path: String): Result<Any?>

    // Remove all listeners for the specified path
    fun removeObserver(path: String)
}
