package com.max.bookrecommendations.ui.common

import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.data.repository.ImageCacheRepository
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.io.File

class PostImageLoader(
    private val imageCacheRepository: ImageCacheRepository
) {

    fun load(
        lifecycleOwner: LifecycleOwner,
        post: Post,
        imageView: ImageView,
        onDefaultImage: (() -> Unit)? = null,
        onRealImage: (() -> Unit)? = null
    ) {
        val imageUrl = post.customImageUrl ?: post.bookThumbnailUrl

        Picasso.get().cancelRequest(imageView)
        onDefaultImage?.invoke()
        imageView.setImageResource(R.drawable.default_book)

        if (imageUrl.isNullOrEmpty()) {
            return
        }

        onRealImage?.invoke()
        imageView.tag = imageUrl

        lifecycleOwner.lifecycleScope.launch {
            val cachedImageFile = imageCacheRepository.getOrCacheImage(
                remoteUrl = imageUrl,
                sourceLastUpdated = post.lastUpdated
            )

            if (imageView.tag != imageUrl) {
                return@launch
            }

            if (cachedImageFile != null) {
                loadFile(cachedImageFile, imageView)
            } else {
                loadUrl(imageUrl, imageView)
            }
        }
    }

    private fun loadFile(file: File, imageView: ImageView) {
        Picasso.get()
            .load(file)
            .placeholder(R.drawable.default_book)
            .error(R.drawable.default_book)
            .into(imageView)
    }

    private fun loadUrl(url: String, imageView: ImageView) {
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.default_book)
            .error(R.drawable.default_book)
            .into(imageView)
    }
}
