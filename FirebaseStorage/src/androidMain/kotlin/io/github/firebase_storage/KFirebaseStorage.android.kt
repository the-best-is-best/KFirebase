package io.github.firebase_storage

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class KFirebaseStorage {
    private val storage = FirebaseStorage.getInstance()

    actual suspend fun uploadFile(
        filePath: String,
        fileData: ByteArray,
        // Returning file URL and file path
    ): Result<Pair<String?, String>> {
        return suspendCancellableCoroutine { cont ->

            val storageRef = storage.reference.child(filePath)
            storageRef.putBytes(fileData)
                .addOnSuccessListener {
                    storageRef.downloadUrl
                        .addOnSuccessListener { url ->
                            cont.resume(
                                Result.success(
                                    Pair(
                                        url.toString(),
                                        filePath
                                    )
                                )
                            ) // Returning URL and path
                        }
                        .addOnFailureListener { exception ->
                            cont.resume(Result.failure(exception))
                        }
                }
                .addOnFailureListener { exception ->
                    cont.resume(Result.failure(exception))
                }
        }
    }


    actual suspend fun downloadFile(
        filePath: String,

        ): Result<KFirebaseStorageDownloadedFile?> {
        return suspendCancellableCoroutine { cont ->

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
                    cont.resume(Result.success(downloadedFile))
                }
                .addOnFailureListener { exception ->
                    cont.resume(Result.failure(exception))
                }

        }
    }


    actual suspend fun deleteFile(
        filePath: String

    ): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            val storageRef = storage.reference.child(filePath)

            storageRef.delete().addOnSuccessListener {
                cont.resume(Result.success(true))
            }.addOnFailureListener { exception ->
                cont.resume(Result.failure(exception))
            }
        }
    }

}