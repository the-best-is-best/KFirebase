package io.github.firebase_messaging

import cocoapods.FirebaseCore.FIRApp
import kotlinx.cinterop.ExperimentalForeignApi

object KIosFirebaseCore {
    @OptIn(ExperimentalForeignApi::class)
    fun configure() {
        FIRApp.configure()

    }
}