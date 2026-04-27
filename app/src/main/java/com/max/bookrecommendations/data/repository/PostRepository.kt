package com.max.bookrecommendations.data.repository

import com.max.bookrecommendations.data.local.PostDao
import com.max.bookrecommendations.data.mapper.toEntity
import com.max.bookrecommendations.data.mapper.toPost
import com.max.bookrecommendations.data.model.Post

class PostRepository(
    private val postDao: PostDao
) {

    suspend fun getAllPosts(): List<Post> {
        return postDao.getAllPosts().map { it.toPost() }
    }

    suspend fun getPostById(postId: String): Post? {
        return postDao.getPostById(postId)?.toPost()
    }

    suspend fun getPostsByOwner(ownerUid: String): List<Post> {
        return postDao.getPostsByOwner(ownerUid).map { it.toPost() }
    }

    suspend fun savePost(post: Post) {
        postDao.insertPost(post.toEntity())
    }

    suspend fun savePosts(posts: List<Post>) {
        postDao.insertPosts(posts.map { it.toEntity() })
    }

    suspend fun deletePost(postId: String) {
        postDao.deletePostById(postId)
    }

    suspend fun deleteAllPosts() {
        postDao.deleteAllPosts()
    }
}