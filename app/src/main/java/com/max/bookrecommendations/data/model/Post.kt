package com.max.bookrecommendations.data.model

data class Post(
    val id: String = "",
    val ownerUid: String = "",
    val ownerName: String = "",
    val ownerProfileImageUrl: String? = null,
    val bookTitle: String = "",
    val bookAuthor: String = "",
    val description: String = "",
    val bookThumbnailUrl: String? = null,
    val customImageUrl: String? = null,
    val createdAt: Long = 0L,
    val lastUpdated: Long? = null
)