package io.github.firebase_firestore

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSNumber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class KFirebaseFirestore {

    private val firestore = cocoapods.kfirebaseFirestore.KFirebaseFirestore()

    actual suspend fun addDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>
    ): Result<Boolean> = suspendCancellableCoroutine { cont ->
        firestore.addDocs(collection, documentId, convertStringMapToAnyMap(data)) { callbackIos ->
            val error = callbackIos?.error()
            if (error != null) {
                cont.resumeWith(Result.failure(error.convertNSErrorToException()))
            } else {
                cont.resume(Result.success(true)) // This indicates that the document was successfully added

            }
        }

    }

    actual suspend fun getDocuments(
        collection: String
    ): Result<List<Map<String, Any?>>> = suspendCancellableCoroutine { cont ->
        firestore.getDocs(collection) { callbackIos ->
            val error = callbackIos?.error()
            val dataDoc = callbackIos?.data()
            if (error != null) {
                cont.resumeWith(Result.failure(error.convertNSErrorToException()))
            } else {
                cont.resume(Result.success(convertToListOfMaps(dataDoc)))
            }
        }
    }

    actual suspend fun getDocumentById(
        collection: String,
        documentId: String
    ): Result<Map<String, Any?>> = suspendCancellableCoroutine { cont ->
        firestore.getDocById(collection, documentId) { callbackIos ->
            val error = callbackIos?.error()
            val data = callbackIos?.data()
            if (error != null) {
                cont.resumeWith(Result.failure(error.convertNSErrorToException()))
            } else {
                cont.resume(Result.success(convertAnyMapToStringMap(data)))
            }
        }
    }


    actual fun listenToCollection(
        collection: String,
        listenToCollectionId: String
    ): Flow<Result<List<Map<String, Any?>>>> = callbackFlow {
        firestore.startRealTimeListener(collection, listenToCollectionId) { callbackIos ->
            val error = callbackIos?.error()
            val data = callbackIos?.data()

            if (error != null) {
                // Resume the flow with a failure
                try {
                    throw error.convertNSErrorToException()
                } catch (e: Exception) {
                    // Send the exception as a failure result
                    trySend(Result.failure(e))
                }
            } else {
                // Convert the data and send as a success result
                trySend(Result.success(convertToListOfMaps(data)))
            }
        }

        // Clean up the listener when the flow is cancelled
        awaitClose {
            firestore.stopRealTimeListenerById(listenToCollectionId)
        }
    }


    actual suspend fun queryDocuments(
        collection: String,
        filters: List<Map<String, Comparable<*>>>,
        orderBy: String?,
        limit: Long?
    ): Result<List<Map<String, Any?>>> = suspendCancellableCoroutine { cont ->
        firestore.getDocsByFilter(
            collection,
            orderBy,
            convertLongToNSNumber(limit),
            filters
        ) { callbackIos ->
            val error = callbackIos?.error()
            val data = callbackIos?.data()
            if (error != null) {
                cont.resumeWith(Result.failure(error.convertNSErrorToException()))
            } else {
                cont.resume(Result.success(convertToListOfMaps(data)))
            }
        }
    }

    actual suspend fun updateDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>
    ): Result<Boolean> = suspendCancellableCoroutine { cont ->
        firestore.updateDocument(
            collection,
            documentId,
            convertStringMapToAnyMap(data)
        ) { callbackIos ->
            val error = callbackIos?.error()
            if (error != null) {
                cont.resumeWithException(error.convertNSErrorToException())
            } else {
                cont.resume(Result.success(true))
            }
        }
    }

    actual suspend fun deleteDocument(
        collection: String,
        documentId: String
    ): Result<Unit> = suspendCancellableCoroutine { cont ->
        firestore.deleteDoc(collection, documentId) { callbackIos ->
            val error = callbackIos?.error()
            if (error != null) {
                cont.resumeWith(Result.failure(error.convertNSErrorToException()))
            } else {
                cont.resume(Result.success(Unit))
            }
        }
    }

    actual suspend fun batchWrite(
        addOperations: List<Pair<String, Any>>,
        updateOperations: List<Triple<String, String, Any>>,
        deleteOperations: List<Pair<String, String>>
    ): Result<Unit> = suspendCancellableCoroutine { cont ->
        firestore.batchWriteDoc(addOperations, updateOperations, deleteOperations) { callbackIos ->
            val error = callbackIos?.error()
            if (error != null) {
                cont.resumeWith(Result.failure(error.convertNSErrorToException()))
            } else {
                cont.resume(Result.success(Unit))
            }
        }
    }

    // Helper functions
    private fun convertStringMapToAnyMap(input: Map<String, Any?>): Map<Any?, *> {
        return input.mapKeys { it.key } // Convert keys to Any?
            .mapValues { it.value } // Keep values as is
            .toMap() // Convert back to a Map
    }

    private fun convertToListOfMaps(input: List<*>?): List<Map<String, Any?>> {
        return input?.mapNotNull { it as? Map<String, Any?> } ?: emptyList()
    }

    private fun convertAnyMapToStringMap(input: Map<Any?, *>?): Map<String, Any?> {
        return input?.mapNotNull { (key, value) ->
            (key as? String)?.let { it to value }
        }?.toMap() ?: emptyMap()
    }

    private fun convertLongToNSNumber(longValue: Long?): NSNumber? {
        return longValue?.let { NSNumber(it.toDouble()) }
    }
}
