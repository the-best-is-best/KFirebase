package io.github.firebase_firestore

expect class KFirebaseFirestore() {

    // Add a custom object to the collection
    suspend fun addDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>
    ): Result<Boolean>

    // Get a list of custom objects from a collection
    suspend fun getDocuments(
        collection: String
    ): Result<List<Map<String, Any?>>>

    // Get a specific document by its ID
    suspend fun getDocumentById(
        collection: String,
        documentId: String
    ): Result<Map<String, Any?>>

    // Listen to a collection for real-time updates
    suspend fun listenToCollection(
        collection: String,
        listenToCollectionId: String
    ): Result<List<Map<String, Any?>>>

    // Stop collection listener
    fun stopListenerCollection(listenerId: String)

    // Stop all listeners
    fun stopAllListeners()

    // Query documents with filters, sorting, and limits
    suspend fun queryDocuments(
        collection: String,
        filters: List<Map<String, Comparable<*>>> = emptyList(),
        orderBy: String? = null,
        limit: Long? = null
    ): Result<List<Map<String, Any?>>>

    // Update a document by its ID
    suspend fun updateDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any?>
    ): Result<Boolean>

    // Delete a document by its ID
    suspend fun deleteDocument(
        collection: String,
        documentId: String
    ): Result<Unit>

    // Batch writes: adding, updating, or deleting multiple documents in a single operation
    suspend fun batchWrite(
        addOperations: List<Pair<String, Any>>, // collection and data
        updateOperations: List<Triple<String, String, Any>>, // collection, documentId, data
        deleteOperations: List<Pair<String, String>> // collection and documentId
    ): Result<Unit>
}
