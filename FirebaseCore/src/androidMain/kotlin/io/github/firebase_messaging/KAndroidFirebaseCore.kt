package io.github.firebase_messaging

import android.annotation.SuppressLint
import android.app.Activity
import com.google.firebase.Firebase
import com.google.firebase.initialize

@SuppressLint("StaticFieldLeak")
object KAndroidFirebaseCore {
    lateinit var context: Activity
    fun initialize(context: Activity) {
        this.context = context
        Firebase.initialize(context = context)
    }
}