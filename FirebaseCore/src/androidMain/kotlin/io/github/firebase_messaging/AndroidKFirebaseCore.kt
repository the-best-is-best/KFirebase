package io.github.firebase_messaging

import android.annotation.SuppressLint
import android.app.Activity
import com.google.firebase.Firebase
import com.google.firebase.initialize
import io.github.kpermissions.handler.PermissionHandler

@SuppressLint("StaticFieldLeak")
object AndroidKFirebaseCore {
    lateinit var context: Activity
    fun initialize(context: Activity) {
        PermissionHandler.init(context)


        this.context = context
        Firebase.initialize(context = context)
    }
}