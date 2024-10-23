package io.github.firebase_database

import kotlinx.coroutines.flow.Flow

expect class KFirebaseDatabase() {
    suspend fun write(path: String, data: Map<String, Any>): Result<Boolean?>
    suspend fun read(path: String): Result<Any?>
    suspend fun writeList(path: String, dataList: List<Map<String, Any>>): Result<Boolean>
    suspend fun readList(path: String): Result<List<Any?>>
    suspend fun delete(path: String): Result<Boolean?>
    suspend fun update(path: String, data: Map<String, Any>): Result<Boolean?>

    // Listen for real-time changes at the specified path
    fun addObserveValueListener(path: String): Flow<Result<Any?>>
}
