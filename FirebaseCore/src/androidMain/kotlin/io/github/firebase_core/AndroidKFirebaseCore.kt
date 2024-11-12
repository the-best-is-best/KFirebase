package io.github.firebase_core

import android.annotation.SuppressLint
import android.app.Activity
import com.google.firebase.Firebase
import com.google.firebase.initialize

@SuppressLint("StaticFieldLeak")
object AndroidKFirebaseCore {

    fun initialize(activity: Activity) {
        Firebase.initialize(context = activity)
    }
}