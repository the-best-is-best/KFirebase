<h1 align="center">KFirebaseCore</h1><br>
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

### KFirebaseCore is a Kotlin Multiplatform library designed to streamline the integration of Firebase services in your mobile applications. With this library, developers can effortlessly initialize Firebase for both Android and iOS, enabling a unified and efficient development experience

<hr>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/kfirebase-core)](https://central.sonatype.com/artifact/io.github.the-best-is-best/kfirebase-core)

KFirebaseCore is available on `mavenCentral()`.

## Installation

```kotlin
implementation("io.github.the-best-is-best:kfirebase-core:1.0.0-rc1")
```

### androidMain

```kotlin
AndroidKFirebaseCore.initialize(this)
```

## Need add this in pod file if not exist run ` pod init `

```pod
 pod 'FirebaseCore' , '11.3.0'
```

### iosApp

```ios
 FirebaseApp.configure()
```

### How use it

```kotlin
 val app = KFirebaseCore.app()
 println(app.name)
 println(app.options)
```
