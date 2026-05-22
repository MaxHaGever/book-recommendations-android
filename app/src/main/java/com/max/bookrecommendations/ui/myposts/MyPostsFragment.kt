package com.max.bookrecommendations.ui.myposts

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.local.DatabaseProvider
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.repository.ImageCacheRepository
import com.max.bookrecommendations.data.repository.PostRepository
import com.max.bookrecommendations.ui.feed.PostAdapter
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class MyPostsFragment : Fragment(R.layout.fragment_my_posts) {

    private lateinit var myPostsViewModel: MyPostsViewModel
    private lateinit var postAdapter: PostAdapter
    private lateinit var imageCacheRepository: ImageCacheRepository

    private val authRemoteDataSource = AuthRemoteDataSource()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myPostsRecyclerView: RecyclerView = view.findViewById(R.id.myPostsRecyclerView)
        val myPostsProgressBar: ProgressBar = view.findViewById(R.id.myPostsProgressBar)
        val emptyMyPostsTextView: TextView = view.findViewById(R.id.emptyMyPostsTextView)
        val profileButton: MaterialButton = view.findViewById(R.id.myPostsProfileButton)

        profileButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val database = DatabaseProvider.getDatabase(requireContext())
        val postRepository = PostRepository(database.postDao())

        imageCacheRepository = ImageCacheRepository(
            context = requireContext().applicationContext,
            cachedImageDao = database.cachedImageDao()
        )

        postAdapter = PostAdapter(
            posts = mutableListOf(),
            onPostClick = { post ->
                val action =
                    MyPostsFragmentDirections.actionMyPostsFragmentToPostDetailsFragment(post.id)

                findNavController().navigate(action)
            },
            onLoadPostImage = { post, imageView ->
                loadCachedPostImage(post, imageView)
            }
        )

        myPostsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        myPostsRecyclerView.adapter = postAdapter

        val factory = MyPostsViewModelFactory(postRepository)
        myPostsViewModel = ViewModelProvider(this, factory)[MyPostsViewModel::class.java]

        myPostsViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitPosts(posts)

            emptyMyPostsTextView.visibility =
                if (posts.isEmpty()) View.VISIBLE else View.GONE
        }

        myPostsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            myPostsProgressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

        myPostsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        val currentUserId = authRemoteDataSource.getCurrentUserId()

        if (currentUserId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        myPostsViewModel.loadMyPosts(currentUserId)
    }

    private fun loadCachedPostImage(post: Post, imageView: ImageView) {
        val imageUrl = post.customImageUrl ?: post.bookThumbnailUrl

        if (imageUrl.isNullOrEmpty()) {
            return
        }

        imageView.tag = imageUrl

        viewLifecycleOwner.lifecycleScope.launch {
            val cachedImageFile = imageCacheRepository.getOrCacheImage(
                remoteUrl = imageUrl,
                sourceLastUpdated = post.lastUpdated
            )

            if (imageView.tag != imageUrl) {
                return@launch
            }

            if (cachedImageFile != null) {
                Picasso.get()
                    .load(cachedImageFile)
                    .placeholder(R.drawable.default_book)
                    .error(R.drawable.default_book)
                    .into(imageView)
            } else {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.default_book)
                    .error(R.drawable.default_book)
                    .into(imageView)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val currentUserId = authRemoteDataSource.getCurrentUserId()

        if (::myPostsViewModel.isInitialized && currentUserId != null) {
            myPostsViewModel.loadMyPosts(currentUserId)
        }
    }
}
