package com.max.bookrecommendations.ui.createeditpost

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.max.bookrecommendations.data.AppContainer

class CreateEditPostViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateEditPostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateEditPostViewModel(
                postRepository = AppContainer.postRepository(context),
                authRemoteDataSource = AppContainer.authRemoteDataSource(),
                storageRemoteDataSource = AppContainer.storageRemoteDataSource()
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
