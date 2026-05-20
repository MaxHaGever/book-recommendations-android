package com.max.bookrecommendations.ui.myposts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.data.repository.PostRepository
import kotlinx.coroutines.launch

class MyPostsViewModel(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadMyPosts(ownerUid: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val myPosts = postRepository.getPostsByOwner(ownerUid)
                _posts.value = myPosts
            } catch (exception: Exception) {
                _errorMessage.value = exception.message
            }

            _isLoading.value = false
        }
    }
}
