package io.github.firebase_database

expect class KFirebaseDatabase() {
    fun write(path: String, data: Map<String, Any>, callback: (Result<Boolean?>) -> Unit)
    fun read(path: String, callback: (Result<Any?>) -> Unit)
    fun writeList(path: String, dataList: List<Map<String, Any>>, callback: (Result<Boolean>) -> Unit)
    fun readList(path: String, callback: (Result<List<Any?>>) -> Unit)
    fun delete(path: String, callback: (Result<Boolean?>) -> Unit)
    fun update(path: String, data: Map<String, Any>, callback: (Result<Boolean?>) -> Unit)

    // Listen for real-time changes at the specified path
    fun addObserveListener(path: String, callback: (Result<Any?>) -> Unit)

    // Remove all listeners for the specified path
    fun removeObserver(path: String)
}
