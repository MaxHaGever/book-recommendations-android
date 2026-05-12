package com.max.bookrecommendations.data.model

data class BookSearchItem(
    val googleBookId: String,
    val title: String,
    val authors: String,
    val thumbnailUrl: String?,
    val description: String?
)