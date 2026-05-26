package com.max.bookrecommendations.data

import android.content.Context
import com.max.bookrecommendations.data.local.DatabaseProvider
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.remote.StorageRemoteDataSource
import com.max.bookrecommendations.data.repository.ImageCacheRepository
import com.max.bookrecommendations.data.repository.PostRepository
import com.max.bookrecommendations.ui.common.PostImageLoader

object AppContainer {

    fun postRepository(context: Context): PostRepository {
        val database = DatabaseProvider.getDatabase(context.applicationContext)
        return PostRepository(database.postDao())
    }

    fun imageCacheRepository(context: Context): ImageCacheRepository {
        val appContext = context.applicationContext
        val database = DatabaseProvider.getDatabase(appContext)

        return ImageCacheRepository(
            context = appContext,
            cachedImageDao = database.cachedImageDao()
        )
    }

    fun postImageLoader(context: Context): PostImageLoader {
        return PostImageLoader(
            imageCacheRepository = imageCacheRepository(context)
        )
    }

    fun authRemoteDataSource(): AuthRemoteDataSource {
        return AuthRemoteDataSource()
    }

    fun storageRemoteDataSource(): StorageRemoteDataSource {
        return StorageRemoteDataSource()
    }
}
