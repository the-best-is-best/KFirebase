package io.github.firebase_database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

actual class KFirebaseDatabase {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val listenersMap = mutableMapOf<String, ValueEventListener>()

    actual fun write(path: String, data: Map<String, Any>, callback: (Result<Boolean?>) -> Unit) {
        database.child(path).setValue(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(Result.success(true))
            } else {
                callback(Result.failure(task.exception ?: Exception("Unknown error")))
            }
        }
    }

    actual fun read(path: String, callback: (Result<Any?>) -> Unit) {
        database.child(path).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(Result.success(task.result?.value))
            } else {
                callback(Result.failure(task.exception ?: Exception("Unknown error")))
            }
        }
    }

    actual fun writeList(path: String, dataList: List<Map<String, Any>>, callback: (Result<Boolean>) -> Unit) {
        val updates = hashMapOf<String, Any>()
        dataList.forEachIndexed { index, data ->
            updates["$path/$index"] = data
        }
        database.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(Result.success(true))
            } else {
                callback(Result.failure(task.exception ?: Exception("Unknown error")))
            }
        }
    }

    actual fun readList(path: String, callback: (Result<List<Any?>>) -> Unit) {
        database.child(path).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataList = task.result?.children?.map { it.value } ?: emptyList()
                callback(Result.success(dataList))
            } else {
                callback(Result.failure(task.exception ?: Exception("Unknown error")))
            }
        }
    }

    actual fun delete(
        path: String,
        callback: (Result<Boolean?>) -> Unit
    ) {
        try {
            database.child(path).removeValue().addOnCompleteListener { task ->
                val exc = task.exception
                val success = task.isSuccessful

                if(!success){
                    callback(Result.failure(Exception(exc)))
                    return@addOnCompleteListener
                }else{
                    callback(Result.success(true))
                }
            }

        } catch (e:Exception){
            callback(Result.failure(e))
        }
    }

    actual fun update(
        path: String,
        data:Map<String, Any>,
        callback: (Result<Boolean?>) -> Unit
    ) {
        try {
            database.child(path).updateChildren(data).addOnCompleteListener { task ->
                 val exc = task.exception
                val isSuccess = task.isSuccessful

                if(!isSuccess){
                    callback(Result.failure(Exception(exc)))
                    return@addOnCompleteListener
                }
                else{
                    callback(Result.success(true))
                }
            }

        } catch (e:Exception){
            callback(Result.failure(e))
        }
    }

    actual fun addObserveListener(
        path: String,
        callback: (Result<Any?>) -> Unit
    ) {
        val ref = database.child(path)
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(Result.success(snapshot.value))
            }

            override fun onCancelled(error: DatabaseError) {
                callback(Result.failure(Exception(error.message)))
            }
        })
        listenersMap[path] = listener
    }

    actual fun removeObserver(path: String) {
        val listener = listenersMap.remove(path)
        if (listener != null) {
            database.child(path).removeEventListener(listener)
        }
    }

}
