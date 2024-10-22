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
import io.github.firebase_database.KFirebaseDatabase
import io.github.sample.theme.AppTheme
import kotlinx.coroutines.launch
import kotlin.random.Random


@Composable
internal fun App() = AppTheme {


    val app = KFirebaseCore.app()
    println(app.options) // Check this log
    val db = KFirebaseDatabase()

    val scope = rememberCoroutineScope()

    val books = mutableListOf(
        mapOf(
            "id" to 1,
            "title" to "To Kill a Mockingbird",
            "author" to mapOf(
                "name" to "Harper Lee",
                "bio" to "American novelist",
                "birthYear" to 1926
            ),
            "genre" to "Fiction",
            "publishedYear" to 1960,
            "isbn" to "978-0-06-112008-4",
            "rating" to 4.27,
            "description" to "A novel about the serious issues of rape and racial inequality seen through the eyes of a child."
        ),
        mapOf(
            "id" to 2,
            "title" to "1984",
            "author" to mapOf(
                "name" to "George Orwell",
                "bio" to "English novelist and essayist",
                "birthYear" to 1903
            ),
            "genre" to "Dystopian",
            "publishedYear" to 1949,
            "isbn" to "978-0-452-28423-4",
            "rating" to 4.17,
            "description" to "A novel that presents a dystopian future under a totalitarian regime where surveillance and manipulation are commonplace."
        ),
        mapOf(
            "id" to 3,
            "title" to "The Great Gatsby",
            "author" to mapOf(
                "name" to "F. Scott Fitzgerald",
                "bio" to "American novelist",
                "birthYear" to 1896
            ),
            "genre" to "Fiction",
            "publishedYear" to 1925,
            "isbn" to "978-0-7432-7356-5",
            "rating" to 3.91,
            "description" to "A critique of the American Dream, exploring themes of wealth, love, and the pursuit of happiness in the Jazz Age."
        ),
        mapOf(
            "id" to 4,
            "title" to "Pride and Prejudice",
            "author" to mapOf(
                "name" to "Jane Austen",
                "bio" to "English novelist",
                "birthYear" to 1775
            ),
            "genre" to "Romance",
            "publishedYear" to 1813,
            "isbn" to "978-1-85326-000-1",
            "rating" to 4.26,
            "description" to "A romantic novel that critiques the British landed gentry at the end of the 18th century."
        ),
        mapOf(
            "id" to 5,
            "title" to "The Catcher in the Rye",
            "author" to mapOf(
                "name" to "J.D. Salinger",
                "bio" to "American writer",
                "birthYear" to 1919
            ),
            "genre" to "Fiction",
            "publishedYear" to 1951,
            "isbn" to "978-0-316-76948-0",
            "rating" to 3.81,
            "description" to "A story about teenage angst and alienation, narrated by the iconic character Holden Caulfield."
        )
    )


    val path = "books"




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
                    scope.launch {
                        val res = db.writeList(path, books)
                        res.onSuccess {
                            println("success add books")
                        }
                        res.onFailure {
                            println("add books error $it")
                        }
                    }

                }) {
                Text("Add list dummy book data")
            }
            Spacer(Modifier.height(20.dp))
          ElevatedButton(onClick = {
              scope.launch {
                  val res = db.addObserveListener(path)
                  res.onSuccess {
                      println("new value in listener $it")
                  }
                  res.onFailure {
                      println("new value in listener error $it")
                  }
              }
          }){
              Text("Listener")
          }
            Spacer(Modifier.height(20.dp))
            ElevatedButton(onClick = {
                val newId = Random.nextInt(books.lastIndex, books.lastIndex +1)
                val newBook =  mapOf(
                    "id" to newId,
                    "title" to "The Catcher in the Rye",
                    "author" to mapOf(
                        "name" to "J.D. Salinger",
                        "bio" to "American writer",
                        "birthYear" to 1919
                    ),
                    "genre" to "Fiction",
                    "publishedYear" to 1951,
                    "isbn" to "978-0-316-76948-0",
                    "rating" to 3.81,
                    "description" to "A story about teenage angst and alienation, narrated by the iconic character Holden Caulfield."
                )
                scope.launch {
                    val res = db.write("$path/$newId", newBook)
                    res.onSuccess {
                        println("data write")
                        books.add(newBook)

                    }
                    res.onFailure {
                        println("data failed write")
                    }
                }
            }){

                Text("add value")
            }
        }


    }
}
