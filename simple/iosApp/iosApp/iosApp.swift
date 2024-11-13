import UIKit
import UserNotifications
import FirebaseCore
import FirebaseMessaging // Replace this with the actual module name
import ComposeApp

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate , MessagingDelegate {
    
    var window: UIWindow?

    // This function is called when the app starts
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Firebase initialization
        FirebaseApp.configure()
        LocalNotification.shared.doInit(userNotificationCenterDelegate: self)
        KFirebaseMessaging.shared.doInit(messagingDelegate: self)
        
        window = UIWindow(frame: UIScreen.main.bounds)
        if let window = window {
            window.rootViewController = MainKt.MainViewController()
            window.makeKeyAndVisible()
        }
        if let userInfo = launchOptions?[.remoteNotification] as? [String: AnyObject] {
            LocalNotification.shared.notifyNotificationAppOpenClicked(data: userInfo)

        }
            return true
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
}
