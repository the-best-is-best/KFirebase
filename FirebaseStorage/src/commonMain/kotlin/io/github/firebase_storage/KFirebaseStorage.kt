package io.github.firebase_storage

expect class KFirebaseStorage() {
    fun uploadFile(
        filePath: String,
        fileData: ByteArray,
        callback: (Result<Pair<String?, String>>) -> Unit
    )

    fun downloadFile(filePath: String, callback: (Result<KFirebaseStorageDownloadedFile?>) -> Unit)
    fun deleteFile(filePath: String, callback: (Result<Unit>) -> Unit)
}
