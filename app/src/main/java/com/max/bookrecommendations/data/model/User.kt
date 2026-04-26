package com.max.bookrecommendations.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val profileImageUrl: String? = null,
    val lastUpdated: Long? = null,
)