<h1 align="center">KFirebaseDatabase</h1><br>
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

### KFirebaseDatabase is a Kotlin Multiplatform library designed to streamline the integration of Firebase services in your mobile applications. With this library, developers can effortlessly initialize Firebase for both Android and iOS, enabling a unified and efficient development experience

<hr>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/kfirebase-core)](https://central.sonatype.com/artifact/io.github.the-best-is-best/kfirebase-core)

KFirebaseDatabase is available on `mavenCentral()`.

## Installation

```kotlin
implementation("io.github.the-best-is-best:kfirebase-database:1.0.0-rc1")
```

### androidMain

```kotlin
KAndroidFirebaseCore.initialize(this)
```

## Need add this in pod file if not exist run ` pod init `

```pod
 pod 'FirebaseDatabase' , '11.3.0'
```

### iosApp

```ios
 FirebaseApp.configure()
```

### How use it

#### Functionality

```kotlin

package io.github.firebase_database

expect class KFirebaseDatabase() {
    fun write(path: String, data: Map<String, Any>, callback: (Result<Boolean?>) -> Unit)
    fun read(path: String, callback: (Result<Any?>) -> Unit)
    fun writeList(path: String, dataList: List<Map<String, Any>>, callback: (Result<Boolean>) -> Unit)
    fun readList(path: String, callback: (Result<List<Any?>>) -> Unit)
    fun delete(path: String, callback: (Result<Boolean?>) -> Unit)
    fun update(path: String, data: Map<String, Any>, callback: (Result<Boolean?>) -> Unit)

    // Listen for real-time changes at the specified path
    fun addObserveListener(path: String, callback: (Result<Any?>) -> Unit)

    // Remove all listeners for the specified path
    fun removeObserver(path: String)
}

```

### Note

- path like `books/$id`
