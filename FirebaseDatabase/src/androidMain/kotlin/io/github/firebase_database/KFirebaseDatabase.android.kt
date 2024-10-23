package io.github.firebase_database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class KFirebaseDatabase {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    actual suspend fun write(path: String, data: Map<String, Any>): Result<Boolean?> {
        return suspendCancellableCoroutine { cont ->
            database.child(path).setValue(data).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(Result.success(true))
                } else {
                    cont.resumeWithException(task.exception ?: Exception("Unknown error"))
                }
            }
        }
    }

    actual suspend fun read(path: String): Result<Any?> {
        return suspendCancellableCoroutine { cont ->
            database.child(path).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(Result.success(task.result?.value))
                } else {
                    cont.resumeWithException(task.exception ?: Exception("Unknown error"))
                }
            }
        }
    }

    actual suspend fun writeList(path: String, dataList: List<Map<String, Any>>): Result<Boolean> {
        val updates = hashMapOf<String, Any>()
        dataList.forEachIndexed { index, data ->
            updates["$path/$index"] = data
        }
        return suspendCancellableCoroutine { cont ->
            database.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(Result.success(true))
                } else {
                    cont.resumeWithException(task.exception ?: Exception("Unknown error"))
                }
            }
        }
    }

    actual suspend fun readList(path: String): Result<List<Any?>> {
        return suspendCancellableCoroutine { cont ->
            database.child(path).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataList = task.result?.children?.map { it.value } ?: emptyList()
                    cont.resume(Result.success(dataList))
                } else {
                    cont.resumeWithException(task.exception ?: Exception("Unknown error"))
                }
            }
        }
    }

    actual suspend fun delete(path: String): Result<Boolean?> {
        return suspendCancellableCoroutine { cont ->
            database.child(path).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(Result.success(true))
                } else {
                    cont.resumeWithException(task.exception ?: Exception("Unknown error"))
                }
            }
        }
    }

    actual suspend fun update(path: String, data: Map<String, Any>): Result<Boolean?> {
        return suspendCancellableCoroutine { cont ->
            database.child(path).updateChildren(data).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(Result.success(true))
                } else {
                    cont.resumeWithException(task.exception ?: Exception("Unknown error"))
                }
            }
        }
    }


    actual fun addObserveValueListener(path: String): Flow<Result<Any?>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Emit the data from the snapshot as a Result
                val data = snapshot.getValue(Any::class.java) // Adjust type as necessary
                trySend(Result.success(data))
            }

            override fun onCancelled(error: DatabaseError) {
                // Emit an error result if the operation is cancelled
                trySend(Result.failure(Exception(error.message)))
            }
        }

        // Add the listener to the Firebase database
        database.child(path).addValueEventListener(listener)

        // Cleanup when the flow is closed
        awaitClose {
            database.child(path).removeEventListener(listener)
        }
    }


}
