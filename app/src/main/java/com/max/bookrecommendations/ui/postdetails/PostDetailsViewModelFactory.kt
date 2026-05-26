package com.max.bookrecommendations.ui.postdetails

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.max.bookrecommendations.data.AppContainer

class PostDetailsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostDetailsViewModel(
                postRepository = AppContainer.postRepository(context),
                authRemoteDataSource = AppContainer.authRemoteDataSource()
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
