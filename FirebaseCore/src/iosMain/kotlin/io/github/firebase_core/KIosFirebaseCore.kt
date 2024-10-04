package io.github.firebase_core

import cocoapods.FirebaseCore.FIRApp
import cocoapods.FirebaseCore.FIROptions
import kotlinx.cinterop.ExperimentalForeignApi

object KIosFirebaseCore {
    @OptIn(ExperimentalForeignApi::class)
    fun configure(name: String = "default", options: FIROptions) {
        FIRApp.configureWithName(name, options)

    }
}