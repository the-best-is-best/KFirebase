package io.github.firebase_firestore

import platform.Foundation.NSNumber

actual class KFirebaseFirestore {

    val firestore = cocoapods.kfirebaseFirestore.KFirebaseFirestore()
    actual fun addDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>,
        callback: (Result<Unit>) -> Unit
    ) {

        firestore.addDocs(collection, documentId, convertStringMapToAnyMap(data)) { callbackIos ->
            if (callbackIos?.error() != null) {
                callback(Result.failure(callbackIos.error()!!.convertNSErrorToException()))
            } else {
                callback(Result.success(Unit))
            }
        }

    }

    actual fun getDocuments(
        collection: String,
        callback: (Result<List<Map<String, Any?>>>) -> Unit
    ) {
        firestore.getDocs(collection) { callbackIos ->
            val error = callbackIos?.error()
            val dataDoc = callbackIos?.data()

            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
            } else {
                callback(Result.success(convertToListOfMaps(dataDoc)))
            }

        }

    }

    actual fun getDocumentById(
        collection: String,
        documentId: String,
        callback: (Result<Map<String, Any?>>) -> Unit
    ) {
        firestore.getDocById(collection, documentId) { callbackIos ->
            val error = callbackIos?.error()
            val data = callbackIos?.data()

            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
            } else {
                callback(Result.success(convertAnyMapToStringMap(data)))
            }
        }

    }

    actual fun queryDocuments(
        collection: String,
        filters: List<Map<String, Comparable<*>>>,
        orderBy: String?,
        limit: Long?,
        callback: (Result<List<Map<String, Any?>>>) -> Unit
    ) {

        firestore.getDocsByFilter(
            collection,
            orderBy,
            convertLongToNSNumber(limit),
            filters
        ) { callbackIos ->
            val error = callbackIos?.error()
            val data = callbackIos?.data()

            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
            } else {
                callback(Result.success(convertToListOfMaps(data)))
            }
        }
    }


    actual fun updateDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>,
        callback: (Result<Unit>) -> Unit
    ) {
        firestore.updateDocument(
            collection,
            documentId,
            convertStringMapToAnyMap(data)
        ) { callbackIos ->
            val error = callbackIos?.error()
            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
            } else {
                callback(Result.success(Unit))
            }
        }


    }

    actual fun deleteDocument(
        collection: String,
        documentId: String,
        callback: (Result<Unit>) -> Unit
    ) {
        firestore.deleteDoc(collection, documentId) { callbackIos ->
            val error = callbackIos?.error()

            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
            } else {
                callback(Result.success(Unit))
            }
        }


    }

    actual fun batchWrite(
        addOperations: List<Pair<String, Any>>,
        updateOperations: List<Triple<String, String, Any>>,
        deleteOperations: List<Pair<String, String>>,
        callback: (Result<Unit>) -> Unit
    ) {
        firestore.batchWriteDoc(addOperations, updateOperations, deleteOperations) { callbackIos ->
            val error = callbackIos?.error()
            if (error != null) {
                callback(Result.failure(error.convertNSErrorToException()))
            } else {
                callback(Result.success(Unit))
            }
        }

    }


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

    actual fun listenToCollection(
        collection: String,
        callback: (Result<List<Map<String, Any?>>>) -> Unit
    ) {
        stopListenerCollection()
        firestore.startRealTimeListener(collection= collection){callbackIos ->
            val error = callbackIos?.error()
            val data  =callbackIos?.data()

            if(error != null){
                callback(Result.failure(error.convertNSErrorToException()))
                return@startRealTimeListener
            } else{
                callback(Result.success(convertToListOfMaps(data)))
            }
        }
    }

    actual fun stopListenerCollection() {
         firestore.stopRealTimeListener()
    }

}

