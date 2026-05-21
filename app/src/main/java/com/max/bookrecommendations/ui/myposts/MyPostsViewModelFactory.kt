package com.max.bookrecommendations.ui.myposts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.max.bookrecommendations.data.repository.PostRepository

class MyPostsViewModelFactory(
    private val postRepository: PostRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPostsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyPostsViewModel(postRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}