package io.github.firebase_firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

actual class KFirebaseFirestore {

    val firestore = FirebaseFirestore.getInstance()


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

//    actual  fun listenToCollection(
//        collection: String,
//         callback: (Result<List<Map<String, Any?>>>) -> Unit
//    ) {
//        firestore.collection(collection).addSnapshotListener { snapshot, exception ->
//            if (exception != null) {
//                callback(Result.failure(exception))
//                return@addSnapshotListener
//            }
//            val documents = snapshot?.documents?.mapNotNull { doc ->
//                try {
//                    doc.data
//                } catch (e: Exception) {
//                    null // Handle deserialization error
//                }
//            } ?: emptyList()
//            callback(Result.success(documents))
//        }
//    }

    actual fun queryDocuments(
        collection: String,
        filters: List<Map<String, Comparable<*>>>, // List of maps for filters
        orderBy: String?,
        limit: Long?,
        callback: (Result<List<Map<String, Any?>>>) -> Unit
    ) {
        var query: Query = firestore.collection(collection)

        // Iterate through the filters
        filters.forEach { filter ->
            val field = filter["field"] as? String ?: return@forEach // Extract field name
            val operator = filter["operator"] as? String ?: return@forEach // Extract operator
            val value = filter["value"] ?: return@forEach // Extract value

            // Handle different cases based on the operator
            when (operator) {
                "==" -> query = query.whereEqualTo(field, value)
                "!=" -> query = query.whereNotEqualTo(field, value)
                "<" -> query = query.whereLessThan(field, value)
                "<=" -> query = query.whereLessThanOrEqualTo(field, value)
                ">" -> query = query.whereGreaterThan(field, value)
                ">=" -> query = query.whereGreaterThanOrEqualTo(field, value)
                "array-contains" -> query = query.whereArrayContains(field, value)
                "array-contains-any" -> {
                    if (value is List<*>) {
                        query = query.whereArrayContainsAny(field, value)
                    }
                }

                "in" -> {
                    if (value is List<*>) {
                        query = query.whereIn(field, value)
                    }
                }

                "not-in" -> {
                    if (value is List<*>) {
                        query = query.whereNotIn(field, value)
                    }
                }
                // Handle additional operators as needed
            }
        }

        // Add order by clause if provided
        orderBy?.let { query = query.orderBy(it) }

        // Add limit if provided
        limit?.let { query = query.limit(it) }

        // Execute the query and return the result
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
        addOperations: List<Pair<String, Any>>, // collection and data
        updateOperations: List<Triple<String, String, Any>>, // collection, documentId, data
        deleteOperations: List<Pair<String, String>>, // collection and documentId
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
}
