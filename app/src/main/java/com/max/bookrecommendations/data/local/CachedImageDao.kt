package com.max.bookrecommendations.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedImageDao {

    @Query("SELECT * FROM cached_images WHERE remoteUrl = :remoteUrl")
    suspend fun getCachedImage(remoteUrl: String): CachedImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedImage(cachedImage: CachedImageEntity)

    @Query("DELETE FROM cached_images WHERE remoteUrl = :remoteUrl")
    suspend fun deleteCachedImage(remoteUrl: String)
}
