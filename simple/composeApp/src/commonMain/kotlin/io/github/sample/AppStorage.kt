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
import io.github.firebase_storage.KFirebaseStorage
import io.github.sample.theme.AppTheme
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.launch


@Composable
internal fun AppStorage() = AppTheme {


    val app = KFirebaseCore.app()
    println(app.options) // Check this log
    val storage = KFirebaseStorage()

    var imageUploadedPath: String? = null

    val path = "images/"

    var selectedFile: PlatformFile? = null
    val scope = rememberCoroutineScope()
// FileKit Compose
    val launcher = rememberFilePickerLauncher(
        type = PickerType.ImageAndVideo,
        mode = PickerMode.Single,
        title = "Pick a media",
        initialDirectory = "/custom/initial/path"
    ) { file ->
        selectedFile = file
        print("file selected")
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {

            ElevatedButton(onClick = {
                launcher.launch()

            }) {
                Text("Select image")
            }
            ElevatedButton(
                onClick = {
                    if (selectedFile != null) {
                        scope.launch {
                            val bytes = selectedFile!!.readBytes()
                            val res = storage.uploadFile("$path/${selectedFile!!.name}", bytes)
                            res.onSuccess { (url, filePath) ->
                                imageUploadedPath = filePath
                            }


                        }
                    }


                }) {
                Text("Upload image")
            }
            Spacer(Modifier.height(20.dp))

            ElevatedButton(

                onClick = {
                    if (imageUploadedPath != null) {
                        scope.launch {
                            val res = storage.downloadFile(imageUploadedPath!!)
                            res.onSuccess {
                                println("Size downloaded ${it?.fileBytes?.size}")
                                val supported = FileKit.isSaveFileWithoutBytesSupported()
                                println("support save $supported")
                                if (supported && it != null) {
                                    val fileSaved = FileKit.saveFile(
                                        it.fileBytes,
                                        extension = it.fileExtension,
                                        baseName = it.fileName
                                    )
                                    println("file saved path is ${fileSaved?.path}")
                                }
                            }
                        }


                    }
                }) {
                Text("Download image")
            }
            Spacer(Modifier.height(20.dp))

            ElevatedButton(
                onClick = {
                    if (imageUploadedPath != null) {
                        scope.launch {
                            val res = storage.deleteFile(imageUploadedPath!!)
                            res.onSuccess {
                                println("deleted file")
                                imageUploadedPath = null
                            }
                            res.onFailure {
                                println("error delete $it")

                            }
                        }


                    }
                }) {
                Text("delete image")
            }

        }


    }
}
