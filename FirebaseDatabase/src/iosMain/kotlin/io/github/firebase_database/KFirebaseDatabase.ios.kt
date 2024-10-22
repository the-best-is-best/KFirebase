package io.github.firebase_database

import cocoapods.FirebaseDatabase.FIRDataEventType
import cocoapods.FirebaseDatabase.FIRDataSnapshot
import cocoapods.FirebaseDatabase.FIRDatabase
import cocoapods.FirebaseDatabase.FIRDatabaseHandle
import cocoapods.FirebaseDatabase.FIRDatabaseReference
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Class to handle Firebase Database operations
actual class KFirebaseDatabase {
    private val databaseRef: FIRDatabaseReference = FIRDatabase.database().reference()
    private val listenersMap = mutableMapOf<String, FIRDatabaseHandle>()

    actual suspend fun write(
        path: String,
        data: Map<String, Any>
    ): Result<Boolean?> = suspendCancellableCoroutine { continuation ->
        val ref = databaseRef.child(path)
        ref.setValue(data) { error: NSError?, _ ->
            if (error != null) {
                continuation.resumeWithException(error.convertNSErrorToException())
            } else {
                continuation.resume(Result.success(true))
            }
        }
    }

    actual suspend fun read(path: String): Result<Any?> =
        suspendCancellableCoroutine { continuation ->
        val ref = databaseRef.child(path)
        ref.observeSingleEventOfType(FIRDataEventType.FIRDataEventTypeValue) { snapshot: FIRDataSnapshot?, error: String? ->
            if (error != null) {
                continuation.resumeWithException(FirebaseDatabaseException("Error reading data: $error"))
            } else {
                continuation.resume(Result.success(snapshot?.value))
            }
        }
    }

    actual suspend fun writeList(
        path: String,
        dataList: List<Map<String, Any>>
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val updates = mutableMapOf<String, Any>()
        dataList.forEachIndexed { index, data ->
            updates["$path/$index"] = data
        }

        databaseRef.updateChildValues(convertMutableMapToMap(updates)) { error: NSError?, _ ->
            if (error != null) {
                continuation.resumeWithException(error.convertNSErrorToException())
            } else {
                continuation.resume(Result.success(true))
            }
        }
    }

    actual suspend fun readList(path: String): Result<List<Any?>> =
        suspendCancellableCoroutine { continuation ->
        val ref = databaseRef.child(path)
        ref.observeSingleEventOfType(FIRDataEventType.FIRDataEventTypeValue) { snapshot: FIRDataSnapshot?, error: String? ->
            if (error != null) {
                continuation.resumeWithException(FirebaseDatabaseException("Error reading list: $error"))
            } else {
                val dataList = mutableListOf<Any?>()
                val children = snapshot?.children // This returns NSEnumerator

                // Use a loop to iterate through the NSEnumerator
                children?.let {
                    while (it.nextObject() != null) {
                        val childSnapshot = it.nextObject() as FIRDataSnapshot
                        dataList.add(childSnapshot.value)
                    }
                }
                continuation.resume(Result.success(dataList))
            }
        }
    }

    actual suspend fun delete(path: String): Result<Boolean?> =
        suspendCancellableCoroutine { continuation ->
            val ref = databaseRef.child(path)
            ref.removeValueWithCompletionBlock { error: NSError?, _ ->
                if (error != null) {
                    continuation.resumeWithException(error.convertNSErrorToException())
                } else {
                    continuation.resume(Result.success(true))
                }
        }
    }

    actual suspend fun update(path: String, data: Map<String, Any>): Result<Boolean?> =
        suspendCancellableCoroutine { continuation ->
            databaseRef.child(path).updateChildValues(convertToAnyKeyMap(data)) { error, _ ->
                if (error != null) {
                    continuation.resumeWithException(error.convertNSErrorToException())
                } else {
                    continuation.resume(Result.success(true))
                }
        }
    }

    actual suspend fun addObserveListener(path: String): Result<Any?> =
        suspendCancellableCoroutine { continuation ->
        val ref = databaseRef.child(path)
            val handle =
                ref.observeEventType(FIRDataEventType.FIRDataEventTypeValue) { snapshot, error ->
                    if (error != null) {
                        continuation.resumeWithException(FirebaseDatabaseException(error))
                    } else {
                        continuation.resume(Result.success(snapshot?.value))
                    }
                }
        listenersMap[path] = handle
    }

    actual fun removeObserver(path: String) {
        val handle = listenersMap.remove(path)
        if (handle != null) {
            databaseRef.child(path).removeObserverWithHandle(handle)
        }
    }
}

// Extension function to convert NSError to a custom Exception
fun NSError.convertNSErrorToException(): Exception {
    return FirebaseDatabaseException(
        message = this.localizedDescription,
        cause = Throwable(this.localizedFailureReason) // You can customize this as needed
    )
}

// Custom Exception class for Firebase Database errors
class FirebaseDatabaseException(message: String?, cause: Throwable? = null) : Exception(message, cause)


fun convertMutableMapToMap(mutableMap: MutableMap<String, Any>): Map<Any?, *> {
    return mutableMap.mapKeys { it.key } // Convert keys to Any?
}

fun convertToAnyKeyMap(map: Map<String, Any>): Map<Any?, *> {
    return map.mapKeys { it.key }  // Converts the keys to Any?
}