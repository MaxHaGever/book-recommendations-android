package com.max.bookrecommendations.data.repository

import android.content.Context
import com.max.bookrecommendations.data.local.CachedImageDao
import com.max.bookrecommendations.data.local.CachedImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest

class ImageCacheRepository(
    private val context: Context,
    private val cachedImageDao: CachedImageDao
) {

    private val client = OkHttpClient()

    suspend fun getOrCacheImage(
        remoteUrl: String,
        sourceLastUpdated: Long? = null
    ): File? {
        return withContext(Dispatchers.IO) {
            val cachedImage = cachedImageDao.getCachedImage(remoteUrl)
            var cachedFile: File? = null

            if (cachedImage != null) {
                cachedFile = File(cachedImage.localPath)

                val isCachedFileFresh =
                    sourceLastUpdated == null || cachedImage.lastUpdated >= sourceLastUpdated

                if (cachedFile.exists() && isCachedFileFresh) {
                    return@withContext cachedFile
                }

                if (!cachedFile.exists()) {
                    cachedImageDao.deleteCachedImage(remoteUrl)
                    cachedFile = null
                }
            }

            downloadAndCacheImage(
                remoteUrl = remoteUrl,
                sourceLastUpdated = sourceLastUpdated
            ) ?: cachedFile
        }
    }

    private suspend fun downloadAndCacheImage(
        remoteUrl: String,
        sourceLastUpdated: Long?
    ): File? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(remoteUrl)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext null
                    }

                    val imageBytes = response.body?.bytes()

                    if (imageBytes == null) {
                        return@withContext null
                    }

                    val cacheDirectory = File(context.filesDir, "post_image_cache")

                    if (!cacheDirectory.exists()) {
                        cacheDirectory.mkdirs()
                    }

                    val fileName = createFileName(remoteUrl)
                    val imageFile = File(cacheDirectory, fileName)

                    imageFile.writeBytes(imageBytes)

                    cachedImageDao.insertCachedImage(
                        CachedImageEntity(
                            remoteUrl = remoteUrl,
                            localPath = imageFile.absolutePath,
                            lastUpdated = sourceLastUpdated ?: System.currentTimeMillis()
                        )
                    )

                    imageFile
                }
            } catch (exception: Exception) {
                null
            }
        }
    }

    private fun createFileName(remoteUrl: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(remoteUrl.toByteArray())

        return hashBytes.joinToString("") { byte ->
            "%02x".format(byte)
        } + ".img"
    }
}
