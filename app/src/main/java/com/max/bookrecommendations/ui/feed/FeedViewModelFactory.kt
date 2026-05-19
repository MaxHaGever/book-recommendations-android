package com.max.bookrecommendations.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.max.bookrecommendations.data.repository.PostRepository

class FeedViewModelFactory(
    private val postRepository: PostRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(postRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
