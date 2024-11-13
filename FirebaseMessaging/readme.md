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
<br>

- Note v 1.0.2 dependent on <a href="https://github.com/the-best-is-best/KLocalNotification"> KLocalNotification </a>

<br>

- Note add permissions notification needed android and ios
- Note Not dependent on KFirebaseMessaging <a href="https://github.com/the-best-is-best/KFirebaseMessaging"> KFirebaseMessaging </a>

<br>

## Installation

```kotlin
implementation("io.github.the-best-is-best:kfirebase-messaging:1.0.2")
implementation("io.github.the-best-is-best:klocal-notification:1.0.0-3-rc")
```

### First in gradle

```gradle
  listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export("io.github.the-best-is-best:klocal-notification")  // Export KLocalNotification so it's available in the framework
            export("kfirebase-messaging")
        }
    }
    ...
    iosMain.dependencies {
          ...
            api("io.github.the-best-is-best:klocal-notification")
            api("kfirebase-messaging")
        
        }
```

### androidMain

```kotlin

KAndroidFirebaseCore.initialize(this)
AndroidKFirebaseMessagingChannel.initialization(this)

AndroidKMessagingChannel.initialization(this)
 AndroidKFirebaseMessagingChannel().initChannel(
            "fcm",
            "fcm notification",
            "ic_notification"
        )
  
      //this for get data fcm when app reopen
     
        // already added
  setContent { App() }
   val data = intent.getStringExtra("data")
    if (data != null) {
        LocalNotification.notifyNotificationClickedListener(data)
    }

  
  // for get data fcm in app background
   override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val dataBundle = intent.extras
        if (dataBundle != null) {
                KFirebaseMessaging.notifyNotificationBackgroundClicked(dataBundle)

        }
        // IF you will create local notification
        val data = intent.getStringExtra("data")
        if (data != null) {
            LocalNotification.notifyNotificationClickedListener(data)
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
    <!-- optional -->
  <meta-data
  android:name="com.google.firebase.messaging.default_notification_color"     android:resource="@color/custom_color" />

```

## Need add this in pod file if not exist run ` pod init `

```pod
pod "FirebaseMessaging" , "11.3.0"
```

### iosApp AppDelegate example

```objectivec
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

    LocalNotification.shared.doInit(userNotificationCenterDelegate: self)
        KFirebaseMessaging.shared.doInit(messagingDelegate: self)

    window = UIWindow(frame: UIScreen.main.bounds)
    if let window = window {
      window.rootViewController = MainKt.MainViewController()
      window.makeKeyAndVisible()
    }
      // this is the same in logic in local notification
     i if let userInfo = launchOptions?[.remoteNotification] as? [String: AnyObject] {
            LocalNotification.shared.notifyNotificationAppOpenClicked(data: userInfo)

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
    // Register for remote notifications and handle device token registration
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print("APNS Token: \(deviceToken)")
        Messaging.messaging().apnsToken = deviceToken
    }

    // Handle failure to register for remote notifications
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register for remote notifications: \(error.localizedDescription)")
    }

    // Handle notification when the app is in the foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        let userInfo = notification.request.content.userInfo
        LocalNotification.shared.notifyNotificationReceived(data: userInfo)
        completionHandler([.alert, .sound, .badge]) // Show notification in the foreground
    }

    // Handle notification when the user interacts with it (taps on the notification)
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
             LocalNotification.shared.notifyNotificationClicked(data: userInfo)
            completionHandler()
    }

    // Firebase Messaging delegate method for receiving FCM token
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        KFirebaseMessaging.shared.notifyTokenListener(token: fcmToken)
    }
```

### How use it

```kotlin
// this lines add it in  App()

    LocalNotification.setNotificationClickedListener {
        println("Notification clicked data: $it")
      

    }
    LocalNotification.setNotificationReceivedListener {
        println("Notification received data: $it")
        
    }
    fcm.setTokenListener {
        println("User token: $it")

    }

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
scope.launch {
    fcm.unsubscribeTopic("topic_test", callback = {
        it.onSuccess {
            println("un sub to topic correctly")
        }
        it.onFailure {
            println("un sub to topic ${it.message}")
        }
    })
}
```
