import UIKit
import UserNotifications
import Firebase
import KFirebaseMessaging // Replace this with the actual module name
import ComposeApp

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    
    var window: UIWindow?

    // This function is called when the app starts
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
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
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        KFirebaseMessaging.shared.application(application, didRegisterForRemoteNotificationsWithDeviceToken: deviceToken)
    }

    // Handle failure to register for remote notifications
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        KFirebaseMessaging.shared.application(application, didFailToRegisterForRemoteNotificationsWithError: error)
    }

    // Handle notification when the app is in the foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        KFirebaseMessaging.shared.userNotificationCenter(center, willPresent: notification, withCompletionHandler: completionHandler)
    }

    // Handle notification when the user interacts with it (taps on the notification)
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        KFirebaseMessaging.shared.userNotificationCenter(center, didReceive: response, withCompletionHandler: completionHandler)
    }

    // Firebase Messaging delegate method for receiving FCM token
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        KFirebaseMessaging.shared.messaging(messaging, didReceiveRegistrationToken: fcmToken)
    }
}
