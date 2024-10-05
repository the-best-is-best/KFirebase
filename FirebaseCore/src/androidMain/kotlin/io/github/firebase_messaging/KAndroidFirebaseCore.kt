package io.github.firebase_messaging

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.initialize

object KAndroidFirebaseCore {
    fun initialize(context: Context) {
        Firebase.initialize(context = context)
    }
}