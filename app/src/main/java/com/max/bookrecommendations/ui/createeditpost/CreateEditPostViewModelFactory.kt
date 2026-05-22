package com.max.bookrecommendations.ui.createeditpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.remote.StorageRemoteDataSource
import com.max.bookrecommendations.data.repository.PostRepository

class CreateEditPostViewModelFactory(
    private val postRepository: PostRepository,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val storageRemoteDataSource: StorageRemoteDataSource
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateEditPostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateEditPostViewModel(
                postRepository = postRepository,
                authRemoteDataSource = authRemoteDataSource,
                storageRemoteDataSource = storageRemoteDataSource
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
