package io.github.firebase_storage

import cocoapods.FirebaseStorage.FIRStorage
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.lastPathComponent
import platform.Foundation.pathExtension
import platform.Foundation.stringByAppendingPathComponent
import platform.posix.memcpy
import kotlin.coroutines.resume

actual class KFirebaseStorage {

    private val storage = FIRStorage.storage()

    actual suspend fun uploadFile(
        filePath: String,
        fileData: ByteArray,
        // Returning file URL and file path
    ): Result<Pair<String?, String>> {
        return suspendCancellableCoroutine { cont ->

            val storageRef = storage.reference().child(filePath)
            fileData.usePinned { pinned ->
                val data =
                    NSData.create(bytes = pinned.addressOf(0), length = fileData.size.toULong())

                storageRef.putData(data, null) { _, error ->
                    if (error != null) {
                        cont.resumeWith(Result.failure(Exception(error.localizedDescription)))
                    } else {
                        storageRef.downloadURLWithCompletion { url, errorD ->
                            if (errorD != null) {
                                cont.resumeWith(Result.failure(Exception(errorD.localizedDescription)))
                            } else {
                                cont.resume(Result.success(Pair(url!!.absoluteString, filePath)))
                            }
                        }
                    }
                }
            }
        }

    }

    actual suspend fun downloadFile(
        filePath: String,
    ): Result<KFirebaseStorageDownloadedFile?> {
        return suspendCancellableCoroutine { cont ->

            val storageRef = storage.reference().child(filePath)

            // Extract file name and extension from file path
            val fileName = (filePath as NSString).lastPathComponent
            val fileExtension = (filePath as NSString).pathExtension

            // Download the file into a temporary location
            val tempFilePath = NSString.create(string = NSTemporaryDirectory())
                .stringByAppendingPathComponent(fileName)
            val tempFile = NSURL.fileURLWithPath(tempFilePath)

            storageRef.writeToFile(tempFile) { _, error: NSError? ->
                if (error != null) {
                    cont.resume(Result.failure(Exception(error.localizedDescription)))
                } else {
                    // Read the file data as ByteArray
                    val fileData = NSData.dataWithContentsOfFile(tempFilePath)
                    if (fileData != null) {
                        val downloadedFile = KFirebaseStorageDownloadedFile(
                            fileName = fileName,
                            fileExtension = fileExtension,
                            fileBytes = fileData.toByteArray()
                        )
                        cont.resume(Result.success(downloadedFile))
                    } else {
                        cont.resume(Result.failure(Exception("Failed to read downloaded file.")))
                    }
                }
            }
        }
    }


    actual suspend fun deleteFile(
        filePath: String,
    ): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->

            val storageRef = storage.reference().child(filePath)

            storageRef.deleteWithCompletion { error ->
                if (error != null) {
                    cont.resume(Result.failure(Exception(error.localizedDescription)))
                } else {
                    cont.resume(Result.success(true))
                }
            }
        }
    }
}

// Helper extension function to convert NSData to ByteArray
fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()
    val byteArray = ByteArray(size)
    memcpy(byteArray.refTo(0), this.bytes, size.convert())
    return byteArray
}
