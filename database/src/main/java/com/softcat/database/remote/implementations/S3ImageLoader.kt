package com.softcat.database.remote.implementations

import android.app.Application
import android.net.Uri
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.softcat.database.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class S3ImageLoader @Inject constructor(
    private val application: Application
) {

    private val credentials = BasicAWSCredentials(
        BuildConfig.S3_ACCESS_KEY,
        BuildConfig.S3_SECRET_KEY
    )
    private val s3Client = AmazonS3Client(credentials).apply {
        setEndpoint(BuildConfig.S3_ENDPOINT)
    }

    private fun getFileSize(uri: Uri): Long {
        application.contentResolver.openFileDescriptor(uri, "r").use { descriptor ->
            descriptor?.statSize?.let { return it }
        }
        return 0L
    }

    fun uploadImageToS3(uri: Uri, imageId: String): String {
        val size = getFileSize(uri)
        val inputStream = application.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open input stream.")
        val fileName = getImageFileName(imageId)
        val metadata = ObjectMetadata().apply {
            contentType = "image/jpeg"
            contentLength = size
        }
        inputStream.use {
            s3Client.putObject(
                BuildConfig.BUCKET_NAME,
                fileName,
                inputStream,
                metadata
            )
        }
        return "${BuildConfig.S3_ENDPOINT}/${BuildConfig.BUCKET_NAME}/$fileName"
    }

    suspend fun deleteImageFromS3(imageId: String) = withContext(Dispatchers.IO) {
        val fileName = getImageFileName(imageId)
        s3Client.deleteObject(
            BuildConfig.BUCKET_NAME,
            fileName
        )
    }

    private fun getImageFileName(imageId: String) = "images/$imageId.jpg"
}