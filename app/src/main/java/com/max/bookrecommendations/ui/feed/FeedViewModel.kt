package com.max.bookrecommendations.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.data.repository.PostRepository
import kotlinx.coroutines.launch

class FeedViewModel(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadFeed() {
        viewModelScope.launch {
            _isLoading.value = true

            val cachedPosts = postRepository.getCachedPosts()
            _posts.value = cachedPosts

            postRepository.refreshPostsFromRemote(
                onSuccess = { remotePosts ->
                    viewModelScope.launch {
                        postRepository.savePosts(remotePosts)
                        _posts.value = remotePosts
                        _isLoading.value = false
                    }
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message
                    _isLoading.value = false
                }
            )
        }
    }
}
