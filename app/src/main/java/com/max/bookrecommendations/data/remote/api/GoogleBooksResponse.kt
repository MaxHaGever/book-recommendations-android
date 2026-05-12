package com.max.bookrecommendations.data.remote.api

data class GoogleBooksResponse(
    val items: List<GoogleBookItem>?
)

data class GoogleBookItem(
    val id: String?,
    val volumeInfo: GoogleVolumeInfo?
)

data class GoogleVolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val description: String?,
    val imageLinks: GoogleImageLinks?
)

data class GoogleImageLinks(
    val thumbnail: String?
)