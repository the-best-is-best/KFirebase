package io.github.sample

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.firebase_auth.rememberKFirebaseUserStates
import io.github.firebase_core.KFirebaseCore
import io.github.sample.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
internal fun AppAuth() = AppTheme {
    val app = KFirebaseCore.app()
    println(app.options) // Check this log
    val currentUserState = rememberKFirebaseUserStates()
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("User id ${currentUserState.user?.uid}")
            Spacer(Modifier.height(10.dp))
            Text("display name ${currentUserState.user?.displayName}")
            Spacer(Modifier.height(10.dp))
            Text("emil ${currentUserState.user?.email}")

            Spacer(Modifier.height(30.dp))

            ElevatedButton(
                enabled = currentUserState.user == null,
                onClick = {
                    scope.launch {
                        val res = currentUserState.signInWithEmailAndPassword(
                        "eng.michelle.raouf@gmail.com",
                        "Mesho@500"
                        )
                        res.onFailure {
                            println("error auth $it")
                        }
                        res.onFailure {
                            println("auth $it")
                        }
                    }
                }) {
                Text("Login with email and password")
            }
            Spacer(Modifier.height(20.dp))

            ElevatedButton(
                enabled = currentUserState.user == null,
                onClick = {
                    scope.launch {
                        val res = currentUserState.getCurrentUser()

                        res.onFailure {
                            println("error get user $it")
                        }
                        res.onSuccess {
                            println("user is $it")
                        }
                    }
                }) {
                Text("get user data")
            }
            Spacer(Modifier.height(20.dp))
            ElevatedButton(
                enabled = currentUserState.user != null,
                onClick = {
                    scope.launch {
                        val res = currentUserState.updateProfile("Michelle", null)
                        res.onFailure {
                            println("error update profile $it")
                        }
                        res.onSuccess {
                            println("update profile $it")
                        }
                    }

                }) {
                Text("Update profile")
            }
            Spacer(Modifier.height(20.dp))
            ElevatedButton(
                enabled = currentUserState.user != null,
                onClick = {
                    scope.launch {
                        val res = currentUserState.updateEmail("meshoraouf515@gamil.com")
                        res.onFailure {
                            println("update email error $it")
                        }
                        res.onSuccess {
                            println("update email $it")
                        }
                    }
                }) {
                Text("Update Email")
            }
            Spacer(Modifier.height(20.dp))
            ElevatedButton(
                enabled = currentUserState.user != null,
                onClick = {
                    scope.launch {
                        val res = currentUserState.delete()
                        res.onFailure {
                            println("delete user $it")
                        }
                        res.onSuccess {
                            println("user deleted")
                        }
                    }
                }) {
                Text("delete user")
            }
            Spacer(Modifier.height(20.dp))
            ElevatedButton(
                enabled = currentUserState.user != null,
                onClick = {
                    scope.launch {
                        val res = currentUserState.signOut()
                        res.onFailure {
                            println("logout user $it")
                        }
                    }
                }) {
                Text("logout user")
            }
        }


    }
}
