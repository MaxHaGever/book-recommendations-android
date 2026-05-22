package com.max.bookrecommendations.ui.postdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.repository.PostRepository

class PostDetailsViewModelFactory(
    private val postRepository: PostRepository,
    private val authRemoteDataSource: AuthRemoteDataSource
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostDetailsViewModel(
                postRepository = postRepository,
                authRemoteDataSource = authRemoteDataSource
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
