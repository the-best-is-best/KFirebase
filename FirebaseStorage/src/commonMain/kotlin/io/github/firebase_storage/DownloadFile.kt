package io.github.firebase_storage

data class KFirebaseStorageDownloadedFile(
    val fileName: String,
    val fileExtension: String,
    val fileBytes: ByteArray
)