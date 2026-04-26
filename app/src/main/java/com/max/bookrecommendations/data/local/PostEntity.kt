package com.max.bookrecommendations.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val ownerUid: String,
    val ownerName: String,
    val ownerProfileImageUrl: String?,
    val bookTitle: String,
    val bookAuthor: String,
    val description: String,
    val bookThumbnailUrl: String?,
    val customImageUrl: String?,
    val createdAt: Long,
    val lastUpdated: Long?
)