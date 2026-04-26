package com.max.bookrecommendations.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val displayName: String,
    val profileImageUrl: String?,
    val lastUpdated: Long?
)