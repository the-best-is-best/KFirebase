package io.github.firebase_firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class KFirebaseFirestore {

    private val firestore = FirebaseFirestore.getInstance()

    actual suspend fun addDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>
    ): Result<Boolean> = suspendCancellableCoroutine { cont ->
        try {
            firestore.collection(collection).document(documentId).set(data)
                .addOnSuccessListener { cont.resume(Result.success(true)) }
                .addOnFailureListener { exception -> cont.resumeWithException(exception) }
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }

    actual suspend fun getDocuments(
        collection: String
    ): Result<List<Map<String, Any?>>> = suspendCancellableCoroutine { cont ->
        firestore.collection(collection).get()
            .addOnSuccessListener { querySnapshot ->
                val documents = querySnapshot.documents.mapNotNull { it.data }
                cont.resume(Result.success(documents))
            }
            .addOnFailureListener { exception -> cont.resumeWithException(exception) }
    }

    actual suspend fun getDocumentById(
        collection: String,
        documentId: String
    ): Result<Map<String, Any?>> = suspendCancellableCoroutine { cont ->
        firestore.collection(collection).document(documentId).get()
            .addOnSuccessListener { document ->
                val result = document.data
                cont.resume(Result.success(result ?: emptyMap()))
            }
            .addOnFailureListener { exception -> cont.resumeWithException(exception) }
    }

    actual suspend fun queryDocuments(
        collection: String,
        filters: List<Map<String, Comparable<*>>>,
        orderBy: String?,
        limit: Long?
    ): Result<List<Map<String, Any?>>> = suspendCancellableCoroutine { cont ->
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
            cont.resume(Result.success(documents))
        }.addOnFailureListener { exception -> cont.resumeWithException(exception) }
    }

    actual suspend fun updateDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>
    ): Result<Boolean> = suspendCancellableCoroutine { cont ->
        try {
            firestore.collection(collection).document(documentId).set(data)
                .addOnSuccessListener { cont.resume(Result.success(true)) }
                .addOnFailureListener { exception -> cont.resumeWithException(exception) }
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }

    actual suspend fun deleteDocument(
        collection: String,
        documentId: String
    ): Result<Unit> = suspendCancellableCoroutine { cont ->
        firestore.collection(collection).document(documentId).delete()
            .addOnSuccessListener { cont.resume(Result.success(Unit)) }
            .addOnFailureListener { exception -> cont.resumeWithException(exception) }
    }

    actual suspend fun batchWrite(
        addOperations: List<Pair<String, Any>>,
        updateOperations: List<Triple<String, String, Any>>,
        deleteOperations: List<Pair<String, String>>
    ): Result<Unit> = suspendCancellableCoroutine { cont ->
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
            cont.resume(Result.success(Unit))
        }.addOnFailureListener { exception -> cont.resumeWithException(exception) }
    }


    actual fun listenToCollection(
        collection: String,
        listenToCollectionId: String
    ): Flow<Result<List<Map<String, Any?>>>> = callbackFlow {
        val firestore = FirebaseFirestore.getInstance()

        // Register the snapshot listener
        val registration = firestore.collection(collection)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    // Resume the coroutine with an exception
                    trySend(Result.failure(firebaseFirestoreException))
                    return@addSnapshotListener
                }

                // Convert the documents from the snapshot to a list of maps
                val documents =
                    querySnapshot?.documents?.map { it.data ?: emptyMap<String, Any?>() }
                        ?: emptyList()

                // Resume the coroutine with the result
                trySend(Result.success(documents))
            }

        // Store the listener registration for future reference

        // Close the channel when the listener is no longer needed
        awaitClose {
            registration.remove() // Remove the listener when the coroutine is cancelled
        }
    }


}
