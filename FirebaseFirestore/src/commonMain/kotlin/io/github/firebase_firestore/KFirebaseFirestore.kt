// commonMain/src/commonMain/kotlin/io/github/firebase_firestore/KFirebaseFirestore.kt
package io.github.firebase_firestore

expect class KFirebaseFirestore() {

    // Add a custom object to the collection
    fun addDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>,
        callback: (Result<Unit>) -> Unit
    )

    // Get a list of custom objects from a collection
    fun getDocuments(
        collection: String,
        callback: (Result<List<Map<String, Any?>>>) -> Unit
    )

    // Get a specific document by its ID
    fun getDocumentById(
        collection: String,
        documentId: String,
        callback: (Result<Map<String, Any?>>) -> Unit
    )
     fun listenToCollection(
        collection: String,
        listenerId: String,
        callback: (Result<List<Map<String, Any?>>>) -> Unit
    )
  // stop collection listener
  fun stopListenerCollection(listenerId: String)

    fun stopAllListeners()

    // Query documents with filters, sorting, and limits
    fun queryDocuments(
        collection: String,
        filters: List<Map<String, Comparable<*>>> = emptyList(),
        orderBy: String? = null,
        limit: Long? = null,
        callback: (Result<List<Map<String, Any?>>>) -> Unit
    )

    // Update a document by its ID
    fun updateDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>,
        callback: (Result<Unit>) -> Unit
    )

    // Delete a document by its ID
    fun deleteDocument(
        collection: String,
        documentId: String,
        callback: (Result<Unit>) -> Unit
    )

    // Batch writes: adding, updating, or deleting multiple documents in a single operation
    fun batchWrite(
        addOperations: List<Pair<String, Any>>, // collection and data
        updateOperations: List<Triple<String, String, Any>>, // collection, documentId, data
        deleteOperations: List<Pair<String, String>>, // collection and documentId
        callback: (Result<Unit>) -> Unit
    )


}

