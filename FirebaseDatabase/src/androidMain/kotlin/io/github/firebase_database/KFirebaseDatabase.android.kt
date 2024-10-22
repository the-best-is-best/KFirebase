package io.github.firebase_database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class KFirebaseDatabase {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val listenersMap = mutableMapOf<String, ValueEventListener>()

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

    actual suspend fun addObserveListener(path: String): Result<Any?> {
        return suspendCancellableCoroutine { cont ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cont.resume(Result.success(snapshot.value))
                }

                override fun onCancelled(error: DatabaseError) {
                    cont.resumeWithException(Exception(error.message))
                }
            }
            database.child(path).addValueEventListener(listener)
            listenersMap[path] = listener
        }
    }

    actual fun removeObserver(path: String) {
        val listener = listenersMap.remove(path)
        if (listener != null) {
            database.child(path).removeEventListener(listener)
        }
    }
}
