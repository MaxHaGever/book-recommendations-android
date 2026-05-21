package com.max.bookrecommendations.data.repository

import com.max.bookrecommendations.data.local.PostDao
import com.max.bookrecommendations.data.mapper.toEntity
import com.max.bookrecommendations.data.mapper.toPost
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.data.remote.PostRemoteDataSource

class PostRepository(
    private val postDao: PostDao,
    private val postRemoteDataSource: PostRemoteDataSource = PostRemoteDataSource()
) {

    suspend fun getCachedPosts(): List<Post> {
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

    fun refreshPostsFromRemote(
        onSuccess: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        postRemoteDataSource.getAllPosts(
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun deletePostFromRemote(
        postId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        postRemoteDataSource.deletePost(
            postId = postId,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}
