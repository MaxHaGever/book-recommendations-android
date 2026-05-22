package com.max.bookrecommendations.ui.postdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.repository.PostRepository
import kotlinx.coroutines.launch

class PostDetailsViewModel(
    private val postRepository: PostRepository,
    private val authRemoteDataSource: AuthRemoteDataSource
) : ViewModel() {

    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post> = _post

    private val _isOwner = MutableLiveData<Boolean>()
    val isOwner: LiveData<Boolean> = _isOwner

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    fun loadPost(postId: String) {
        _isLoading.value = true

        postRepository.getPostByIdFromRemote(
            postId = postId,
            onSuccess = { remotePost ->
                _isLoading.value = false
                _post.value = remotePost
                updateOwnership(remotePost)

                viewModelScope.launch {
                    postRepository.savePost(remotePost)
                }
            },
            onFailure = { exception ->
                _isLoading.value = false
                _errorMessage.value = exception.message ?: "Failed to load post"
            }
        )
    }

    fun deletePost(post: Post) {
        val currentUserId = authRemoteDataSource.getCurrentUserId()

        if (post.ownerUid != currentUserId) {
            _errorMessage.value = "You can delete only your own posts"
            return
        }

        _isLoading.value = true

        postRepository.deletePostFromRemote(
            postId = post.id,
            onSuccess = {
                viewModelScope.launch {
                    postRepository.deletePost(post.id)

                    _isLoading.value = false
                    _deleteSuccess.value = true
                }
            },
            onFailure = { exception ->
                _isLoading.value = false
                _errorMessage.value = exception.message ?: "Failed to delete post"
            }
        )
    }

    private fun updateOwnership(post: Post) {
        val currentUserId = authRemoteDataSource.getCurrentUserId()
        _isOwner.value = post.ownerUid == currentUserId
    }
}
