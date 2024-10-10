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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.firebase_core.KFirebaseCore
import io.github.firebase_firestore.KFirebaseFirestore
import io.github.sample.theme.AppTheme

data class User(
    val uid: String?,
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoURL: String?,
    val isAnonymous: Boolean?,
    val isEmailVerified: Boolean?,
    val metaData: UserMetaData?
) {
    fun toMap(): Map<String, Any?> {
        // Manually create a map from the User properties
        return mapOf(
            "uid" to uid,
            "displayName" to displayName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "photoURL" to photoURL,
            "isAnonymous" to isAnonymous,
            "isEmailVerified" to isEmailVerified,
            "metaData" to metaData?.toMap() // Call toMap on metaData if it is not null
        )
    }
}

data class UserMetaData(
    val creationTime: Double?,
    val lastSignInTime: Double?
) {
    // Helper function to convert UserMetaData to a Map
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "creationTime" to creationTime,
            "lastSignInTime" to lastSignInTime
        )
    }
}


@Composable
internal fun App() = AppTheme {


    val app = KFirebaseCore.app()
    println(app.options) // Check this log
    val db = KFirebaseFirestore()
    val user = User(
        uid = "12345",
        displayName = "John Doe",
        email = "john.doe@example.com",
        phoneNumber = "+123456789",
        photoURL = "http://example.com/photo.jpg",
        isAnonymous = false,
        isEmailVerified = true,
        metaData = UserMetaData(
            creationTime = 1633072800.0,
            lastSignInTime = 1633169200.0
        )
    )
    val collection = "users"




    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {


            ElevatedButton(
                onClick = {
                    db.addDocument(
                        collection,
                        user.uid.toString(),
                        user.toMap()
                    ) {
                        it.onFailure {
                            println("error add doc $it")
                        }
                    }
                }) {
                Text("Add docs")
            }
            Spacer(Modifier.height(20.dp))

            ElevatedButton(

                onClick = {
                    db.getDocuments(collection) {
                        it.onSuccess {
                            println("get list of data is $it")
                        }
                        it.onFailure {
                            println("error get list data $it")
                        }
                    }
                }) {
                Text("get users data")
            }


            Spacer(Modifier.height(20.dp))
            ElevatedButton(

                onClick = {
                    db.getDocumentById(collection, user.uid.toString()) {
                        it.onFailure {
                            println("error get data $it")
                        }
                        it.onSuccess {
                            println("doc data $it")
                        }
                    }
                }) {
                Text("get user data")
            }
            Spacer(Modifier.height(20.dp))
            ElevatedButton(
                onClick = {
                    db.deleteDocument(collection, user.uid.toString()) {
                        it.onFailure {
                            println("delete data error $it")
                        }
                    }
                }) {
                Text("delete data")
            }
        }


    }
}
