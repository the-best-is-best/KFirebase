package io.github.firebase_storage

expect class KFirebaseStorage() {
    suspend fun uploadFile(
        filePath: String,
        fileData: ByteArray,

        ): Result<Pair<String?, String>>

    suspend fun downloadFile(filePath: String): Result<KFirebaseStorageDownloadedFile?>
    suspend fun deleteFile(filePath: String): Result<Boolean>
}
