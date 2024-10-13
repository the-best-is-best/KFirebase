package io.github.firebase_firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

actual class KFirebaseFirestore {

    private val firestore = FirebaseFirestore.getInstance()

    // Maintain a map of listenerId to ListenerRegistration
    private val listenerRegistrations: MutableMap<String, ListenerRegistration> = mutableMapOf()

    actual fun addDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>,
        callback: (Result<Unit>) -> Unit
    ) {
        try {
            firestore.collection(collection).document(documentId).set(data).addOnSuccessListener {
                callback(Result.success(Unit))
            }.addOnFailureListener { exception ->
                callback(Result.failure(exception))
            }
        } catch (e: Exception) {
            callback(Result.failure(e))
        }
    }

    actual fun getDocuments(
        collection: String,
        callback: (Result<List<Map<String, Any?>>>) -> Unit
    ) {
        firestore.collection(collection).get().addOnSuccessListener { querySnapshot ->
            val documents = querySnapshot.documents.mapNotNull { doc ->
                try {
                    doc.data
                } catch (e: Exception) {
                    null // Handle deserialization error
                }
            }
            callback(Result.success(documents))
        }.addOnFailureListener { exception ->
            callback(Result.failure(exception))
        }
    }

    actual fun getDocumentById(
        collection: String,
        documentId: String,
        callback: (Result<Map<String, Any?>>) -> Unit
    ) {
        firestore.collection(collection).document(documentId).get()
            .addOnSuccessListener { document ->
                val result = document.data
                callback(Result.success(result!!.toMap()))
            }.addOnFailureListener { exception ->
                callback(Result.failure(exception))
            }
    }

    actual fun queryDocuments(
        collection: String,
        filters: List<Map<String, Comparable<*>>>,
        orderBy: String?,
        limit: Long?,
        callback: (Result<List<Map<String, Any?>>>) -> Unit
    ) {
        var query: Query = firestore.collection(collection)

        filters.forEach { filter ->
            val field = filter["field"] as? String ?: return@forEach
            val operator = filter["operator"] as? String ?: return@forEach
            val value = filter["value"] ?: return@forEach

            when (operator) {
                "==" -> query = query.whereEqualTo(field, value)
                "!=" -> query = query.whereNotEqualTo(field, value)
                "<" -> query = query.whereLessThan(field, value)
                "<=" -> query = query.whereLessThanOrEqualTo(field, value)
                ">" -> query = query.whereGreaterThan(field, value)
                ">=" -> query = query.whereGreaterThanOrEqualTo(field, value)
                "array-contains" -> query = query.whereArrayContains(field, value)
                "array-contains-any" -> if (value is List<*>) query =
                    query.whereArrayContainsAny(field, value)

                "in" -> if (value is List<*>) query = query.whereIn(field, value)
                "not-in" -> if (value is List<*>) query = query.whereNotIn(field, value)
            }
        }

        orderBy?.let { query = query.orderBy(it) }
        limit?.let { query = query.limit(it) }

        query.get().addOnSuccessListener { querySnapshot ->
            val documents = querySnapshot.documents.mapNotNull { it.data }
            callback(Result.success(documents))
        }.addOnFailureListener { exception ->
            callback(Result.failure(exception))
        }
    }

    actual fun updateDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>,
        callback: (Result<Unit>) -> Unit
    ) {
        try {
            firestore.collection(collection).document(documentId).set(data).addOnSuccessListener {
                callback(Result.success(Unit))
            }.addOnFailureListener { exception ->
                callback(Result.failure(exception))
            }
        } catch (e: Exception) {
            callback(Result.failure(e))
        }
    }

    actual fun deleteDocument(
        collection: String,
        documentId: String,
        callback: (Result<Unit>) -> Unit
    ) {
        firestore.collection(collection).document(documentId).delete().addOnSuccessListener {
            callback(Result.success(Unit))
        }.addOnFailureListener { exception ->
            callback(Result.failure(exception))
        }
    }

    actual fun batchWrite(
        addOperations: List<Pair<String, Any>>,
        updateOperations: List<Triple<String, String, Any>>,
        deleteOperations: List<Pair<String, String>>,
        callback: (Result<Unit>) -> Unit
    ) {
        val batch = firestore.batch()

        addOperations.forEach { (collection, data) ->
            val documentRef = firestore.collection(collection).document() // Auto ID
            batch.set(documentRef, data)
        }

        updateOperations.forEach { (collection, documentId, data) ->
            val documentRef = firestore.collection(collection).document(documentId)
            batch.set(documentRef, data)
        }

        deleteOperations.forEach { (collection, documentId) ->
            val documentRef = firestore.collection(collection).document(documentId)
            batch.delete(documentRef)
        }

        batch.commit().addOnSuccessListener {
            callback(Result.success(Unit))
        }.addOnFailureListener { exception ->
            callback(Result.failure(exception))
        }
    }

    actual fun listenToCollection(
        collection: String,
        listenerId: String,
        callback: (Result<List<Map<String, Any?>>>) -> Unit
    ) {
        val registration = firestore.collection(collection)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    callback(Result.failure(firebaseFirestoreException))
                    return@addSnapshotListener
                }

                val documents = querySnapshot?.documents?.map { documentSnapshot ->
                    documentSnapshot.data ?: emptyMap<String, Any?>()
                } ?: emptyList()

                callback(Result.success(documents))
            }

        // Add to listener registrations map
        listenerRegistrations[listenerId] = registration
    }

    actual fun stopListenerCollection(listenerId: String) {
        listenerRegistrations[listenerId]?.remove() // Stop the listener
        listenerRegistrations.remove(listenerId) // Remove it from the map
    }

    actual fun stopAllListeners() {
        listenerRegistrations.values.forEach { it.remove() } // Stop all listeners
        listenerRegistrations.clear() // Clear the map
    }
}
