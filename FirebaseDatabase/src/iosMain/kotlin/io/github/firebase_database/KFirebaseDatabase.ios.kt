package io.github.firebase_database

import cocoapods.FirebaseDatabase.FIRDataEventType
import cocoapods.FirebaseDatabase.FIRDatabase
import cocoapods.FirebaseDatabase.FIRDatabaseReference
import cocoapods.FirebaseDatabase.FIRDataSnapshot
import platform.Foundation.NSError

// Class to handle Firebase Database operations
actual class KFirebaseDatabase {
    private val databaseRef: FIRDatabaseReference = FIRDatabase.database().reference()

    actual fun write(
        path: String,
        data: Map<String, Any>,
        callback: (Result<Boolean?>) -> Unit
    ) {
        val ref = databaseRef.child(path)
        ref.setValue(data) { error: NSError?, _ ->
            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
            } else {
                callback(Result.success(true))
            }
        }
    }

    actual fun read(path: String, callback: (Result<Any?>) -> Unit) {
        val ref = databaseRef.child(path)
        ref.observeSingleEventOfType(FIRDataEventType.FIRDataEventTypeValue) { snapshot: FIRDataSnapshot?, error: String? ->
            if (error != null) {
                callback(Result.failure(FirebaseDatabaseException("Error reading data: $error")))
            } else {
                callback(Result.success(snapshot?.value))
            }
        }
    }

    actual fun writeList(
        path: String,
        dataList: List<Map<String, Any>>,
        callback: (Result<Boolean>) -> Unit
    ) {
        val updates = mutableMapOf<String, Any>()
        dataList.forEachIndexed { index, data ->
            updates["$path/$index"] = data
        }

        databaseRef.updateChildValues(convertMutableMapToMap(updates)) { error: NSError?, _ ->
            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
            } else {
                callback(Result.success(true))
            }
        }
    }

    actual fun readList(
        path: String,
        callback: (Result<List<Any?>>) -> Unit
    ) {
        val ref = databaseRef.child(path)
        ref.observeSingleEventOfType(FIRDataEventType.FIRDataEventTypeValue) { snapshot: FIRDataSnapshot?, error: String? ->
            if (error != null) {
                callback(Result.failure(FirebaseDatabaseException("Error reading list: $error")))
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
                callback(Result.success(dataList))
            }
        }
    }

    actual fun delete(
        path: String,
        callback: (Result<Boolean?>) -> Unit
    ) {
        try {
            databaseRef.child(path).removeValue()

        }catch (e:Exception){
            callback(Result.failure(e))
        }
    }

    actual fun update(
        path: String,
        data: Map<String, Any>,
        callback: (Result<Boolean?>) -> Unit
    ) {
        databaseRef.child(path).updateChildValues(convertToAnyKeyMap(data)){ error , _ ->
           if(error != null){
               callback(Result.failure(error.convertNSErrorToException()))
               return@updateChildValues
           }
            else {
                callback(Result.success(true))
           }
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
    return mutableMap.mapKeys { it.key as Any? } // Convert keys to Any?
}

fun convertToAnyKeyMap(map: Map<String, Any>): Map<Any?, *> {
    return map.mapKeys { it.key as Any? }  // Converts the keys to Any?
}