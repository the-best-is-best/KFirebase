# KFirebase

KFirebase is a suite of Kotlin Multiplatform libraries that provide a simple and idiomatic way to use Firebase services in your KMM projects.

<br>

## Recommended Versions

- **KFirebase Packages Version:** 2.1.2
- **Firebase iOS SDK Version:** 12.8.0

## Modules

<table>
  <tr>
    <th style="text-align:center;">Name packages</th>
    <th style="text-align:center;">Status</th>
    <th style="text-align:center;">Details</th>
  </tr>
  <tr>
    <td>Firebase Core</td>
    <td style="text-align:center;"><img src="https://img.shields.io/badge/%20✓-green?style=flat-square&color=276221" alt="✓"></td>
    <td style="text-align:center;"><a href="https://github.com/the-best-is-best/KFirebaseCore/blob/main/README.MD">Click me</a></td>
  </tr>
  <tr>
    <td>Firebase Auth</td>
    <td style="text-align:center;"><img src="https://img.shields.io/badge/%20✓-green?style=flat-square&color=276221" alt="✓"></td>
    <td style="text-align:center;"><a href="https://github.com/the-best-is-best/KFirebaseAuth/blob/main/README.MD">Click me</a></td>
  </tr>
  <tr>
    <td>Firebase Firestore</td>
    <td style="text-align:center;"><img src="https://img.shields.io/badge/%20✓-yellow?style=flat-square&color=FFD700" alt="✓"></td>
    <td style="text-align:center;"><a href="https://github.com/the-best-is-best/kmmFirebaseFireStore/blob/main/README.MD">Click me</a></td>
  </tr>
  <tr>
    <td>Firebase Storage</td>
    <td style="text-align:center;"><img src="https://img.shields.io/badge/%20✓-green?style=flat-square&color=276221" alt="✓"></td>
    <td style="text-align:center;"><a href="https://github.com/the-best-is-best/KFirebaseStorage/blob/main/README.MD">Click me</a></td>
  </tr>
  <tr>
    <td>Firebase Analytics</td>
    <td style="text-align:center;"><img src="https://img.shields.io/badge/%20✓-green?style=flat-square&color=276221" alt="✓"></td>
    <td style="text-align:center;"><a href="https://github.com/the-best-is-best/KFirebaseAnalytics/blob/main/README.MD">Click me</a></td>
  </tr>
  <tr>
    <td>Firebase Messaging</td>
    <td style="text-align:center;"><img src="https://img.shields.io/badge/%20✓-green?style=flat-square&color=276221" alt="✓"></td>
    <td style="text-align:center;"><a href="https://github.com/the-best-is-best/KFirebaseMessaging/blob/main/README.md">Click me</a></td>
  </tr>
  <tr>
    <td>Firebase Crashlytics</td>
    <td style="text-align:center;"><img src="https://img.shields.io/badge/%20✓-green?style=flat-square&color=276221" alt="✓"></td>
    <td style="text-align:center;"><a href="https://github.com/the-best-is-best/KFirebaseCrashlytics/blob/main/README.MD">Click me</a></td>
  </tr>
  <tr>
    <td>Firebase Remote Config</td>
    <td style="text-align:center;"><img src="https://img.shields.io/badge/%20✓-green?style=flat-square&color=276221" alt="✓"></td>
    <td style="text-align:center;"><a href="https://github.com/the-best-is-best/KFirebaseRemoteConfig/blob/main/README.MD">Click me</a></td>
  </tr>
  <tr>
    <td>Firebase Realtime Database</td>
    <td style="text-align:center;"><img src="https://img.shields.io/badge/%20x-red?style=flat-square&color=C30010" alt="x"></td>
    <td style="text-align:center;">Not yet implemented</td>
  </tr>
</table>

## How to use
To use a KFirebase package, add the dependency to your `build.gradle.kts` file in your shared module. For example, to use `kfirebase-core`:
'''kotlin
implementation("io.github.the-best-is-best:kfirebase-core:2.1.2")
'''

For iOS, you need to add the Firebase iOS SDK to your Xcode project. The recommended version is `12.8.0`. You can add the Swift Package from the following URL:
'''
https://github.com/firebase/firebase-ios-sdk.git
'''
And select the required packages.

For more details, please refer to the README file of each module.
