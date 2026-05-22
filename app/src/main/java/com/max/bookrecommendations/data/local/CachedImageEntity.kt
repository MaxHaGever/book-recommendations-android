package com.max.bookrecommendations.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_images")
data class CachedImageEntity(
    @PrimaryKey val remoteUrl: String,
    val localPath: String,
    val lastUpdated: Long
)
