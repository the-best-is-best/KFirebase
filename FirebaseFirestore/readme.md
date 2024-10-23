<h1 align="center">KFirebaseFirestore</h1><br>
<div align="center">
<a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
<a href="https://android-arsenal.com/api?level=21" rel="nofollow">
    <img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" style="max-width: 100%;">
</a>
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android" alt="Badge Android" />
  <img src="https://img.shields.io/badge/iOS-13%2B-blue.svg?logo=apple" alt="iOS 13+ Badge" />

<a href="https://github.com/the-best-is-best/"><img alt="Profile" src="https://img.shields.io/badge/github-%23181717.svg?&style=for-the-badge&logo=github&logoColor=white" height="20"/></a>
</div>

<br>

### KFirebaseFirestore is a Kotlin Multiplatform library designed to streamline the integration of Firebase services in your mobile applications. With this library, developers can effortlessly initialize Firebase for both Android and iOS, enabling a unified and efficient development experience

<hr>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/kfirebase-messaging)](https://central.sonatype.com/artifact/io.github.the-best-is-best/kfirebase-firestore)

KFirebaseFirestore is available on `mavenCentral()`.

## Installation

```kotlin
implementation("io.github.the-best-is-best:kfirebase-firestore:1.0.0-rc1")
```

### androidMain

```kotlin
KAndroidFirebaseCore.initialize(this)
```

## Need add this in pod file if not exist run ` pod init `

```pod
 pod 'kfirebaseFirestore' , '0.1.0-6-rc'
```

### iosApp AppDelegate example

```swift
    FirebaseApp.configure()
```

### How use it

#### Functionality

```kotlin

// commonMain/src/commonMain/kotlin/io/github/firebase_firestore/KFirebaseFirestore.kt
package io.github.firebase_firestore

import kotlinx.coroutines.flow.Flow

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
    fun listenToCollection(
        collection: String,
        listenToCollectionId: String
    ): Flow<Result<List<Map<String, Any?>>>>



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



```

#### How use queries or filters

```kotlin
val filters = listOf(
    mapOf("field" to "age", "operator" to ">=", "value" to 25),
    mapOf("field" to "status", "operator" to "==", "value" to "active"),
    mapOf("field" to "hobbies", "operator" to "array-contains", "value" to "reading")
 mapOf("field" to "hobbies", "operator" to "array-contains", "value" to "reading"), // Array contains
    mapOf(
        "field" to "hobbies", 
        "operator" to "array-contains-any", 
        "value" to listOf("reading", "traveling")                                      // Array contains any
    ),
    mapOf(
        "field" to "age", 
        "operator" to "in", 
        "value" to listOf(25, 30, 35)                                                  // In (set operation)
    ),
    mapOf(
        "field" to "age", 
        "operator" to "not-in", 
        "value" to listOf(40, 45, 50)                                                  // Not in (set operation)
    )
)

````
