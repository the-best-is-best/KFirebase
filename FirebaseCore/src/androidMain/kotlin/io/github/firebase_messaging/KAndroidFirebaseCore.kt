package io.github.firebase_messaging

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseOptions
import com.google.firebase.initialize

object KAndroidFirebaseCore {
    fun initialize(context: Context, options: FirebaseOptions, name: String = "default") {
        Firebase.initialize(context = context, options, name)
    }
}