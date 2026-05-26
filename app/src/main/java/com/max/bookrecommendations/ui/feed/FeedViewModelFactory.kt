package com.max.bookrecommendations.ui.feed

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.max.bookrecommendations.data.AppContainer

class FeedViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(
                AppContainer.postRepository(context)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
