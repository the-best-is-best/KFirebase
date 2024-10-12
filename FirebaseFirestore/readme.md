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

    // Real-time listener for a collection - support only one collection
    fun listenToCollection(
        collection: String,
        callback: (Result<List<Map<String, Any?>>>) -> Unit
    )
    // stop collection listener
    fun stopListenerCollection()

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
