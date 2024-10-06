<h1 align="center">KFirebaseMessaging</h1><br>
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

### KFirebaseMessaging is a Kotlin Multiplatform Mobile (KMM) package that simplifies the integration of Firebase Cloud Messaging (FCM) across Android and iOS platforms. It provides a unified API for handling push notifications and FCM messaging in a shared codebase, allowing developers to seamlessly implement FCM functionality for both platforms without duplicating code

<hr>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/kfirebase-messaging)](https://central.sonatype.com/artifact/io.github.the-best-is-best/kfirebase-messaging)

KFirebaseMessaging is available on `mavenCentral()`.

## Installation

```kotlin
implementation("io.github.the-best-is-best:kfirebase-messaging:1.0.0-rc2")
```

### androidMain

```kotlin

KAndroidFirebaseCore.initialize(this)
AndroidKFirebaseMessagingChannel(this).initChannel(
    "fcm",  // id
    "fcm notification", // name
    R.drawable.ic_notification // icon
)
  
      //this for get data fcm when app reopen
      val dataBundle = intent.extras
      if (dataBundle != null) {
          KFirebaseMessagingImpl.notifyNotificationBackgroundClicked(dataBundle)
      }
        // already added
  setContent { App() }
  }
  
  // for get data fcm in app background
  override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val dataBundle = intent.extras
        if (dataBundle != null) {
            KFirebaseMessagingImpl.notifyNotificationBackgroundClicked(dataBundle)
        }
    }
  
```

## Android Mainifist

```xml
       <meta-data
            android:name="com.google.firebase.messaging.background_enabled"
            android:value="true" />
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_icon"
            android:resource="@drawable/ic_notification" />

        <meta-data
           android:name="com.google.firebase.messaging.default_notification_channel_id"
            android:value="@string/default_notification_channel_id" />
        <meta-data
  android:name="com.google.firebase.messaging.default_notification_channel_name"
            android:value="@string/default_notification_channel_name" />
         // optional
  <meta-data
  android:name="com.google.firebase.messaging.default_notification_color"     android:resource="@color/custom_color" />

```

## Need add this in pod file if not exist run ` pod init `

```pod
 pod 'KFirebaseMessaging' , '0.1.0-rc.1'
```

### iosApp AppDelegate example

```swift
import ComposeApp
import Firebase
import KFirebaseMessaging  
import UIKit
import UserNotifications

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate,
  MessagingDelegate
{

  var window: UIWindow?

  // This function is called when the app starts
  func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    // Firebase initialization
    FirebaseApp.configure()

    // Initialize Firebase messaging delegate and notification center delegate
    let firebaseMessaging = KFirebaseMessaging.shared
    firebaseMessaging.initDelegate(notificationDelegate: self, messagesDelegate: self)
    firebaseMessaging.requestAuthorization()
    // Request notifications permissions

    window = UIWindow(frame: UIScreen.main.bounds)
    if let window = window {
      window.rootViewController = MainKt.MainViewController()
      window.makeKeyAndVisible()
    }
    if let remoteNotification = launchOptions?[.remoteNotification] as? [String: AnyObject] {

      KFirebaseMessaging.shared.notifyMessagingClicked(remoteNotification)
    }
    return true
  }

  // Register for remote notifications and handle device token registration
  func application(
    _ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
  ) {
    KFirebaseMessaging.shared.application(
      application, didRegisterForRemoteNotificationsWithDeviceToken: deviceToken)
  }

  // Handle failure to register for remote notifications
  func application(
    _ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error
  ) {
    KFirebaseMessaging.shared.application(
      application, didFailToRegisterForRemoteNotificationsWithError: error)
  }

  // Handle notification when the app is in the foreground
  func userNotificationCenter(
    _ center: UNUserNotificationCenter, willPresent notification: UNNotification,
    withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
  ) {
    KFirebaseMessaging.shared.userNotificationCenter(
      center, willPresent: notification, withCompletionHandler: completionHandler)
  }

  // Handle notification when the user interacts with it (taps on the notification)
  func userNotificationCenter(
    _ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse,
    withCompletionHandler completionHandler: @escaping () -> Void
  ) {
    KFirebaseMessaging.shared.userNotificationCenter(
      center, didReceive: response, withCompletionHandler: completionHandler)
  }

  // Firebase Messaging delegate method for receiving FCM token
  func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
    KFirebaseMessaging.shared.messaging(messaging, didReceiveRegistrationToken: fcmToken)
  }
}

```

### How use it

```kotlin
// this lines add it in compose func direct
     val fcm = KFirebaseMessaging.create()
   fcm.setNotificationClickedListener { it ->
    it.onSuccess { data ->
        println("Notification clicked data: $data")
        notificationValue = "Notification clicked data: ${data?.get("token").toString()}"
    }
}

fcm.setNotificationListener { it ->
    it.onSuccess { data ->
        println("Notification received data: $data")
        notificationValue = "Notification received data: ${data?.get("token").toString()}"
    }
}

fcm.setTokenListener { it -> it.onSuccess { token -> println("User token: $token") } }
```

<br>

### For request permission for android `Note`  ios request added in app delegate

```kotlin
    fcm.requestAuthorization(callback = {
          println("per state $it")
    })
```

### For get token fcm

```kotlin
    fcm.getToken {
      it.onSuccess {
          println("token $it")
      }
      it.onFailure {
          println("error token $it")
      }
  }
```

### For subscribe topic

```kotlin
fcm.subscribeTopic("topic_test", callback = {
      it.onSuccess {
          println("sub to topic correctly")
      }
      it.onFailure {
          println("sub to topic ${it.message}")
      }
  })
```

### For un subscribe topic

```kotlin
    fcm.unsubscribeTopic("topic_test", callback = {
        it.onSuccess {
            println("un sub to topic correctly")
        }
        it.onFailure {
            println("un sub to topic ${it.message}")
        }
    })
```
