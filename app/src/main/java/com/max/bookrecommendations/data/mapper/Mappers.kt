package com.max.bookrecommendations.data.mapper

import com.max.bookrecommendations.data.local.PostEntity
import com.max.bookrecommendations.data.local.UserEntity
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.data.model.User

fun UserEntity.toUser(): User = User(
    uid = uid,
    email = email,
    displayName = displayName,
    profileImageUrl = profileImageUrl,
    lastUpdated = lastUpdated
)

fun User.toEntity(): UserEntity = UserEntity(
    uid = uid,
    email = email,
    displayName = displayName,
    profileImageUrl = profileImageUrl,
    lastUpdated = lastUpdated
)

fun PostEntity.toPost(): Post = Post(
    id = id,
    ownerUid = ownerUid,
    ownerName = ownerName,
    ownerProfileImageUrl = ownerProfileImageUrl,
    bookTitle = bookTitle,
    bookAuthor = bookAuthor,
    description = description,
    bookThumbnailUrl = bookThumbnailUrl,
    customImageUrl = customImageUrl,
    createdAt = createdAt,
    lastUpdated = lastUpdated
)

fun Post.toEntity(): PostEntity = PostEntity(
    id = id,
    ownerUid = ownerUid,
    ownerName = ownerName,
    ownerProfileImageUrl = ownerProfileImageUrl,
    bookTitle = bookTitle,
    bookAuthor = bookAuthor,
    description = description,
    bookThumbnailUrl = bookThumbnailUrl,
    customImageUrl = customImageUrl,
    createdAt = createdAt,
    lastUpdated = lastUpdated
)