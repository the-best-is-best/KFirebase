package io.github.firebase_storage

import com.google.firebase.storage.FirebaseStorage

actual class KFirebaseStorage {
    private val storage = FirebaseStorage.getInstance()

    actual fun uploadFile(
        filePath: String,
        fileData: ByteArray,
        callback: (Result<Pair<String?, String>>) -> Unit // Returning file URL and file path
    ) {
        val storageRef = storage.reference.child(filePath)
        storageRef.putBytes(fileData)
            .addOnSuccessListener {
                storageRef.downloadUrl
                    .addOnSuccessListener { url ->
                        callback(
                            Result.success(
                                Pair(
                                    url.toString(),
                                    filePath
                                )
                            )
                        ) // Returning URL and path
                    }
                    .addOnFailureListener { exception ->
                        callback(Result.failure(exception))
                    }
            }
            .addOnFailureListener { exception ->
                callback(Result.failure(exception))
            }
    }


    actual fun downloadFile(
        filePath: String,
        callback: (Result<KFirebaseStorageDownloadedFile?>) -> Unit
    ) {
        val storageRef = storage.reference.child(filePath)

        storageRef.getBytes(Long.MAX_VALUE)
            .addOnSuccessListener { fileBytes ->
                val fileName = filePath.substringAfterLast('/')
                val fileExtension = fileName.substringAfterLast('.', "")

                val downloadedFile = KFirebaseStorageDownloadedFile(
                    fileName = fileName,
                    fileExtension = fileExtension,
                    fileBytes = fileBytes
                )
                callback(Result.success(downloadedFile))
            }
            .addOnFailureListener { exception ->
                callback(Result.failure(exception))
            }
    }


    actual fun deleteFile(
        filePath: String,
        callback: (Result<Unit>) -> Unit
    ) {
        val storageRef = storage.reference.child(filePath)

        storageRef.delete().addOnSuccessListener {
            callback(Result.success(Unit))
        }.addOnFailureListener { exception ->
            callback(Result.failure(exception))
        }
    }

}