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
import io.github.firebase_core.KFirebaseCore
import io.github.firebase_firestore.KFirebaseFirestore
import io.github.sample.theme.AppTheme
import kotlinx.coroutines.launch


@Composable
internal fun AppFirestore() = AppTheme {


    val app = KFirebaseCore.app()
    println(app.options) // Check this log
    val db = KFirebaseFirestore()
    val scope = rememberCoroutineScope()
    val users = listOf(
        mapOf(
            "id" to 1,
            "name" to "John",
            "age" to 30,
            "rating" to 4.8,
            "hobbies" to listOf("reading", "swimming"),
            "status" to "active"
        ),
        mapOf(
            "id" to 2,
            "name" to "Alice",
            "age" to 25,
            "rating" to 4.5,
            "hobbies" to listOf("painting", "running"),
            "status" to "pending"
        ),
        mapOf(
            "id" to 3,
            "name" to "Bob",
            "age" to 35,
            "rating" to 4.9,
            "hobbies" to listOf("cycling", "hiking"),
            "status" to "inactive"
        ),
        mapOf(
            "id" to 4,
            "name" to "Eve",
            "age" to 22,
            "rating" to 4.2,
            "hobbies" to listOf("dancing", "gaming"),
            "status" to "active"
        ),
        mapOf(
            "id" to 5,
            "name" to "Charlie",
            "age" to 28,
            "rating" to 4.0,
            "hobbies" to listOf("photography", "reading"),
            "status" to "active"
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
                    users.forEach {
                        scope.launch {
                            val res = db.addDocument(
                                collection,
                                it["id"].toString(),
                                it
                            )
                            res.onFailure {
                                println("error add doc $it")
                            }
                            res.onSuccess {
                                println("doc added")
                            }
                        }

                    }
                }) {
                Text("Add list dummy users data")
            }
            Spacer(Modifier.height(20.dp))

            ElevatedButton(

                onClick = {
                    scope.launch {
                        val res = db.getDocuments(collection)
                        res.onSuccess {
                            println("get list of data is $it")
                        }
                        res.onFailure {
                            println("error get list data $it")
                        }

                    }
                }) {
                Text("get users data")
            }
            Spacer(Modifier.height(20.dp))

            ElevatedButton(

                onClick = {
                    val filters = listOf(
                        mapOf("field" to "age", "operator" to ">=", "value" to 25),
                        mapOf("field" to "status", "operator" to "==", "value" to "active"),
                        mapOf(
                            "field" to "hobbies",
                            "operator" to "array-contains",
                            "value" to "reading"
                        )
                    )
                    scope.launch {
                        val res = db.queryDocuments(collection, filters, orderBy = "age")
                        res.onSuccess {
                            println("data is ${it}")
                        }

                    }

                }) {
                Text("Example filters   ")
            }


            Spacer(Modifier.height(20.dp))
            ElevatedButton(

                onClick = {
                    scope.launch {
                        val res = db.getDocumentById(collection, users.last()["id"].toString())
                        res.onFailure {
                            println("error get data $it")
                        }
                        res.onSuccess {
                            println("doc data $it")
                        }
                    }

                }) {
                Text("get user data")
            }
            Spacer(Modifier.height(20.dp))
            ElevatedButton(
                onClick = {
                    scope.launch {
                        val res = db.deleteDocument(collection, users.last()["id"].toString())
                        res.onFailure {
                            println("delete data error $it")
                        }
                        res.onSuccess {
                            println("data deleted")
                        }
                    }
                }) {
                Text("delete data")
            }
        }


    }
}
